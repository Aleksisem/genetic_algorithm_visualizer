package com.lex.genetics.model;

import javafx.geometry.Point2D;

public class Shape {
  public Point2D minCoord;
  public Point2D maxCoord;

  public Shape(Point2D minCoord, Point2D maxCoord) {
    this.minCoord = minCoord;
    this.maxCoord = maxCoord;
  }
  
  public double getWidth() {
    return maxCoord.getX() - minCoord.getX();
  }
  
  public double getHeight() {
    return maxCoord.getY() - minCoord.getY();
  }
}