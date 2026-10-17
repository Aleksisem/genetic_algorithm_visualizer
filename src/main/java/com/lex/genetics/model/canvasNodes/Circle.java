package com.lex.genetics.model.canvasNodes;

import com.lex.genetics.Utils;
import com.lex.genetics.model.CanvasBorder;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Circle extends CanvasNode {
  private double radius;
  
  public Circle(Point2D coord, CanvasBorder canvas, double radius, Color color) {
    super(coord, canvas, color);
    this.radius = radius;
  }
  
  @Override
  public boolean contains(Point2D pCoord) {
    return distanceTo(pCoord) <= radius;
  }
  
  @Override
  Point2D calculateNewCoord(Point2D coord) {
    return new Point2D(
      Math.max(canvas.minCoord.getX() + getRadius(), Math.min(coord.getX(), canvas.maxCoord.getX() - getRadius())), 
      Math.max(canvas.minCoord.getY() + getRadius(), Math.min(coord.getY(), canvas.maxCoord.getY() - getRadius()))
    ); 
  }
  
  public double getRadius() {
    return radius;
  }
  public void setRadius(double radius) {
    this.radius = radius; 
  }
  
  @Override
  public boolean collideWithNode(CanvasNode node) {
    boolean isCollide = false;
    if (node instanceof Rectangle) {
      Utils.isRectangleCollideWithCircle((Rectangle) node, this);
    }
    return isCollide;
  }
}
