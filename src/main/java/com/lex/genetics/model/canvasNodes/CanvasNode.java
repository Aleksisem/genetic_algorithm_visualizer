package com.lex.genetics.model.canvasNodes;

import com.lex.genetics.model.Shape;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public abstract class CanvasNode {
  private Point2D coordinate; 
  private Color color;
  Shape canvas;

  public CanvasNode(Point2D coord, Shape canvas, Color color) {
    this.coordinate = coord;
    this.color = color;
    this.canvas = canvas;
  }
  
  public Point2D getCoordinates() {
    return coordinate;
  }
  public void setCoordinates(Point2D coord) {
    coordinate = calculateNewCoord(coord);
  }
  public Color getColor() {
    return color;
  }
  public void setColor(Color color) {
    this.color = color;
  }

  public abstract boolean contains(Point2D pCoord);
  abstract Point2D calculateNewCoord(Point2D coord);
  
  public double distanceTo(Point2D pCoord) {
    return Math.hypot(pCoord.getX() - coordinate.getX(), pCoord.getY() - coordinate.getY());
  }
   
}
