package com.lex.genetics.model.canvasNodes;

import com.lex.genetics.model.Shape;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class SpawnPoint extends Circle {

  private static final Color SPAWN_POINT_COLOR = Color.rgb(66, 135, 245, 1); 

  public SpawnPoint(Point2D coord, Shape canvas) {
    super(coord, canvas, 10, SPAWN_POINT_COLOR);
  }
   
}
