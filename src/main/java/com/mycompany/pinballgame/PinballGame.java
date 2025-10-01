package com.mycompany.pinballgame;

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.scene.input.KeyCode;
import java.lang.Math;

/*  
Power ups: Slow-mo, flame trail, arena change, different obstacles
Rainbow trail on the ball

Make it an achievement to end the game with the golden ball (first try)

********************************************
*           To - Do                        *  
*   1. Score label                         *
*   2. Bumpers that give points            *
*   3. More paddles                        *
*   4. Flinging thingy on the right        *
********************************************
*/

public class PinballGame extends Application {
    private static double fpsLock = 60.0;
    private static final double STEP = 1.0 / fpsLock;
    private double accumulator = 0;
    private long lastUpdate = 0;
    private double deltaSeconds = 0;
    private final double nanoseconds = 1_000_000_000.00;
    public static double chaos = 3.5;
    
    public static final int[] sceneDimensions = {500, 800};
    public static final double gravityIncrease = Math.pow(9.8, chaos); // idrk why this works honestly perseverance is king
    private final ArrayList<Ball> ballArray = new ArrayList<>();
    
    // Score label properties
    public static int score = 0;
    public static Label scoreLabel = new Label("" + score);
    
    // Bounding line properties
    public static double leftBoundingLineX = 50;
    public static double rightBoundingLineX = sceneDimensions[0] - 50;
    public static double rightBoundingLineStartY = 100; // Offset for allowing the ball to exit on launch
    
    // Paddle properties (universal)
    private static KeyCode leftPaddleButton = KeyCode.Q; // Left paddle button is Q
    private static KeyCode rightPaddleButton = KeyCode.E; // Right paddle button is E
    private static final Color paddleColor = Color.BLACK;
    
    // Paddle properties (bottom)
    private static final double bottomPaddleLength = 100, bottomPaddleHeight = 20;
    private static final double bottomLeftPaddleX = sceneDimensions[0] / 2 - (bottomPaddleLength * 1.5);
    private static final double bottomRightPaddleX = sceneDimensions[0] / 2 + (bottomPaddleLength / 2);
    private static final double bottomPaddleY = 700;
    
    // Paddle properties (upper)
    private static final double upperPaddleLength = 50, upperPaddleHeight = 10;
    private static final double upperLeftPaddleX = leftBoundingLineX;
    private static final double upperRightPaddleX = rightBoundingLineX - upperPaddleLength;
    private static final double upperPaddleY = 200;
       
    // Instantiate paddles
    public static Paddle bottomLeftPaddle = new Paddle(bottomLeftPaddleX, bottomPaddleY, bottomPaddleLength, bottomPaddleHeight, "Left", paddleColor);
    public static Paddle bottomRightPaddle = new Paddle(bottomRightPaddleX, bottomPaddleY, bottomPaddleLength, bottomPaddleHeight, "Right", paddleColor);
    public static Paddle upperLeftPaddle = new Paddle(upperLeftPaddleX, upperPaddleY, upperPaddleLength, upperPaddleHeight, "Left", paddleColor);
    public static Paddle upperRightPaddle = new Paddle(upperRightPaddleX, upperPaddleY, upperPaddleLength, upperPaddleHeight, "Right", paddleColor);
            
