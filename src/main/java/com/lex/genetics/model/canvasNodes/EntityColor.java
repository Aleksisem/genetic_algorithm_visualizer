package com.lex.genetics.model.canvasNodes;

import javafx.scene.paint.Color;

public enum EntityColor {
  MOVING(Color.rgb(26, 28, 25, 1)),
  SUCCESS(Color.rgb(52, 235, 95, 1)),
  INACTIVE(Color.rgb(207, 173, 169, 1));

  private final Color rgb;

  EntityColor(Color rgb) {
    this.rgb = rgb;
  }
  public Color getColor() {
    return rgb;
  }
}
