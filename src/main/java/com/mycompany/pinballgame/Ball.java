package com.mycompany.pinballgame;

import java.util.HashSet;
import java.util.Set;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

// TO-DO: Get the ball to interact with the paddles

// This class creates the balls the player interacts with
public class Ball extends Circle {
    private double velocityX, velocityY;
    private double prevX, prevY, currX, currY;
    private final double airResistance = 0.995;
    private final double ceilingHitReduction = -0.8;
    private final double wallHitReduction = -0.9;
    
    private final double theta = Math.toRadians(PinballGame.leftPaddle.angleLeft);
    
    // Length = A Cos(theta) Physics came in clutch
    public final double paddleLength = PinballGame.paddleLength * Math.cos(theta);
    public final double paddleHeight = PinballGame.paddleHeight * Math.sin(theta);

    public Ball(double x, double y, double radius, double velocityX, double velocityY, Color color) {
        super(radius, color);
        setCenterX(x);
        setCenterY(y);
        
        this.currX = x;
        this.currY = y;
        
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void update(double gravity, double step, int[] sceneDimensions) {
        prevX = currX;
        prevY = currY;
        
        double radius = this.getRadius();

        if (checkPaddleCollision(PinballGame.leftPaddle)) {
            this.onPaddleHit(PinballGame.leftPaddle);
        }

        if (checkPaddleCollision(PinballGame.rightPaddle)) {
            this.onPaddleHit(PinballGame.rightPaddle);
        }
        
        // Apply da gravity
        velocityY += gravity * step;

        // Apply air resistance
        velocityX *= airResistance;
        velocityY *= airResistance;
        
        // Move da ball
        currX += velocityX * step;
        currY += velocityY * step;

        
        // Ball hits bounds of arena
        
        // When it hit da right line bounce back
        if (currX + radius >= PinballGame.rightBoundingLineX
             && prevX + radius <= PinballGame.rightBoundingLineX) {
            currX = PinballGame.rightBoundingLineX - radius;
            velocityX *= wallHitReduction;  // lose some energy on bounce
        }
        
        // When it hit da left line bounce back pleasseeeeeeeeee
        if (currX - radius <= PinballGame.leftBoundingLineX 
            && prevX - radius >= PinballGame.leftBoundingLineX) {
            
            currX = PinballGame.leftBoundingLineX + radius;
            velocityX *= wallHitReduction; // bro look at the last one idk
        }

        // When it hit da floor bounce back
        if (currY + radius >= sceneDimensions[1]) {
            
            currY = sceneDimensions[1] - radius;
            velocityY = -velocityY;
        }

        // When it hit da ceiling bounce back
        if (currY <= 0) {
            currY = radius;
            velocityY *= ceilingHitReduction; // Lose more energy on ceiling bounce
        }
    }
    
    // Checks if the current ball is hitting the paddle
    private boolean checkPaddleCollision(Paddle paddle) {
        // For some reason, javafx takes in angles going clockwise, so to negate, make the angle negative
        double theta = Math.toRadians(-paddle.angle);
        double trueLength = PinballGame.paddleLength * Math.cos(theta);
        double trueHeight = PinballGame.paddleHeight * Math.sin(theta);
        
        // Find da Y-value using y = mx + b & m = tan(theta)
        if (paddle.pivotDirection.equals("Left")) {         
            if (currX >= paddle.pivotX && currX <= paddle.pivotX + trueLength // Within X Bounds
                && currY >= PinballGame.paddleY - Math.tan(theta)) {
                
                return true;
            }
        } else if (paddle.pivotDirection.equals("Right")) {
            if (currX >= PinballGame.rightPaddleX && currX <= PinballGame.rightPaddleX + trueLength
                && currY >= PinballGame.paddleY - Math.tan(theta)) {
                
                return true;
            }
        }
        return false;
    }
    
    private void onPaddleHit(Paddle paddle) {
        double theta = Math.toRadians(-paddle.angle);
        currY -= this.getRadius();

        double dx = currX - paddle.pivotX;
        double dy = currY - paddle.pivotY;
        
        double rotatedX = dx * Math.cos(theta) - dy * Math.sin(theta);
        double rotatedY = dx * Math.sin(theta) + dy * Math.cos(theta);
        
        if (rotatedY < 0) {
            rotatedY = -this.getRadius();
        } else {
            rotatedY = this.getRadius();
        }
        
        theta *= -1;
        double unrotatedX = rotatedX * Math.cos(theta) - rotatedY * Math.sin(theta) + paddle.pivotX;
        double unrotatedY = rotatedX * Math.sin(theta) + rotatedY * Math.cos(theta) + paddle.pivotY;
        
        currX = unrotatedX;
        currY = unrotatedY;
        
        double speed = Math.hypot(velocityX, velocityY);
        velocityX = speed * Math.sin(theta);
        velocityY = -speed * Math.cos(theta);
    }
    
    // Method for interpolating graphics so that the lock on updates doesn't 
    // make the ball's rendering choppy.
    
    public void interpolate(double alpha) {
        double renderX = prevX + (currX - prevX) * alpha;
        double renderY = prevY + (currY - prevY) * alpha;
        
        this.setCenterX(renderX);
        this.setCenterY(renderY);
    }
    
    public void flingBall(Paddle paddle) {
        if (checkPaddleCollision(paddle)) {
            double theta = Math.toRadians(-paddle.angle);
            
            double maxBoingSpeed = 500 * PinballGame.chaos;
            double currSpeed = Math.hypot(velocityX, velocityY);
            
            double newSpeed = currSpeed + maxBoingSpeed;
            
            
            velocityX = newSpeed * Math.cos(theta);
            velocityY = newSpeed * Math.sin(theta);

            // Push ball slightly away from paddle so it doesn't "stick"
            currX += Math.cos(theta) * this.getRadius();
            currY += Math.sin(theta) * this.getRadius();
            
            updateScoreLabel();
        }
    }
    
    private void updateScoreLabel() {
        PinballGame.scoreLabel.setText("" + ++PinballGame.score);
        
        
    }
}
