import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.Timer;

/**
 *
 * @author 
 */
public class RealSnake extends JComponent implements ActionListener {

    // Height and Width of our game
    //make sure they are the exact same (square)

    //making screen size adjustable would make my life so hard so no
    static final int WIDTH = 800;
    static final int HEIGHT = 800;

    //Title of the window
    String title = "Snake";

    // sets the framerate and delay for our game
    // this calculates the number of milliseconds per frame
    // you just need to select an appropriate framerate
    
    //good speed :D
    int desiredFPS = 10;
    
    //time it takes to loop
    int desiredTime = Math.round((1000 / desiredFPS));
    
    // timer used to run the game loop
    // this is what keeps our time running smoothly :)
    Timer gameTimer;

    // YOUR GAME VARIABLES WOULD GO HERE
    
    //game dimensions - you can change tile size as long as it's divisible in the game area
    static final int borderWidth = 50;
    int tileSize = 50;
    int gridWidth = WIDTH - (2 * borderWidth);
    int tileRowLength = gridWidth / tileSize;
    int totalTiles = tileRowLength * tileRowLength;

    //2D array of all the tiles just because i wanted to try out a 2d array
    Rectangle[][] tiles = new Rectangle[tileRowLength][tileRowLength];

    //array for all snake body parts so we can move all of them easily. index at 0 will represent head of snake
    //make size total tiles because thats the max size the snake can grow
    Rectangle[] snakeParts = new Rectangle[totalTiles];
    
    //the snake
    Rectangle snakeHead = new Rectangle(250, 450, tileSize, tileSize);
    
    //the apple 
    Rectangle apple = new Rectangle(450, 450, tileSize, tileSize);

    //variable to keep track of snake length, starts at 1 unit
    int snakeLength = 1;
    
    //apples eaten
    int applesEaten = 0;

    //record of apples consumed
    int highscore = 0;
    //tracks new highscore
    boolean newHighscore = false;

    //movement control 
    boolean up = false;
    boolean down = false;
    boolean right = false;
    boolean left = false;

    //ALL COLORS
    Color lightTile = new Color(213, 245, 245);
    Color darkTile = new Color(189, 222, 222);
    Color background = new Color(217, 217, 219);
    Color borderColor = new Color(50, 50, 50);
    Color snakeHeadCol = new Color(0, 255, 21);
    Color snakeBody = new Color(0, 201, 17);
    Color appleCol = new Color(255, 0, 76);
    Color hovering = new Color(135, 213, 255);
    Color button = new Color(129, 192, 247);
    Color partyFalse = new Color(255, 79, 138);
    Color partyTrue = new Color(84, 255, 175);

    //pause colors
    Color lightTilePause = new Color(233, 245, 245);
    Color darkTilePause = new Color(215, 222, 222);
    Color snakeHeadColPause = new Color(199, 255, 203);
    Color snakeBodyPause = new Color(170, 191, 172);
    Color appleColPause = new Color(255, 204, 219);

    boolean tileColorSwitch = false;

    //FONTS
    Font score = new Font("Arial", Font.BOLD, borderWidth / 2);
    Font big = new Font("Dialog", Font.BOLD, 30);
    Font titleFont = new Font("Monospaced", Font.BOLD, 50);
    Font smallTitleFont = new Font("Monospaced", Font.BOLD, 40);
    Font little = new Font("Dialog", Font.BOLD, 15);

    //use a variable to keep track of the direction of the snake. Make it nothing initially so the snake can actually move first
    char lastDirection;

    boolean gameOver = false;
    boolean mainMenu = true;
    boolean gameScreen = false;
    boolean pauseScreen = false;

    boolean isParty = false;

    //buttons
    Rectangle restart = new Rectangle(300, 500, 200, 100);
    Rectangle play = new Rectangle(275, 300, 250, 125);
    Rectangle gameScreenHome = new Rectangle(750, 0, 50, 50);
    Rectangle party = new Rectangle(325, 600, 150, 100);

    //mouse
    int mouseX, mouseY = 0;
    int mouseSize = 10;
    
    // GAME VARIABLES END HERE    

    // Constructor to create the Frame and place the panel in
    // You will learn more about this in Grade 12 :)
    public RealSnake(){
        // creates a windows to show my game
        JFrame frame = new JFrame(title);

        // sets the size of my game
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // adds the game to the window
        frame.add(this);

        // sets some options and size of the window automatically
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // shows the window to the user
        frame.setVisible(true);

        // add listeners for keyboard and mouse
        frame.addKeyListener(new Keyboard());
        Mouse m = new Mouse();
        this.addMouseMotionListener(m);
        this.addMouseWheelListener(m);
        this.addMouseListener(m);
        

        // Set things up for the game at startup

        setup();


       // Start the game loop
        gameTimer = new Timer(desiredTime,this);
        gameTimer.setRepeats(true);
        gameTimer.start();
    }

