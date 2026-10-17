package com.lex.genetics;

import com.lex.genetics.model.canvasNodes.Circle;
import com.lex.genetics.model.canvasNodes.Rectangle;

public final class Utils {
  public static boolean isRectangleCollideWithRectangle(Rectangle rectangle1, Rectangle rectangle2) {
    return (
      ((rectangle1.getCoordinate().getX() - rectangle1.getWidth() / 2) < (rectangle2.getCoordinate().getX() + rectangle2.getWidth() / 2)) 
      && ((rectangle1.getCoordinate().getX() + rectangle1.getWidth() / 2) > (rectangle2.getCoordinate().getX() - rectangle2.getWidth() / 2))
      && ((rectangle1.getCoordinate().getY() - rectangle1.getHeight() / 2) < (rectangle2.getCoordinate().getY() + rectangle2.getHeight() / 2))
      && ((rectangle1.getCoordinate().getY() + rectangle1.getHeight() / 2) > (rectangle2.getCoordinate().getY() - rectangle2.getHeight() / 2))
    );
    
  }  
  
  public static boolean isRectangleCollideWithCircle(Rectangle rectangle, Circle circle) {
    
    double closeX = Math.max(
      rectangle.getCoordinate().getX() - rectangle.getWidth() / 2, 
      Math.min(
        circle.getCoordinate().getX(), 
        rectangle.getCoordinate().getX() + rectangle.getWidth() / 2
      )
    );
    double closeY = Math.max(
      rectangle.getCoordinate().getY() - rectangle.getHeight() / 2,
      Math.min(
        circle.getCoordinate().getY(),
        rectangle.getCoordinate().getY() + rectangle.getHeight() / 2
      )      
    );
    
    double distance = Math.hypot(circle.getCoordinate().getX() - closeX, circle.getCoordinate().getY() - closeY);
    return (distance < circle.getRadius());
  }
  
}
