package com.lex.genetics.model.canvasNodes;

import com.lex.genetics.model.CanvasBorder;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public abstract class CanvasNode {
  private Point2D coordinate; 
  private Color defaultColor;
  private Color color;
  CanvasBorder canvas;

  public CanvasNode(Point2D coord, CanvasBorder canvas, Color color) {
    this.coordinate = coord;
    this.defaultColor = color;
    this.color = color;
    this.canvas = canvas;
  }
  
  public Point2D getCoordinate() {
    return coordinate;
  }
  public void setCoordinate(Point2D coord) {
    coordinate = calculateNewCoord(coord);
  }
  public Color getColor() {
    return color;
  }
  public void setColor(Color color) {
    this.color = color;
  }
  
  public Color getDefaultColor() {
    return this.defaultColor;
  }

  public abstract boolean contains(Point2D pCoord);
  abstract Point2D calculateNewCoord(Point2D coord);
  
  public double distanceTo(Point2D pCoord) {
    return Math.hypot(pCoord.getX() - coordinate.getX(), pCoord.getY() - coordinate.getY());
  }
  
  public double distanceTo(CanvasNode node) {
    return Math.hypot(node.getCoordinate().getX() - coordinate.getX(), node.getCoordinate().getY() - coordinate.getY());
  }
  
  public abstract boolean collideWithNode(CanvasNode node);
  
  public void setCollisionColor() {
    color = Color.RED;
  } 

  public void resetCollisionColor() {
    color = getDefaultColor();
  }
   
}
