package com.lex.genetics.model.canvasNodes;

import com.lex.genetics.model.Shape;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Rectangle extends CanvasNode {
  private double width, height;

  public Rectangle(Point2D coord, Shape canvas, Color color, double width, double height) {
    super(coord, canvas, color);
    setWidth(width);
    setHeight(height);
  } 
  
  public double getWidth() {
    return width;
  }
  public void setWidth(double width) {
    if (width <= 0) throw new IllegalArgumentException(String.format("Длина элемента не может быть меньше 1. Получено: %.2f", width)); 
    this.width = width;
  }
  
  public double getHeight() {
    return height;
  }
  public void setHeight(double height) {
    if (height <= 0) throw new IllegalArgumentException(String.format("Высота элемента не может быть меньше 1. Получено: %.2f", height)); 
    this.height = height;
  }

  @Override
  public boolean contains(Point2D pCoord) {
    return pCoord.getX() >= getCoordinates().getX()
      && pCoord.getX() <= getCoordinates().getX() + getWidth()
      && pCoord.getY() >= getCoordinates().getY()
      && pCoord.getY() <= getCoordinates().getY() + getHeight();
  }

  @Override
  Point2D calculateNewCoord(Point2D coord) {
    return new Point2D(
      Math.max(canvas.minCoord.getX() + getWidth() / 2, Math.min(coord.getX() + getWidth() / 2, canvas.maxCoord.getX())), 
      Math.max(canvas.minCoord.getY() + getHeight() / 2, Math.min(coord.getY() + getHeight() / 2, canvas.maxCoord.getY()))
    ); 
  }
}
