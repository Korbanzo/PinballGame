package com.mycompany.pinballgame;

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
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
*   1. Make the ball be flung by paddles   *
*   2. Make the game a game!               *
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
    
    private static final int[] sceneDimensions = {500, 800};
    public static final double gravityIncrease = Math.pow(9.8, chaos); // idrk why this works honestly perseverance is king
    private final ArrayList<Ball> ballArray = new ArrayList<>();
    
    // Bounding line properties
    public static double leftBoundingLineX = 50;
    public static double rightBoundingLineX = sceneDimensions[0] - 50;
    public static double rightBoundingLineStartY = 100; // Offset for allowing the ball to exit on launch
    
    // Paddle properties
    private static KeyCode leftPaddleButton = KeyCode.Q; // Left paddle button is Q
    private static KeyCode rightPaddleButton = KeyCode.E; // Right paddle button is E
    private static final Color paddleColor = Color.BLACK;
    public static final double paddleLength = 100, paddleHeight = 20;
    public static final double leftPaddleX = sceneDimensions[0] / 2 - (paddleLength * 1.5);
    public static final double rightPaddleX = sceneDimensions[0] / 2 + (paddleLength / 2);
    public static final double paddleY = 700;
    
    // Instantiate paddles
    public static Paddle leftPaddle = new Paddle(leftPaddleX, paddleY, paddleLength, paddleHeight, "Left", paddleColor);
    public static Paddle rightPaddle = new Paddle(rightPaddleX, paddleY, paddleLength, paddleHeight, "Right", paddleColor);

    // This method creates the scene, and all objects to be placed in it
    @Override
    public void start(Stage stage) {
        // Main pinball properties
        final double pinballX = leftPaddleX + (paddleLength / 2);
        final double pinballY = 100;
        final double pinballInitVelX = 1, pinballInitVelY = 0;
        final double pinballRadius = 10;
        final Color pinballColor = Color.PURPLE;
        
        // Extra pinball properties
        
        final double extraPinballRadius = 10;
        final double extraPinballX = pinballX; 
        double extraPinballY = (paddleY - 50) + (pinballRadius * 2); // This will change in the for loop of creation
        final double extraBallInitVelX = 0, extraBallInitVelY = 0;
        final int extraBalls = 5;
        final Color extraPinballColor = Color.FORESTGREEN;
        
        // Instantiate main ball
        Ball pinball = new Ball(pinballX, pinballY, pinballRadius, pinballInitVelX, pinballInitVelY, pinballColor);
        ballArray.add(pinball); // Make sure to add every ball to the list!
        
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
        
        Group root = new Group(leftPaddle, rightPaddle, pinball, leftBoundingLine, rightBoundingLine);
        
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
                leftPaddle.pivot("Pressed");
                for (Ball b : ballArray)
                    b.flingBall(leftPaddle);
            }
            
            else if (pressedKey.getCode() == rightPaddleButton) {
                rightPaddle.pivot("Pressed");
                for (Ball b : ballArray)
                    b.flingBall(rightPaddle);
            }
        });

        scene.setOnKeyReleased(releasedKey -> {
            if (releasedKey.getCode() == leftPaddleButton)
                leftPaddle.pivot("Released");

            else if (releasedKey.getCode() == rightPaddleButton)
                rightPaddle.pivot("Released");                
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
                            b.update(gravityIncrease, STEP, sceneDimensions);
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
