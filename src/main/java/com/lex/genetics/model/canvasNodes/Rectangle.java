package com.lex.genetics.model.canvasNodes;

import com.lex.genetics.Utils;
import com.lex.genetics.model.CanvasBorder;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Rectangle extends CanvasNode {
  private double width, height;

  public Rectangle(Point2D coord, CanvasBorder canvas, Color color, double width, double height) {
    super(coord, canvas, color);
    setWidth(width);
    setHeight(height);
  } 
  
  public double getWidth() {
    return width;
  }
  public void setWidth(double width) {
    // if (width < 0) {
    //   double newX = getCoordinate().getX() + width;
    //   setCoordinate(new Point2D(newX, getCoordinate().getY()));
    // }
    this.width = Math.abs(width);
  }
  
  public double getHeight() {
    return height;
  }
  public void setHeight(double height) {
    // if (height < 0) {
    //   double newY = getCoordinate().getY() + height;
    //   setCoordinate(new Point2D(getCoordinate().getX(), newY));
    // }
    this.height = Math.abs(height);
  }

  @Override
  public boolean contains(Point2D pCoord) {
    return pCoord.getX() >= getCoordinate().getX() - getWidth() / 2
      && pCoord.getX() <= getCoordinate().getX() + getWidth() / 2
      && pCoord.getY() >= getCoordinate().getY() - getHeight() / 2
      && pCoord.getY() <= getCoordinate().getY() + getHeight() / 2;
  }

  @Override
  Point2D calculateNewCoord(Point2D coord) {
    return new Point2D(
      Math.max(canvas.minCoord.getX() + getWidth() / 2, Math.min(coord.getX(), canvas.maxCoord.getX() - getWidth() / 2)), 
      Math.max(canvas.minCoord.getY() + getHeight() / 2, Math.min(coord.getY(), canvas.maxCoord.getY() - getHeight() / 2))
    ); 
  }
  
  @Override
  public boolean collideWithNode(CanvasNode node) {
    boolean isCollide = false;
    if (node instanceof Rectangle) {
      isCollide = Utils.isRectangleCollideWithRectangle(this, (Rectangle) node);
    } else {
      isCollide = Utils.isRectangleCollideWithCircle(this, (Circle) node);
    }
    return isCollide;
  }
}
