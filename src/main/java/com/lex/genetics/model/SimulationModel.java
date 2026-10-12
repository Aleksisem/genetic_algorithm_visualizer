package com.lex.genetics.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
  private final Random random = new Random();
  private final int selectionTournamentSize = 3;
  
  private Entity[] population;
  private int generationVersion = 0;
  private int stepsInIteration = 0;
  private SpawnPoint spawnPoint;
  private DestinationPoint destinationPoint;

  public SimulationModel(Canvas canvas) {
    this.canvas = canvas;
  }
  
  public void generateNewPopulation() {
    population = new Entity[getPopulationSize()];
    for (int i = 0; i < getPopulationSize(); i++) {
      population[i] = new Entity(
        spawnPoint.getCoordinates(), 
        new Shape(
          new Point2D(canvas.getLayoutX(), canvas.getLayoutY()),
          new Point2D(canvas.getLayoutX() + canvas.getWidth(), canvas.getLayoutX() + canvas.getHeight())
        ), 
        getGenesLength()
      );
    } 
  }
  
  public void populationMove() {
    for (Entity entity : population) {
      entity.move(destinationPoint);
    }
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
 
  private void beginEvolution() {
    // Вычисление значения функции приспособленности для каждой особи
    double[] populationFitness = new double[population.length];
    for (int i = 0; i < population.length; i++) {
      double fitness = population[i].getFitness(destinationPoint.getCoordinates());
      populationFitness[i] = fitness;
    }

    // Получение следующего поколение размера, равному родительскому
    Entity[] newPopulation = new Entity[population.length];
    for (int i = 0; i < population.length; i++) {
      // 1. Отбор
      Entity parent1 = selectionTournament(populationFitness, selectionTournamentSize); 
      Entity parent2 = selectionTournament(populationFitness, selectionTournamentSize); 
      
      // 2. Скрещивание
      Entity child = crossover(parent1, parent2);

      // 3. Мутация
      child = mutateEntity(child); 
      
      // Добавление потомка в новое поколение
      newPopulation[i] = child;
    }
  }

  private Entity selectionTournament(double[] populationFitness, int size) {
    int bestEntityIndex = -1; 
    while (size > 0) {
      // Выбираем случайное значение из массива значений функций приспособленности
      int randomIndex = random.nextInt(0, populationFitness.length);
      // Пропускаем цикл, если индекс лучшей особи совпадает
      if (randomIndex == bestEntityIndex) continue;
      // Если индекс лучшей особи ещё не выбран или значение фитнес-функции особи по сгенерированному индексу выше сохранённого
      if (bestEntityIndex < 0 || populationFitness[randomIndex] > populationFitness[bestEntityIndex]) {
        bestEntityIndex = randomIndex; 
      }
      size -= 1;
    }
    return population[bestEntityIndex]; 
  }
  
  private Entity crossover(Entity parent1, Entity parent2) {
    // TODO: реализовать операцию скрещивания
    Entity child = parent1;
    return child;
  }
  
  private Entity mutateEntity(Entity entity) {
    // TODO: реализовать операцию мутации
    Entity mutatedEntity = entity;
    return mutatedEntity;    
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
    for (Entity entity : population) {
      entity.stop();
    }
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

  public Entity[] getPopulation() {
    return population;
  }
  public void setPopulation(Entity[] population) {
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