    // drawing of the game happens in here
    // we use the Graphics object, g, to perform the drawing
    // NOTE: This is already double buffered!(helps with framerate/speed)
    @Override
    public void paintComponent(Graphics g) {

        // always clear the screen first!
        g.clearRect(0, 0, WIDTH, HEIGHT);

        // GAME DRAWING GOES HERE

        if(mainMenu) {
            //draw the background
            g.setColor(lightTile);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            //draw the border
            g.setColor(borderColor); 
            g.fillRect(0, 0, borderWidth, HEIGHT);
            g.fillRect(0, 0, WIDTH, borderWidth);
            g.fillRect(WIDTH - borderWidth, 0, borderWidth, HEIGHT);
            g.fillRect(0, HEIGHT - borderWidth, WIDTH, borderWidth);

            //draw the buttons
            g.setColor(button);

            //check if we are on the button
            if(mouseCollides(play.x, play.y, play.width, play.height)) {
                //set the hover color
                g.setColor(hovering);
            }

            g.fillRect(play.x, play.y, play.width, play.height);

            //party button
            if(isParty) {
                g.setColor(partyTrue);
            }
            else if(!isParty) {
                g.setColor(partyFalse);
            }

            g.fillRect(party.x, party.y, party.width, party.height);
        

            //draw the title text
            g.setColor(Color.BLACK);
            g.setFont(titleFont);
            g.drawString("REAL SNAKE GAME", 180, 200);

            //draw the button text
            g.drawString("PLAY", play.x + 60, play.y + 75);

            g.setFont(smallTitleFont);
            g.drawString("Party", party.x + 15, party.y + 60);


        }
        //game finishes when they either win or lose, this is the end screen
        else if(gameOver) {
            //draw the background
            g.setColor(lightTile);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            //draw the border
            g.setColor(borderColor); 
            g.fillRect(0, 0, borderWidth, HEIGHT);
            g.fillRect(0, 0, WIDTH, borderWidth);
            g.fillRect(WIDTH - borderWidth, 0, borderWidth, HEIGHT);
            g.fillRect(0, HEIGHT - borderWidth, WIDTH, borderWidth);

            //draw the stats
            g.setColor(Color.BLACK);
            g.setFont(big);

            //length
            g.drawString("Snake Length: " + snakeLength, 280, 250);

            //score
            g.drawString("Score: " + applesEaten, 340, 300);

            //highscore

            if(newHighscore) {
                g.setColor(randomCol());
                g.drawString("NEW Highscore: " + highscore, 280, 350);
            }
            else {
                g.drawString("Highscore: " + highscore, 300, 350);
            }
            

            g.setColor(button);
            //if the mouse collides with the restart button highlight it
            if(mouseCollides(restart.x, restart.y, restart.width, restart.height)) {
                g.setColor(hovering);
            }

            g.fillRect(restart.x, restart.y, restart.width, restart.height);

            //draw the text
            g.setColor(Color.BLACK);
            g.setFont(big);
            g.drawString("Play Again?", restart.x + 15, restart.y + 55);

        }

        else if(gameScreen) {
    
            //draw a border 
            g.setColor(borderColor);
            g.fillRect(0, 0, borderWidth, HEIGHT);
            g.fillRect(0, 0, WIDTH, borderWidth);
            g.fillRect(WIDTH - borderWidth, 0, borderWidth, HEIGHT);
            g.fillRect(0, HEIGHT - borderWidth, WIDTH, borderWidth);

            //draw the grid
            //Color newCol = randomCol();
            //for loop for columns
            for(int x = borderWidth, c = 0; x < WIDTH - borderWidth; x += tileSize, c++) {
                //switch the starting color every time this loop iterates
                if(c % 2 == 0) {
                    tileColorSwitch = true;
                }
                else {
                    tileColorSwitch = false;
                }

                //make new colors for every column so i don't have a seizure that bad
                //Color newCol = randomCol();
                //Color newCol2 = randomCol();
                //for loop for rows
                for(int y = borderWidth, r = 0; y < HEIGHT - borderWidth; y = y + tileSize, r++) {
                    //light tile color
                    Color newCol = randomCol();
                    if(tileColorSwitch) {
                        if(isParty) {
                            g.setColor(newCol);
                        }  
                        else {
                            g.setColor(lightTile);
                        }                    
                        g.fillRect(x, y, tileSize, tileSize);
                        tileColorSwitch = false;
                    }
                    //dark tile color
                    else {
                        if(isParty) {
                            g.setColor(newCol);
                        }
                        else {
                            g.setColor(darkTile);
                        }
                        g.fillRect(x, y, tileSize, tileSize);
                        tileColorSwitch = true;
                    }

                    //store the the tiles into a 2D array
                    Rectangle rect = new Rectangle(x, y, tileSize, tileSize);
                    //index is column and row
                    tiles[c][r] = rect;
                }
            }

            //drawing the snake
            
            //draw every single body part of the snake
            for(int i = 0; i < snakeLength; i++) {
                //if the index is zero, then dealing with the snake head
                if(i == 0) {
                    //draw the head
                    g.setColor(snakeHeadCol);
                    g.fillRect(snakeParts[i].x, snakeParts[i].y, snakeParts[i].width, snakeParts[i].height);
                }
                else {
                    //draw the body parts
                    g.setColor(snakeBody);
                    g.fillRect(snakeParts[i].x, snakeParts[i].y, snakeParts[i].width, snakeParts[i].height);
                }
            }
            
            //draw the apple
    
            g.setColor(appleCol);
            g.fillOval(apple.x, apple.y, apple.width, apple.height);

            //draw the score
            g.setColor(Color.WHITE);
            g.setFont(score);
            g.drawString("LENGTH: " + snakeLength, (WIDTH / 2) - 50, (borderWidth / 2) + 10);

            //draw the highscore 
            g.drawString("HIGHSCORE: " + highscore, (WIDTH / 2) - 75, HEIGHT - (borderWidth / 2));
            
            //draw pause text
            g.drawString("Pause: P ", 5, 35);

            
        }

        else if(pauseScreen) {
            //draw a border 
            g.setColor(borderColor);
            g.fillRect(0, 0, borderWidth, HEIGHT);
            g.fillRect(0, 0, WIDTH, borderWidth);
            g.fillRect(WIDTH - borderWidth, 0, borderWidth, HEIGHT);
            g.fillRect(0, HEIGHT - borderWidth, WIDTH, borderWidth);

            //draw the grid

            //for loop for columns
            for(int x = borderWidth, c = 0; x < WIDTH - borderWidth; x += tileSize, c++) {
                //switch the starting color every time this loop iterates
                if(c % 2 == 0) {
                    tileColorSwitch = true;
                }
                else {
                    tileColorSwitch = false;
                }
                //for loop for rows
                for(int y = borderWidth; y < HEIGHT - borderWidth; y = y + tileSize) {
                    //draw the tiles for visuals for now

                    //light tile color
                    if(tileColorSwitch) {
                        g.setColor(lightTilePause);
                        g.fillRect(x, y, tileSize, tileSize);
                        tileColorSwitch = false;
                    }
                    //dark tile color
                    else {
                        g.setColor(darkTilePause);
                        g.fillRect(x, y, tileSize, tileSize);
                        tileColorSwitch = true;
                    }
                }
            }

            //draw the stationary snake and apple so player can see where they still are

            for(int i = 0; i < snakeLength; i++) {
                //if the index is zero, then dealing with the snake head
                if(i == 0) {
                    //draw the head
                    g.setColor(snakeHeadColPause);
                    g.fillRect(snakeParts[i].x, snakeParts[i].y, snakeParts[i].width, snakeParts[i].height);
                }
                else {
                    //draw the body parts
                    g.setColor(snakeBodyPause);
                    g.fillRect(snakeParts[i].x, snakeParts[i].y, snakeParts[i].width, snakeParts[i].height);
                }
            }
            
            //draw the apple
            g.setColor(appleColPause);
            g.fillOval(apple.x, apple.y, apple.width, apple.height);

            //draw the text now
            g.setColor(borderColor);
            g.setFont(big);
            g.drawString("GAME PAUSED", 290, 400);

            //home button
            g.setColor(button);

            if(mouseCollides(gameScreenHome.x, gameScreenHome.y, gameScreenHome.width, gameScreenHome.height)) {
                g.setColor(hovering);
            }
            g.fillRect(gameScreenHome.x, gameScreenHome.y, gameScreenHome.width, gameScreenHome.height);

            //draw text
            g.setColor(Color.BLACK);
            
            //draw house bottom
            g.drawRect(763, 25, 25, 25);

            //draw roof
            int[] xPoints = {755, 775, 795};
            int[] yPoints = {25, 10, 25};
            g.drawPolygon(xPoints, yPoints, 3);

            //draw door
            g.drawRect(770, 35, 10, 15);

            //this house is really ugly oml
            
        }

        // GAME DRAWING ENDS HERE
    }