    // This method creates the scene, and all objects to be placed in it
    @Override
    public void start(Stage stage) {
        // Main pinball properties
        final double pinballX = bottomLeftPaddleX + (bottomPaddleLength / 2);
        final double pinballY = 100;
        final double pinballInitVelX = 1, pinballInitVelY = 0;
        final double pinballRadius = 10;
        final Color pinballColor = Color.PURPLE;
        
        // Extra pinball properties
        final double extraPinballRadius = 10;
        final double extraPinballX = pinballX; 
        double extraPinballY = (bottomPaddleY - 50) + (pinballRadius * 2); // This will change in the for loop of creation
        final double extraBallInitVelX = 0, extraBallInitVelY = 0;
        final int extraBalls = 20;
        final Color extraPinballColor = Color.FORESTGREEN;
        
        // Instantiate main ball
        Ball pinball = new Ball(pinballX, pinballY, pinballRadius, pinballInitVelX, pinballInitVelY, pinballColor);
        ballArray.add(pinball); // Make sure to add every ball to the list!
        
        // Instantiate score label
        double scoreLabelX = (rightBoundingLineX + leftBoundingLineX) / 2;
        double scoreLabelY = sceneDimensions[1] / 8;
        scoreLabel.relocate(scoreLabelX, scoreLabelY);

        // Instantiate bounding lines     
        Line leftBoundingLine = new Line();
        leftBoundingLine.setStartX(leftBoundingLineX);
        leftBoundingLine.setStartY(0); // Top of the screen
        leftBoundingLine.setEndX(leftBoundingLineX);
        leftBoundingLine.setEndY(sceneDimensions[1]); // Bottom of the screen
        
        Line rightBoundingLine = new Line();
        rightBoundingLine.setStartX(rightBoundingLineX);
        rightBoundingLine.setStartY(rightBoundingLineStartY); // Top of the screen
        rightBoundingLine.setEndX(rightBoundingLineX);
        rightBoundingLine.setEndY(sceneDimensions[1]); // Bottom of the screen
        
        Group root = new Group(bottomLeftPaddle, bottomRightPaddle, upperLeftPaddle, upperRightPaddle, pinball, leftBoundingLine, rightBoundingLine, scoreLabel);
        
        // Instantiate extra balls
        for (int extraBall = 1; extraBall <= extraBalls; extraBall++) {
            extraPinballY -= pinballRadius * 2; // Adjusting the Y value for next ball
            Ball temp = new Ball(extraPinballX, extraPinballY, extraPinballRadius, extraBallInitVelX, extraBallInitVelY, extraPinballColor);
            
            ballArray.add(temp); // Make sure to add every ball to the list!
            root.getChildren().add(temp); // adding each ball to the group
        }
        
        System.out.println(root.getChildren().size());
        
        Scene scene = new Scene(root, sceneDimensions[0], sceneDimensions[1]);

        // Handling of pivoting the paddles on command
        scene.setOnKeyPressed(pressedKey -> {
            if (pressedKey.getCode() == leftPaddleButton) {
                bottomLeftPaddle.pivot("Pressed");
                upperLeftPaddle.pivot("Pressed");
                for (Ball b : ballArray) {
                    b.flingBall(bottomLeftPaddle);
                    b.flingBall(upperLeftPaddle);
                }
            }
            
            else if (pressedKey.getCode() == rightPaddleButton) {
                bottomRightPaddle.pivot("Pressed");
                upperRightPaddle.pivot("Pressed");
                for (Ball b : ballArray) {
                    b.flingBall(bottomRightPaddle);
                    b.flingBall(upperLeftPaddle);
                }
            }
        });

        scene.setOnKeyReleased(releasedKey -> {
            if (releasedKey.getCode() == leftPaddleButton) {
                bottomLeftPaddle.pivot("Released");
                upperLeftPaddle.pivot("Released");
            }
            
            else if (releasedKey.getCode() == rightPaddleButton) {
                bottomRightPaddle.pivot("Released");              
                upperRightPaddle.pivot("Released");
            }
        });
        
        stage.setResizable(false);
        stage.setTitle("Pinball Game");
        stage.setScene(scene);
        stage.show();
        
        // Game loop (timer)
        // Lock to 60fps
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate > 0) {
                    deltaSeconds = (now - lastUpdate) / nanoseconds;
                    accumulator += deltaSeconds;
                    
                    while (accumulator >= STEP) {
                        // Run physics at fixed rate (60 fps)
                        for (Ball b : ballArray) {
                            b.update(gravityIncrease, STEP);
                        }
                        accumulator -= STEP;
                    }
                    
                    double alpha = accumulator / STEP;
                    for (Ball b : ballArray) {
                        b.interpolate(alpha);
                    }
                }
                lastUpdate = now;
            }
        };
        timer.start();
        
        stage.toFront();
        stage.requestFocus();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
