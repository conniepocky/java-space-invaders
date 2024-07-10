import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener{

    class Block {
        int x, y;
        int width,height;
        Image img;
        boolean alive = true; // for aliens
        boolean used = false; // for bullets

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int tileSize = 32;
    int rows = 16;
    int cols = 16;
    int boardWidth = tileSize * cols;
    int boardHeight = tileSize * rows;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    //ship 

    int shipWidth = tileSize*2;
    int shipHeight = tileSize;
    int shipX = tileSize*cols/2 - tileSize;
    int shipY = boardHeight - tileSize*2;
    int shipVelocityX = tileSize;
    Block ship;

    //aliens

    ArrayList<Block> alienArray;
    int alienWidth = tileSize*2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0;
    int alienVelocityX = 1;

    //bullets

    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize/8;
    int bulletHeight = tileSize/2;
    int bulletVelocityY = -10;

    // game info

    Timer gameLoop;
    int score = 0;
    boolean gameOver = false;

    SpaceInvaders() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        shipImg = new ImageIcon(getClass().getResource("./ship.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();
    
        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();

        gameLoop = new Timer(1000/60, this);

        createAliens();

        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        //ship

        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);
    
        //aliens

        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        //bullets

        g.setColor(Color.white);
        for (int i = 0; i < bulletArray.size(); i ++) {
            Block bullet = bulletArray.get(i);
            if (!bullet.used) {
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
        
            }
        }

        //score

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 20));

        if (gameOver) {
            g.drawString("GAME OVER: " + String.valueOf(score), 10, 35);
        } else {
            g.drawString(String.valueOf(score), 10, 35);  
        }


    }

    public void createAliens() {
        Random random = new Random();
        for (int r = 0; r < alienRows; r++) {
            for (int c = 0; c < alienColumns; c++) {
                int alienType = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                    alienX + c*alienWidth, 
                    alienY + r*alienHeight,
                    alienWidth,
                    alienHeight,
                    alienImgArray.get(alienType));

                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    public void move() {

        for (int i = 0; i < alienArray.size(); i ++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                alien.x += alienVelocityX;

                if (alien.x + alien.width >= boardWidth || alien.x <= 0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX*2;

                    for (int f = 0; f < alienArray.size(); f++) {
                        alienArray.get(f).y += alien.height;
                    }
                }
            }

            if (alien.y >= ship.y) {
                gameOver = true;
            }
        }

        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;

            for (int f=0; f < alienArray.size(); f++) {
                Block alien = alienArray.get(f);
                if (detectCollision(bullet, alien) && alien.alive && !bullet.used) {
                    alien.alive = false;
                    bullet.used = true;
                    alienCount--;
                    score += 100;
                }
            }
        }

        while (bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
            bulletArray.remove(0);
        }

        if (alienCount == 0) { // next level if all aliens destroyed
            alienColumns = Math.min(alienColumns + 1, cols/2 - 2);
            alienRows = Math.min(alienRows + 1, rows - 6);

            alienArray.clear();
            bulletArray.clear();

            createAliens();

            score += alienColumns * alienRows * 100; 
        }
    }

    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {

        if (gameOver) {
            ship.x = shipX;
            alienArray.clear();
            bulletArray.clear();
            score = 0;
            alienVelocityX = 1;
            alienColumns = 3;
            alienRows = 2;
            gameOver = false;
            createAliens();
        }

        if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && ship.x - shipVelocityX >= 0) {
            ship.x -= shipVelocityX;
        } else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && ship.x + ship.width + shipVelocityX <= boardWidth) {
            ship.x += shipVelocityX;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            Block bullet = new Block(
                ship.x + shipWidth*15/32,
                ship.y,
                bulletWidth,
                bulletHeight,
                null);

            bulletArray.add(bullet);
            
        }
    }
}
