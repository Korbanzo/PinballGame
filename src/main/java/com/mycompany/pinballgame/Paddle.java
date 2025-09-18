package com.mycompany.pinballgame;

import javafx.scene.transform.Rotate;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// This class is for the player to interact with so they can hit the ball.
public class Paddle extends Rectangle {
    // Fields
    
    private Rotate rotation;
    
    public double length, height;    
    public double pivotX, pivotY;       
    public String pivotDirection;
    public double angle;
    public final double angleLeft = 15;
    public final double angleRight = -angleLeft;
    public boolean paddleIsActivated = false; // This is used to change the bounds
    
    public Paddle(double x, double y, double length, double height,
                  String pivotDirection, Color color) {
        
        // pivotDirection: 1 means it turns left
        // pivotDirectionL 2 means it turns right  
        super(x, y, length, height);
        this.setFill(color);
        
        this.length = length;
        this.height = height;
        this.pivotDirection = pivotDirection;
        
        if (this.pivotDirection.equals("Left")) { // Rotate 15 degrees
            this.angle = angleLeft;
            pivotX = x;
            pivotY = y + (height / 2);
            rotation = new Rotate(angleLeft, pivotX, pivotY);
        }
        
        else { // Rotate 360 - 15 = 345 degrees
            this.angle = angleRight;
            pivotX = x + length;
            pivotY = y + (height / 2);
            rotation = new Rotate(angleRight, pivotX, pivotY);
        }
        
        
        getTransforms().add(rotation);
       
    }
    
    public void pivot(String pressedOrReleased) {
        if (pivotDirection.equals("Left")) {
            if (pressedOrReleased.equals("Pressed")) {
                this.angle = angleRight;
                rotation.setAngle(this.angle);
                this.paddleIsActivated = true;
            }
                
            else if (pressedOrReleased.equals("Released")) {
                this.angle = angleLeft;
                rotation.setAngle(this.angle);
                this.paddleIsActivated = false;
            }
            
        } else if (pivotDirection.equals("Right")) {
            if (pressedOrReleased.equals("Pressed")) {
                this.angle = angleLeft;
                rotation.setAngle(this.angle);
                this.paddleIsActivated = true;
            }
       
            else if (pressedOrReleased.equals("Released")) {
                this.angle = angleRight;
                rotation.setAngle(this.angle);
                this.paddleIsActivated = false;
            }
        }
    }
}