    //methods
    
    public boolean snakeCollides() {
        //the boolean return variaible
        boolean collides;
        //check if the head of the snake is outside the border
        if(snakeParts[0].x < borderWidth || snakeParts[0].x + tileSize > WIDTH - borderWidth || snakeParts[0].y < borderWidth || snakeParts[0].y + tileSize > HEIGHT - borderWidth) {
            //if any of these are true then there is collision
            collides = true;
            //return this immediately, no need to check for other conditions
            return collides;
        }
        else {
            //false.. for now?
            collides = false;
        }

        //check if the head of the snake hits its on body by going through each snake part starting at index 1
        for(int i = 1; i < snakeLength; i++) {
            //if the head of the snake is at the same position of any of the body parts then collision is true
            if(snakeParts[0].x == snakeParts[i].x && snakeParts[0].y == snakeParts[i].y) {
                collides = true;
                //stop iterating the loop, collision is true
                break;
            }
            else {
                //still false, still alive!!!
                collides = false;
            }
        }
        //return the status
        return collides;
    }
    
    public void generateApple() {
        //going to need a random tile to spawn in, but it can't spawn in a tile with a snake body part
        int min = 0; 
        //minus one because indexes
        int max = tileRowLength - 1; 
        int columnRand = (int)(Math.random( )*(max-min+1));
        int rowRand = (int)(Math.random( )*(max-min+1));

        //set the x and y to a random tile
        apple.x = tiles[columnRand][rowRand].x;
        apple.y = tiles[columnRand][rowRand].y; 

        //scuffed solutiion here
        for(int i = 0; i < snakeLength; i++) {
            //if a snake body part is equal to the apple, just try again LOL
            if(snakeParts[i].x == apple.x && snakeParts[i].y == apple.y && snakeLength != totalTiles) {
                //what an efficient piece of code am i right
                generateApple();
            }
        }
    }   

