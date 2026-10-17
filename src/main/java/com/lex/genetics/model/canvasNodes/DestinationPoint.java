package com.lex.genetics.model.canvasNodes;

import com.lex.genetics.model.CanvasBorder;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class DestinationPoint extends Circle {

  private static final Color DESTINATION_POINT_COLOR = Color.rgb(235, 180, 52, 1); 

  public DestinationPoint(Point2D coord, CanvasBorder canvas) {
    super(coord, canvas, 10, DESTINATION_POINT_COLOR);
  }
   
}
