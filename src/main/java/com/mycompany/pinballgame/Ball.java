package com.mycompany.pinballgame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

// TO-DO: Fix transparent underside of paddle, and fix ball sliding weirdly on paddle

// This class creates the balls the player interacts with
public class Ball extends Circle {
    private double velocityX, velocityY;
    private double prevX, prevY, currX, currY;
    private final double airResistance = 0.995;
    private final double ceilingHitReduction = -0.8;
    private final double wallHitReduction = -0.9;
    private final double radius = this.getRadius();

    public Ball(double x, double y, double radius, double velocityX, double velocityY, Color color) {
        super(radius, color);
        setCenterX(x);
        setCenterY(y);
        
        this.currX = x;
        this.currY = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void update(double gravity, double step) {
        prevX = currX;
        prevY = currY;
        
        if (checkPaddleCollision(PinballGame.bottomLeftPaddle)) {
            this.onPaddleHit(PinballGame.bottomLeftPaddle);
        }

        if (checkPaddleCollision(PinballGame.bottomRightPaddle)) {
            this.onPaddleHit(PinballGame.bottomRightPaddle);
        }
        
        if (checkPaddleCollision(PinballGame.upperLeftPaddle)) {
            this.onPaddleHit(PinballGame.upperLeftPaddle);
        }
        
        if (checkPaddleCollision(PinballGame.upperRightPaddle)) {
            this.onPaddleHit(PinballGame.upperRightPaddle);
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
        
        // Hit da right wall? bounce back
        rightWallHit();
        
        // When it hit da left line bounce back pleasseeeeeeeeee
        leftWallHit();

        // When it hit da floor bounce back
        floorHit();

        // When it hit da ceiling bounce back
        ceilingHit();
        
    }
    
    // tan(theta) = slope
    
    // Checks if the current ball is hitting the paddle
    private boolean checkPaddleCollision(Paddle paddle) {
        // For some reason, javafx takes in angles going clockwise, so to negate, make the angle negative
        double theta = Math.toRadians(-paddle.angle);
        double trueLength = paddle.length * Math.cos(theta);
        double trueHeight = paddle.height * Math.sin(theta);
        double slope = Math.tan(theta);
        
        double topOfPaddle = paddle.y - slope;
        double bottomOfPaddle = topOfPaddle + paddle.height;
        
        // Find da Y-value using y = mx + b & m = tan(theta)
        if (paddle.pivotDirection.equals("Left")) {         
            if (currX >= paddle.pivotX && currX <= paddle.pivotX + trueLength // Within X Bounds
                && currY >= topOfPaddle && currY <= bottomOfPaddle) {
                
                return true;
            }
        } else if (paddle.pivotDirection.equals("Right")) {
            if (currX >= paddle.x && currX <= paddle.x + trueLength
                && currY >= topOfPaddle && currY <= bottomOfPaddle) {
                
                return true;
            }
        }
        return false;
    }
    
    // Method for interpolating so that the 60 update/ s lock  doesn't 
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
    
    // Overload the method to work with lines as well
    private double[] getVelocityWithAngle(Paddle paddle) {
        double theta = Math.toRadians(-paddle.angle);
        
        double speed = Math.hypot(velocityX, velocityY);
        
        double vX = -speed * Math.sin(theta);
        double vY = -speed * Math.cos(theta);
        
        double[] velocities = {vX, vY};
        return velocities;
    }
    
    private double getVelocityWithAngle(Line line) {
        double theta = Math.toRadians(prevX); // get the 
        
        return 0;
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
        
        double[] velocities = getVelocityWithAngle(paddle);
        velocityX = velocities[0];
        velocityY = velocities[1];
    }
    
    private void rightWallHit() {
        // When it hit da right line bounce back
        if (currX + radius >= PinballGame.rightBoundingLineX && prevX + radius <= PinballGame.rightBoundingLineX) {
            currX = PinballGame.rightBoundingLineX - radius;
            velocityX *= wallHitReduction;  // lose some energy on bounce
        }
    }
    
    private void leftWallHit() {
        if (currX - radius <= PinballGame.leftBoundingLineX 
            && prevX - radius >= PinballGame.leftBoundingLineX) {
            
            currX = PinballGame.leftBoundingLineX + radius;
            velocityX *= wallHitReduction; // bro look at the last one idk
        }
    }
    
    private void floorHit() {
        if (currY + radius >= PinballGame.sceneDimensions[1]) {
            currY = PinballGame.sceneDimensions[1] - radius;
            velocityY = -velocityY;
        }
    }
    
    private void ceilingHit() {
        if (currY <= 0) {
            currY = radius;
            velocityY *= ceilingHitReduction; // Lose more energy on ceiling bounce
        }
    }
    
    private void updateScoreLabel() {
        PinballGame.scoreLabel.setText("" + ++PinballGame.score);
    }
}