    //mouse collision checking
    public boolean mouseCollides(int x, int y, int width, int height) {
        //do a lot of comparing
        if (mouseX < x + width && mouseX + mouseSize > x && mouseY < y + height && mouseY + mouseSize > y) {
            //the shpaes collide
            return true;
        }
        else {
            //the shapes do not collide
            return false;
        }
    }

    public void setupGame() {
        //reset everything to game screen
        gameOver = false;
        mainMenu = false;
        gameScreen = true;

        //reset snake and apple

        //define the snakehead 
        snakeParts[0] = snakeHead;
        //move back to starting pos
        snakeParts[0].x = 250;
        snakeParts[0].y = 450;
        snakeLength = 1;

        apple.x = 450;
        apple.y = 450;

        //reset the movement

        //set the last direction to nothing
        lastDirection = '-';

        //make it stop moving
        up = false;
        down = false;
        right = false;
        left = false;

        //things
        applesEaten = 0;
        
    }

    public void checkScore() {

        //highscore thing

        if (applesEaten > highscore) {
            //set the new highscore
            highscore = applesEaten;
            newHighscore = true;
        }
        else if (applesEaten < highscore) {
            //not a new highscore
            newHighscore = false;
        }
    }

    public Color randomCol() {

        //generate the random rgb values
        int min = 0; 
        int max = 255; 
        int R = (int)(Math.random( )*(max-min+1));
        int G = (int)(Math.random( )*(max-min+1));
        int B = (int)(Math.random( )*(max-min+1));
        Color randColor = new Color(R, G, B);

        return randColor;
    }

    // This method is used to do any pre-setup you might need to do
    // This is run before the game loop begins!
    public void setup() {
        // Any of your pre setup before the loop starts should go here
        

    }

