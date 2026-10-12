package com.lex.genetics.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lex.genetics.model.Shape;
import com.lex.genetics.model.SimulationModel;
import com.lex.genetics.model.SimulationState;
import com.lex.genetics.model.canvasNodes.CanvasNode;
import com.lex.genetics.model.canvasNodes.Circle;
import com.lex.genetics.model.canvasNodes.DestinationPoint;
import com.lex.genetics.model.canvasNodes.Entity;
import com.lex.genetics.model.canvasNodes.Rectangle;
import com.lex.genetics.model.canvasNodes.SpawnPoint;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

public class SimulationController {

  private GraphicsContext ctx;
  private SimulationModel model;

  private AnimationTimer animationTimer;
  private long animationLastUpdateTime = 0;
  private double animationUpdateInterval = 0.05;

  /* Набор рисования элементов Canvas
    Используется структура данных HashMap для быстрого поиска объектов по ключу
    Generic тип позволяет передавать любой объект, наследуемого от класса CanvasNode, для доступа к его методам рисования 
  */
  private Map<Class<? extends CanvasNode>, CanvasNodeDrawer> drawers = new HashMap<>(); 

  public SimulationController(GraphicsContext graphicsContext, SimulationModel model) {
    this.ctx = graphicsContext;
    this.model = model;
    this.initialize();
  }
  
  /**
   * Инициализирует контроллер симуляции. 
   *
   * <p>Определяет точки спавна и цели, инициализирует инструменты для отрисовки элементов canvas.</p>
   */
  public void initialize() {
    Canvas canvas = ctx.getCanvas();
    Point2D minCanvasCoord = new Point2D(canvas.getLayoutX(), canvas.getLayoutY());
    Point2D maxCanvasCoord = new Point2D(canvas.getLayoutX() + canvas.getWidth(), canvas.getLayoutY() + canvas.getHeight());
    SpawnPoint spawnPoint = new SpawnPoint(new Point2D(
      canvas.getLayoutX() + canvas.getWidth() * 0.1, 
      canvas.getLayoutY() + canvas.getHeight() / 2
    ), new Shape(minCanvasCoord, maxCanvasCoord));
    DestinationPoint destinationPoint = new DestinationPoint(new Point2D(
      canvas.getLayoutX() + canvas.getWidth() * 0.9,
      canvas.getLayoutY() + canvas.getHeight() / 2
    ), new Shape(minCanvasCoord, maxCanvasCoord));
    model.initialize(spawnPoint, destinationPoint);

    // Определение методов рисования различных элементов Canvas в зависимости от их типа
    drawers.put(Circle.class, new CircleDrawer());
    drawers.put(Rectangle.class, new RectangleDrawer());
    drawers.put(SpawnPoint.class, new CircleDrawer());
    drawers.put(DestinationPoint.class, new CircleDrawer());
    drawers.put(Entity.class, new CircleDrawer());

    animationInit();
    updateCanvas();
  }
  
  public void selectNode(Point2D mouseCoord) {
    // model.stopNodeDragging(); 
    // Остановить выполнение, если симуляция запущена
    if (model.getSimulationState() != SimulationState.IDLE) return;
    model.clearSelectedNode();
    // Поиск первого элемента, находящегося под курсором мыши
    for (CanvasNode node : model.getCanvasNodes()) {
      if (node.contains(mouseCoord)) {
        model.setSelectedNode(node);
        break;
      }
    }
    updateCanvas();
  }
  
  /**
   * Перемещает элемент Canvas на указанную координату
   * @param mouseCoord координата {x; y}
   */
  public void startNodeDragging(Point2D mouseCoord) {
    // Остановка выполнения, если симуляция запущена или элемент не был выбран 
    if (model.getSimulationState() != SimulationState.IDLE || !model.hasSelectedNode()) return; 
    model.startNodeDragging();
    model.getSelectedNode().setCoordinates(mouseCoord);
    updateCanvas();
  }
  
  /** Завершает перемещение элемента Canvas */
  public void stopNodeDragging() {
    model.stopNodeDragging();
    updateCanvas();
  }
  
  /** Запускает симуляцию */
  public void startSimulation() {
    if (model.getPopulation() == null) {
      model.generateNewPopulation();
    }
    model.startSimulation();
    animationStart();
  }
  
  /** Останавливает симуляцию */
  public void stopSimulation() {
    model.stopSimulation();
    animationStop();
    updateCanvas();
  }
  
  /** Сбрасывает симуляцию в начальное состояние */
  public void resetSimulation() {
    model.resetSimulation();
    updateCanvas();
  }
  
  /** Пропускает одну итерацию симуляции */
  public void skipIteration() {
    if (model.getSimulationState() == SimulationState.IDLE) return;
    animationStop();
    while (model.getStepsInIteration() < model.getGenesLength()) {
      model.populationMove();
    }
    updateCanvas();
    finishIteration();
  }
  
  /** Перерисовывает все элементы canvas */
  private void updateCanvas() {
    clear();
    // Отрисовка размещённых на canvas объектов
    model.getCanvasNodes().forEach(element -> {
      CanvasNodeDrawer drawer = drawers.get(element.getClass());
      if (drawer != null) {
        drawer.draw(ctx, element);
      }
    });
    // Отрисовка особей
    Entity[] population = model.getPopulation();
    if (population != null) {
      for (Entity entity : population) {
        CanvasNodeDrawer drawer = drawers.get(entity.getClass());
        if (drawer != null) {
          drawer.draw(ctx, entity);
        }
      };
    } 
  }
  
  /** Инициализирует анимацию */
  private void animationInit() {
    animationTimer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        double elapsedSeconds = (now - animationLastUpdateTime) / 1_000_000_000.0;
        if (elapsedSeconds >= animationUpdateInterval) {
          animationUpdate();
          animationLastUpdateTime = now;
        }
      }
    };
  }
  
  /** Запускает анимацию */
  private void animationStart() {
    animationTimer.start(); 
  }
  
  /** Обновляет состояние анимации */
  private void animationUpdate() {
    if (model.getSimulationState() == SimulationState.RUNNING) {
      if (model.getStepsInIteration() == model.getGenesLength()) {
        System.out.printf("[SimulationController] Iteration %d has completed. Launching next iteration...\n", model.getGenerationVersion() + 1);
        animationStop();
        finishIteration();
        return;
      }
      model.populationMove();
      updateCanvas();
    }
  }
  
  /** Останавливает анимацию */
  private void animationStop() {
    animationTimer.stop();
  }
  
  /** Обрабатывает конец итерации симуляции */
  private void finishIteration() {
    model.nextIteration();
    PauseTransition pause = new PauseTransition(Duration.seconds(1));
    pause.setOnFinished(event -> {
      animationStart();
      if (model.getSimulationState() == SimulationState.RUNNING) {
        updateCanvas();
      }
    });
    pause.play();
  }
  
  /** Очищает весь canvas */
  private void clear() {
     ctx.clearRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
  }
  
}
