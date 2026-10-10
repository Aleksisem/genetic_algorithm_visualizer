package com.lex.genetics.model.canvasNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lex.genetics.model.Shape;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Entity extends Circle {
  public static final double VELOCITY = 4.0;
  public static final double RADIUS = 3.0;
  public static final Color COLOR = EntityColor.MOVING.getColor();
  private final Random random = new Random();

  private Point2D startCoord;
  private List<Double> genes;
  private int genesLength;
  private boolean destinationReached = false;
  private int currentGeneIdx = 0;

  public Entity(Point2D coord, Shape canvas, List<Double> genes) {
    super(coord, canvas, RADIUS, COLOR);
    startCoord = coord;
    this.genes = genes;
    genesLength = genes.size();
  }

  public Entity(Point2D coord, Shape canvas, int genesLength) {
    super(coord, canvas, RADIUS, COLOR);
    startCoord = coord;
    this.genesLength = genesLength;
    this.genes = generateRandomGenes(genesLength);
  }
  
  public boolean isDestinationReached() {
    return destinationReached;
  } 
  
  private void markAsReached() {
    destinationReached = true;
    setColor(EntityColor.SUCCESS.getColor());
  }
  
  public void move(DestinationPoint destinationPoint) {
    if (isLifeComplete()) return;

    setColor(EntityColor.MOVING.getColor());
    double angle = genes.get(currentGeneIdx);
    double x = getCoordinates().getX() + Math.cos(angle) * VELOCITY;
    double y = getCoordinates().getY() + Math.sin(angle) * VELOCITY;
    setCoordinates(new Point2D(
      Math.max(RADIUS, Math.min(canvas.getWidth() - RADIUS, x)),
      Math.max(RADIUS, Math.min(canvas.getHeight() - RADIUS, y))       
    ));
    
    checkDestinationReached(destinationPoint);
    currentGeneIdx++;
    if (isLifeComplete()) {
      stop();
    }
  }
  
  public void stop() {
    if (isDestinationReached()) {
      setColor(EntityColor.SUCCESS.getColor());
    } else {
      setColor(EntityColor.INACTIVE.getColor()); 
    }
  }
  
  public void reset(List<Double> genes) {
    setCoordinates(startCoord);
    updateGenes(genes); 
  }
  
  private void updateGenes(List<Double> genes) {
    if (genes != null) {
      this.genes = genes;
      this.genesLength = genes.size();
    }
  }
  
  private boolean isLifeComplete() {
    return isDestinationReached() || (currentGeneIdx == genesLength);
  }
  
  private void checkDestinationReached(DestinationPoint destinationPoint) {
    double distance = Math.sqrt(
      Math.pow(getCoordinates().getX() - destinationPoint.getCoordinates().getX(), 2) 
      + Math.pow(getCoordinates().getY() - destinationPoint.getCoordinates().getY(), 2)
    );
    if (distance < destinationPoint.getRadius()) {
      markAsReached(); 
    }
  }
  
  private List<Double> generateRandomGenes(int length) {
    List<Double> newGenes = new ArrayList<>(length); 
    for (int i = 0; i < genesLength; i++) {
      newGenes.add(random.nextDouble() * 2 * Math.PI);
    }
    return newGenes;
  }
}
