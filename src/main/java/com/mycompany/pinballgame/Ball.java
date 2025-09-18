package com.mycompany.pinballgame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.lang.Math;

// TO-DO: Get the ball to interact with the paddles

// This class creates the balls the player interacts with
public class Ball extends Circle {
    private double velocityX;
    private double velocityY;
    private final double airResistance = 0.995;
    private final double ceilingHitReduction = -0.8;
    private final double wallHitReduction = -0.9;
    private final double paddleFriction = 0.5;
    
    private final double theta = Math.toRadians(PinballGame.leftPaddle.angleLeft);
    
    // Length = A Cos(theta) Physics came in clutch
    public final double paddleLength = PinballGame.paddleLength * Math.cos(theta);
    public final double paddleHeight = PinballGame.paddleHeight * Math.sin(theta);

    public Ball(double x, double y, double radius, double velocityX, double velocityY, Color color) {
        super(radius, color);
        setCenterX(x);
        setCenterY(y);
        
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void update(double gravity, int[] sceneDimensions) {
        double radius = this.getRadius();
        
        if (checkPaddleCollision(PinballGame.leftPaddle)) {
            this.onPaddleHit(PinballGame.leftPaddle);
        }

        if (checkPaddleCollision(PinballGame.rightPaddle)) {
            this.onPaddleHit(PinballGame.rightPaddle);
        }
        
        
        // Apply da gravity
        velocityY += gravity;

        // Apply air resistance
        velocityX *= airResistance;
        velocityY *= airResistance;
        
        // Move da ball
        setCenterX(getCenterX() + velocityX);
        setCenterY(getCenterY() + velocityY);
           

        
        // Ball hits bounds of arena
        
        // When it hit da right line bounce back
        if (this.getCenterX() + radius >= PinballGame.rightBoundingLineX) {
            this.setCenterX(PinballGame.rightBoundingLineX - radius);
            velocityX *= wallHitReduction;  // lose some energy on bounce
        }
        
        // When it hit da left line bounce back pleasseeeeeeeeee
        if (this.getCenterX() - radius <= PinballGame.leftBoundingLineX && this.getCenterX() != 20) {
            this.setCenterX(PinballGame.leftBoundingLineX + radius);
            velocityX *= wallHitReduction; // bro look at the last one idk
        }

        // When it hit da floor bounce back
        if (this.getCenterY() + radius >= sceneDimensions[1]) {
            this.setCenterY(sceneDimensions[1] - radius);
                
            velocityY = -velocityY;
        }

        // When it hit da ceiling bounce back
        if (this.getCenterY() <= 0) {
            this.setCenterY(radius);
            velocityY *= ceilingHitReduction; // Lose more energy on ceiling bounce
        }

        // Stop micro-bouncing when at da bottom
        if (velocityY < 0.1 && velocityY > -0.1) {
            velocityY = 0;
        }
    }
    
    // Checks if the current ball is hitting the paddle
    
    private boolean checkPaddleCollision(Paddle paddle) {
        double posX = this.getCenterX();
        double posY = this.getCenterY();
        double theta = Math.toRadians(paddle.angle);
        
        double pivotX = paddle.pivotX;
        double pivotY = paddle.pivotY;
        
        // Translate ball relative to pivot
        double dx = posX - pivotX;
        double dy = posY - pivotY;
        
        // Rotate coordinates by -theta
        double rotatedX = dx * Math.cos(-theta) - (dy * Math.sin(-theta));
        double rotatedY = dx * Math.sin(-theta) + (dy * Math.cos(-theta));
        
        // check if ball is within da bounds (after rotation)
        if (rotatedX >= 0 && rotatedX <= paddle.length && 
            rotatedY >= -paddle.height / 2 && rotatedY <= paddle.height / 2) {
            return true;
        }

        return false;
    }
    
    private void onPaddleHit(Paddle paddle) {
        // Ball must bounce at the angle of the plane (in this case: paddle)\
        // V = |V| when the ball hits the plane
        // Vx = |V| * sin(theta) + gt * sin(theta) (gt = gravityIncrease)
        double theta = paddle.angle;
        this.setCenterY(this.getCenterY() - this.getRadius());

        velocityX += (velocityX * Math.sin(theta)) + (PinballGame.gravityIncrease * Math.sin(theta));
        velocityY += (velocityX * Math.cos(theta)) - (PinballGame.gravityIncrease * Math.cos(theta));
        
        System.out.println("velX: " + velocityX + " velY: " + velocityY);
    }
    
}
