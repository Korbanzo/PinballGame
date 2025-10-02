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
            // Correct the angle by a factor of -1 since javafx finds angles from clockwise
            double theta = Math.toRadians(-paddle.angle);
            
            // Set the max speed that ball can fling
            double maxBoingSpeed = 500 * PinballGame.chaos;
            
            double currSpeed = Math.hypot(velocityX, velocityY);
            
            double newSpeed = currSpeed + maxBoingSpeed;
            
            // Add to new velocities
            velocityX = newSpeed * Math.cos(theta);
            velocityY = newSpeed * Math.sin(theta);

            // Push ball slightly away from paddle so it doesn't "stick"
            currX += Math.cos(theta) * this.getRadius();
            currY += Math.sin(theta) * this.getRadius();
            
            // Increment score on ball flung
            updateScoreLabel();
        }
    }
    
    private double[] getVelocityWithAngle(Paddle paddle) { // This one for paddles
        // Correct the angle by a factor of -1 since javafx finds angles from clockwise
        double theta = Math.toRadians(-paddle.angle);
        
        // Resolve the vector for it's magnitude sqrt(vx^2 + vy^2) = |v|
        
        double speed = Math.hypot(velocityX, velocityY);
        
        double vX = -speed * Math.sin(theta);
        double vY = -speed * Math.cos(theta);
        
        // Just snag the values from this array when I want to access velocity.
        double[] velocities = {vX, vY};
        return velocities;
    }
    
    private double[] getVelocityWithAngle(Line line) { // This one for lines
        // Current velocities
        double vX = velocityX;
        double vY = velocityY;
        
        // Direction vector of our line (passed as argument)
        double deltaX = line.getEndX() - line.getStartX();
        double deltaY = line.getEndY() - line.getStartY();
        
        // Normalize line direction to make magnitude 1.
        double length = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        deltaX /= length;
        deltaY /= length;
        
        // Get the vector of orthogonal line either -dy, dx or -dx, dy works
        double normalX = -deltaY;
        double normalY = deltaX;
        
        // Dot product of both vectors now
        double dotProduct = (vX * normalX) + (vY * normalY);
        
        // Reflection time! Formula: v' = v - 2 * dotProduct * normalDimension
        double vPrimeX = vX - 2 * dotProduct * normalX;
        double vPrimeY = vY - 2 * dotProduct * normalY;
        
        double[] velocities = {vPrimeX, vPrimeY};
        return velocities;
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
            
            double[] newVelocities = getVelocityWithAngle(PinballGame.rightBoundingLine);
            velocityX = newVelocities[0];
            velocityY = newVelocities[1];
        }
    }
    
    private void leftWallHit() {
        if (currX - radius <= PinballGame.leftBoundingLineX 
            && prevX - radius >= PinballGame.leftBoundingLineX) {
            
            currX = PinballGame.leftBoundingLineX + radius;
            double[] newVelocities = getVelocityWithAngle(PinballGame.rightBoundingLine);
            velocityX = newVelocities[0];
            velocityY = newVelocities[1];
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
