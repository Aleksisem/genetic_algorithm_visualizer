package com.lex.genetics.model.canvasNodes;

import com.lex.genetics.model.CanvasBorder;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Obstacle extends Rectangle {

  public static final Color COLOR_DEFAULT = Color.rgb(0, 0, 0, 1);
  public static final Color COLOR_TRANSPARENT = Color.rgb(0, 0, 0, 0.5);
  
  private Point2D startCoord;

  public Obstacle(Point2D coord, CanvasBorder canvas, Color color, double width, double height) {
    super(coord, canvas, color, width, height);
    startCoord = new Point2D(coord.getX() - width / 2, coord.getY() - height / 2);
  }
  
  public Point2D getStartCoord() {
    return startCoord;
  }
}