    // The main game loop
    // In here is where all the logic for my game will go
    public void loop() {

        

        if(gameScreen) {

            //continue making each body part follow each other
            for(int i = snakeLength - 1; i > 0; i--) {
                //make the body part follow the previous body part
                snakeParts[i].x = snakeParts[i-1].x;
                snakeParts[i].y = snakeParts[i-1].y;
            }

            //how the head of the snake moves

            if(up) {
                snakeParts[0].y = snakeParts[0].y - tileSize;
                lastDirection = 'U';
            }
            if(down) {
                snakeParts[0].y = snakeParts[0].y + tileSize;
                lastDirection = 'D';
            }
            if(right) {
                snakeParts[0].x = snakeParts[0].x + tileSize;
                lastDirection = 'R';
            }
            if(left) {
                snakeParts[0].x = snakeParts[0].x - tileSize;
                lastDirection = 'L';
            }

            //collision
            if(snakeCollides()) {
                //game has been lost
                gameOver = true;
                //MAKE THE GAME SCREEN FALLLLSEEEE BROTHER
                gameScreen = false;
                //check for highscore
                checkScore();
            } 

            //apple is eaten
            if(snakeParts[0].intersects(apple) && snakeLength != totalTiles) {
                //new apple needs to be generated and the length needs to increase
                generateApple();
                snakeLength++;
                applesEaten++;
                
                //make a new rectangle at the previous body parts position
                snakeParts[snakeLength - 1] = new Rectangle(snakeParts[snakeLength - 2].x, snakeParts[snakeLength - 2].y, tileSize, tileSize);
            } 
        }
    }
    

    // Used to implement any of the Mouse Actions
    private class Mouse extends MouseAdapter {

        // if a mouse button has been pressed down
        @Override
        public void mousePressed(MouseEvent e) {
            //use this to make buttons and stuff

            //if we are on game over screen and hovering on the restart button when pressed do this
            if(mouseCollides(restart.x, restart.y, restart.width, restart.height) && gameOver) {
                //restart the game
                setupGame();
            }

            //if we are on main menu screen and hovering on play button start the game
            if(mouseCollides(play.x, play.y, play.width, play.height) && mainMenu) {
                //go to game screen
                setupGame();
            }

            if(mouseCollides(gameScreenHome.x, gameScreenHome.y, gameScreenHome.width, gameScreenHome.height) && pauseScreen) {
                //go to home screen
                pauseScreen = false;
                mainMenu = true;
            }

            if(mouseCollides(party.x, party.y, party.width, party.height) && mainMenu) {
                if(isParty) {
                    isParty = false;
                }
                else if(!isParty) {
                isParty = true;
                }
            }
            
        }

        // if a mouse button has been released
        @Override
        public void mouseReleased(MouseEvent e) {

        }

        // if the scroll wheel has been moved
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

        }

        // if the mouse has moved positions
        @Override
        public void mouseMoved(MouseEvent e) {
            //get the values of where the mouse is
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    // Used to implements any of the Keyboard Actions
    private class Keyboard extends KeyAdapter {

        // if a key has been pressed down
        @Override
        public void keyPressed(KeyEvent e) {
            //determine the key that is pressed
            int key = e.getKeyCode();
            //check if it's being pressed and were in the game screen

            if(gameScreen) {
                if(key == KeyEvent.VK_UP) {
                    //check direction first, but if we are only one snakelength then its okay we don't have to check this 
                    if(lastDirection == 'D' && snakeLength != 1) {
                        //keep going down then, you can't 180 in snake lol
                        down = true;
                        //make sure to keep everything else false aswell, to avoid bugs!! this took a little to figure out
                        up = false;
                        right = false;
                        left = false;
                    }
                    else {
                        //the usual case!
                        up = true;
                        //all other directions will be false now
                        down = false;
                        right = false; 
                        left = false;
                    }
                    
                }
                else if(key == KeyEvent.VK_DOWN) {
                    if(lastDirection == 'U' && snakeLength != 1) {
                        up = true;
                        down = false;
                        right = false;
                        left = false;
                    }
                    else {
                        down = true;
                        up = false;
                        right = false;
                        left = false;
                    }
                }
                else if(key == KeyEvent.VK_RIGHT) {
                    if(lastDirection == 'L' && snakeLength != 1) {
                        left = true;
                        up = false;
                        down = false;
                        right = false;
                    }
                    else {
                        right = true;
                        up = false;
                        down = false;
                        left = false;
                    }
                }
                else if(key == KeyEvent.VK_LEFT) {
                    if(lastDirection == 'R' && snakeLength != 1) {
                        right = true;
                        up = false;
                        down = false;
                        left = false;
                    }
                    else {
                        left = true;
                        up = false;
                        down = false;
                        right = false;
                    }
                }
            }
            

            //pause logic

            if(key == KeyEvent.VK_P && gameScreen) {
                //go onto pause screen
                gameScreen = false;
                pauseScreen = true;
                
            }
            else if(key == KeyEvent.VK_P && pauseScreen) {
                //unpause if on game screen and go back to game screen
                pauseScreen = false;
                gameScreen = true;
            }
        }

        // if a key has been released
        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        loop();
        repaint();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // creates an instance of my game
        RealSnake game = new RealSnake();
    }
}




