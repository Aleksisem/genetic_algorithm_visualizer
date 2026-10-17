package com.lex.genetics.controller;

import com.lex.genetics.model.canvasNodes.CanvasNode;
import com.lex.genetics.model.canvasNodes.Circle;
import com.lex.genetics.model.canvasNodes.Rectangle;

import javafx.scene.canvas.GraphicsContext;

public interface CanvasNodeDrawer {
  void draw(GraphicsContext ctx, CanvasNode element); 
}

class CircleDrawer implements CanvasNodeDrawer {
  @Override
  public void draw(GraphicsContext ctx, CanvasNode node) {
    Circle circleNode = (Circle) node;
    ctx.save();
    ctx.setFill(circleNode.getColor());
    ctx.fillOval(
      circleNode.getCoordinate().getX() - circleNode.getRadius(), 
      circleNode.getCoordinate().getY() - circleNode.getRadius(),
      circleNode.getRadius() * 2.0, 
      circleNode.getRadius() * 2.0
    );
    ctx.restore();
  }
}

class RectangleDrawer implements CanvasNodeDrawer {
  @Override
  public void draw(GraphicsContext ctx, CanvasNode node) {
    Rectangle rectangleNode = (Rectangle) node;
    ctx.save();
    ctx.setFill(rectangleNode.getColor());
    ctx.fillRect(
      rectangleNode.getCoordinate().getX() - rectangleNode.getWidth() / 2, 
      rectangleNode.getCoordinate().getY() - rectangleNode.getHeight() / 2, 
      rectangleNode.getWidth(), 
      rectangleNode.getHeight()
    );
    ctx.restore();
  }
}
