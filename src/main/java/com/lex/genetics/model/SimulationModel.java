package com.lex.genetics.model;

import java.util.ArrayList;
import java.util.List;

import com.lex.genetics.model.canvasNodes.CanvasNode;
import com.lex.genetics.model.canvasNodes.DestinationPoint;
import com.lex.genetics.model.canvasNodes.Entity;
import com.lex.genetics.model.canvasNodes.SpawnPoint;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

public class SimulationModel {
  private SimpleIntegerProperty genesLength = new SimpleIntegerProperty(100);
  private SimpleIntegerProperty populationSize = new SimpleIntegerProperty(50);
  private SimpleDoubleProperty mutationRate = new SimpleDoubleProperty(0.1);
  private SimpleIntegerProperty elitismQuantity = new SimpleIntegerProperty(0);
  private ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.IDLE);
  private ObjectProperty<CanvasTool> activeCanvasTool = new SimpleObjectProperty<>(CanvasTool.MOVE);
  
  private List<CanvasNode> canvasNodes = new ArrayList<>(); 
  private boolean nodeDragging = false;
  private CanvasNode selectedNode;
  private Canvas canvas;
  
  private List<Entity> population;
  private int generationVersion = 0;
  private int stepsInIteration = 0;
  private SpawnPoint spawnPoint;
  private DestinationPoint destinationPoint;

  public SimulationModel(Canvas canvas) {
    this.canvas = canvas;
  }
  
  public void generateNewPopulation() {
    population = new ArrayList<>(getPopulationSize());
    for (int i = 0; i < getPopulationSize(); i++) {
      population.add(new Entity(
        spawnPoint.getCoordinates(), 
        new Shape(
          new Point2D(canvas.getLayoutX(), canvas.getLayoutY()),
          new Point2D(canvas.getLayoutX() + canvas.getWidth(), canvas.getLayoutX() + canvas.getHeight())
        ), 
        getGenesLength()
      ));
    } 
  }
  
  public void populationMove() {
    population.forEach(entity -> {
      entity.move(destinationPoint);
    });
    increaseStepsInIteration();
  }
  
  public void nextIteration() {
    clearPopulation();
    generateNewPopulation();
    increaseGenerationVersion();
    resetStepsInIteration();
  }

  public void initialize(SpawnPoint spawnPoint, DestinationPoint destinationPoint) {
    canvasNodes.clear();
    canvasNodes.add(spawnPoint);
    canvasNodes.add(destinationPoint);
    this.spawnPoint = spawnPoint;
    this.destinationPoint = destinationPoint;
    generationVersion = 0;
    stepsInIteration = 0;
  }

  public int getGenesLength() {
    return genesLength.get();
  }
  public void setGenesLength(int value) {
    genesLength.set(value);
  }
  public IntegerProperty genesLengthProperty() {
    return genesLength;
  }
  
  public int getPopulationSize() {
    return populationSize.get();
  }
  public void setPopulationSize(int value) {
    populationSize.set(value);
  }
  public IntegerProperty populationSizeProperty() {
    return populationSize;
  }
  
  public double getMutationRate() {
    return mutationRate.get();
  }
  public void setMutationRate(double value) {
    mutationRate.set(value);
  }
  public DoubleProperty mutationRateProperty() {
    return mutationRate;
  }
  
  public int getElitismQuantity() {
    return elitismQuantity.get();
  }
  public void setElitismQuantity(int value) {
    elitismQuantity.set(value);
  }
  public IntegerProperty elitismQuantityProperty() {
    return elitismQuantity;
  }
  
  public void addCanvasNode(CanvasNode element) {
    if (element == null) return;
    canvasNodes.add(element);
  }
  public void addCanvasNodes(List<? extends CanvasNode> elements) {
    if (elements == null) return;
    canvasNodes.addAll(elements);
  }
  public List<CanvasNode> getCanvasNodes() {
    return canvasNodes;
  }
  public void removeCanvasNode(CanvasNode element) {
    canvasNodes.remove(element);
  }
  
  public SimulationState getSimulationState() {
    return simulationState.get();
  }
  public void setSimulationState(SimulationState state) {
    simulationState.set(state);
  }
  public ObjectProperty<SimulationState> simulationStateProperty() {
    return simulationState;
  }

  public int getStepsInIteration() {
    return stepsInIteration;
  }
  public void increaseStepsInIteration() {
    stepsInIteration++;
  }
  public void resetStepsInIteration() {
    stepsInIteration = 0;
  }
  
  public int getGenerationVersion() {
    return generationVersion;
  }
  public void increaseGenerationVersion() {
    generationVersion++;
  }
  public void resetGenerationVersion() {
    generationVersion = 0;
  }

  public void startSimulation() {
    setSimulationState(SimulationState.RUNNING);
  }
  public void stopSimulation() {
    setSimulationState(SimulationState.STOPPED);
    population.forEach(entity -> {
      entity.stop();
    });
  }

  public void resetSimulation() {
    setSimulationState(SimulationState.IDLE);
    clearSelectedNode();
    clearPopulation();
    resetStepsInIteration();
    resetGenerationVersion();
    initialize(spawnPoint, destinationPoint);
  }

  public boolean isNodeDragging() {
    return nodeDragging;
  }
  public void startNodeDragging() {
    nodeDragging = true;
  }
  public void stopNodeDragging() {
    nodeDragging = false;
  }

  public void setSelectedNode(CanvasNode node) {
    selectedNode = node;
  }
  public CanvasNode getSelectedNode() {
    return selectedNode;
  }
  public boolean hasSelectedNode() {
    return selectedNode != null;
  }
  public void clearSelectedNode() {
    selectedNode = null;
  }

  public List<Entity> getPopulation() {
    return population;
  }
  public void setPopulation(List<Entity> population) {
    this.population = population;
  }
  private void clearPopulation() {
    population = null;
  }

  public CanvasTool getActiveCanvasTool() {
    return activeCanvasTool.get();
  }
  public void setActiveCanvasTool(CanvasTool tool) {
    activeCanvasTool.set(tool);;
  }
  public ObjectProperty<CanvasTool> activeCanvasToolProperty() {
    return activeCanvasTool;
  }
  
}
