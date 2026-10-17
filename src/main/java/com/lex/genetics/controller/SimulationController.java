package com.lex.genetics.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.lex.genetics.model.SimulationModel;
import com.lex.genetics.model.SimulationState;
import com.lex.genetics.model.canvasNodes.CanvasNode;
import com.lex.genetics.model.canvasNodes.Circle;
import com.lex.genetics.model.canvasNodes.DestinationPoint;
import com.lex.genetics.model.canvasNodes.Entity;
import com.lex.genetics.model.canvasNodes.Obstacle;
import com.lex.genetics.model.canvasNodes.Rectangle;
import com.lex.genetics.model.canvasNodes.SpawnPoint;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

public class SimulationController {
  private final Random random = new Random();

  private GraphicsContext ctx;
  private SimulationModel model;

  private AnimationTimer animationTimer;
  private long animationLastUpdateTime = 0;
  private double animationUpdateInterval = 0.01;
  

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
    SpawnPoint spawnPoint = new SpawnPoint(new Point2D(
      canvas.getLayoutX() + canvas.getWidth() * 0.1, 
      canvas.getLayoutY() + canvas.getHeight() / 2
    ), model.getCanvas());
    DestinationPoint destinationPoint = new DestinationPoint(new Point2D(
      canvas.getLayoutX() + canvas.getWidth() * 0.9,
      canvas.getLayoutY() + canvas.getHeight() / 2
    ), model.getCanvas());
    model.initialize(spawnPoint, destinationPoint);

    // Определение методов рисования различных элементов Canvas в зависимости от их типа
    drawers.put(Circle.class, new CircleDrawer());
    drawers.put(Rectangle.class, new RectangleDrawer());
    drawers.put(SpawnPoint.class, new CircleDrawer());
    drawers.put(DestinationPoint.class, new CircleDrawer());
    drawers.put(Entity.class, new CircleDrawer());
    drawers.put(Obstacle.class, new RectangleDrawer());

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
    CanvasNode selectedNode = model.getSelectedNode();
    selectedNode.setCoordinate(mouseCoord);
    model.startNodeDragging();
    setCollisionSelection(selectedNode);
    updateCanvas();
  }
  
  public void initializeObstacle(Point2D coord) {
    if (model.getTempObstacle() == null) {
      model.setTempObstacle(
        new Obstacle(
          coord, 
          model.getCanvas(), 
          Obstacle.COLOR_DEFAULT, 
          2, 2
        )
      );
    } 
    updateCanvas();
  }
  
  public void drawObstacle(Point2D coord) {
    if (model.getTempObstacle() == null) {
      model.setTempObstacle(
        new Obstacle(
          coord, 
          model.getCanvas(), 
          Obstacle.COLOR_DEFAULT,
           2, 2
        )
      );
    } else {
      updateObstacleSize(model.getTempObstacle(), coord);
    }
    setCollisionSelection(model.getTempObstacle());
    updateCanvas();
  }
  
  public void createObstacle(Point2D coord) {
    if (model.getTempObstacle() == null) return; 
    boolean isCollide = isCollideWithAnyNode(model.getTempObstacle());
    // Добавляем препятствие, если оно не пересекается с остальными элементами Canvas
    if (!isCollide) {
      updateObstacleSize(model.getTempObstacle(), coord);
      model.getTempObstacle().setColor(Obstacle.COLOR_DEFAULT);
      model.addCanvasNode(model.getTempObstacle());
    }
    model.clearTempObstacle();
    resetCollisionSelection();
    updateCanvas();
  }
  
  private boolean isCollideWithAnyNode(CanvasNode node) {
    // Проверка на пересечение с остальными элементами Canvas
    boolean isCollide = false;
    for (CanvasNode n : model.getCanvasNodes()) {
      if (node == n) continue;
      isCollide = node.collideWithNode(n); 
      if (isCollide) break;
    }
    return isCollide;
  }
  
  private void setCollisionSelection(CanvasNode node) {
    for (CanvasNode n : model.getCanvasNodes()) {
      if (node == n) continue;
      boolean isCollide = node.collideWithNode(n); 
      if (isCollide) {
        n.setCollisionColor();
      } else {
        n.resetCollisionColor();
      }
    }
  }
  
  private void resetCollisionSelection() {
    for (CanvasNode node : model.getCanvasNodes()) {
      node.resetCollisionColor();
    }
  }

  private void updateObstacleSize(Obstacle obstacleNode, Point2D newCoord) {
      double px = obstacleNode.getStartCoord().getX(); 
      double py = obstacleNode.getStartCoord().getY();
      double dx = newCoord.getX() - px;
      double dy = newCoord.getY() - py;
      double newWidth = dx;
      double newHeight = dy;
      model.setTempObstacle(
        new Obstacle(
          new Point2D(px + newWidth / 2, py + newHeight / 2), 
          model.getCanvas(), 
          Obstacle.COLOR_DEFAULT, 
          newWidth, 
          newHeight
        )
      );
      model.getTempObstacle().setColor(Obstacle.COLOR_TRANSPARENT);
  }
  
  public void removeObstacle(Point2D coord) {
    for (CanvasNode node : model.getCanvasNodes()) {
      if (node instanceof Obstacle && node.contains(coord)) {
        model.removeCanvasNode(node);
      }
    }
  }
  
  /** Завершает перемещение элемента Canvas */
  public void stopNodeDragging() {
    if (!model.hasSelectedNode()) return;
    CanvasNode selectedNode = model.getSelectedNode();
    if (isCollideWithAnyNode(selectedNode)) {
      selectedNode.setCoordinate(model.getSelectedNodeCoord()); 
    }
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
  
  public void evolvePopulation() {
    // Вычисление значения функции приспособленности для каждой особи
    // Сохранение значений в виде коллекции, где ключ - индекс особи в массиве, а значение - значение фукнции
    HashMap<Integer, Double> populationFitness = new HashMap<>(model.getPopulationSize());
    for (int i = 0; i < model.getPopulationSize(); i++) {
      double fitness = model.getPopulation()[i].getFitness(model.getDestinationPoint().getCoordinate());
      populationFitness.put(i, fitness);
    }
    // Сортировка значений функций по убыванию
    Map<Integer, Double> sortedPopulationFitness = populationFitness
      .entrySet()
      .stream()
      .sorted(Map.Entry.comparingByValue())
      .collect(Collectors.toMap(
        Map.Entry::getKey, 
        Map.Entry::getValue,
        (oldValue, newValue) -> oldValue,
        LinkedHashMap::new
      ));

    // Получение следующего поколение размера, равному родительскому
    Entity[] newPopulation = new Entity[model.getPopulationSize()];
    for (Integer i : sortedPopulationFitness.keySet()) {
      // 1. Отбор
      Entity parent1 = selectionTournament(sortedPopulationFitness, model.selectionTournamentSize); 
      Entity parent2 = selectionTournament(sortedPopulationFitness, model.selectionTournamentSize); 
      
      // 2. Скрещивание
      Entity child = crossover(parent1, parent2);

      // 3. Мутация
      child = mutateEntity(child, model.getMutationRate()); 
      
      // Добавление потомка в новое поколение
      newPopulation[i] = child;
    }
    model.setPopulation(newPopulation);
  }

  private Entity selectionTournament(Map<Integer, Double> populationFitness, int size) {
    int bestEntityIndex = -1; 
    while (size > 0) {
      // Выбираем случайное значение из массива значений функций приспособленности
      int randomIndex = random.nextInt(populationFitness.size());
      // Пропускаем цикл, если индекс лучшей особи совпадает
      if (randomIndex == bestEntityIndex) continue;
      // Если индекс лучшей особи ещё не выбран или значение фитнес-функции особи по сгенерированному индексу выше сохранённого
      if (bestEntityIndex < 0 || populationFitness.get(randomIndex) > populationFitness.get(bestEntityIndex)) {
        bestEntityIndex = randomIndex; 
      }
      size -= 1;
    }
    return model.getPopulation()[bestEntityIndex]; 
  }
  
  /**    
   * Получает потомка путём скрещивания двух родителей. Реализует алгоритм одноточечного скрещивания.
   *
   * @param parent1 Первый родитель.
   * @param parent2 Второй родитель.
   * @return Потомок.
   */
  private Entity crossover(Entity parent1, Entity parent2) {
    int genesLength = model.getGenesLength();
    int crossoverPoint = random.nextInt(genesLength);
    List<Double> childGenes = new ArrayList<>(genesLength); 
    
    childGenes.addAll(parent1.getGenes().subList(0, crossoverPoint));
    childGenes.addAll(parent2.getGenes().subList(crossoverPoint, genesLength));
    return new Entity(model.getSpawnPoint().getCoordinate(), model.getCanvas(), childGenes);
  }
  
  /**
   * Производит мутацию генов особи. Реализует алгоритм мутации обменом.
   * @param entity Особь
   * @param mutationRate Вероятность мутации
   * @return Особь после мутации
   */
  private Entity mutateEntity(Entity entity, double mutationRate) {
    double r = random.nextDouble(); 
    if (r < mutationRate) {
      return entity;
    }
    int index1 = random.nextInt(model.getGenesLength());
    int index2 = index1;
    while (index1 == index2) {
      index2 = random.nextInt(model.getGenesLength());
    }
    entity.swapGenes(index1, index2);
    return entity;    
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
    // Отрисовка временного препятствия
    if (model.getTempObstacle() != null) {
      CanvasNodeDrawer drawer = drawers.get(model.getTempObstacle().getClass());
      drawer.draw(ctx, model.getTempObstacle());
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
    if (model.getPopulation() != null && model.getGenerationVersion() > 0) {
      System.out.println("[SimulationController] Launch evolvePopulation method");
      evolvePopulation(); 
    } else {
      System.out.println("[SimulationController] Launch generateNewPopulation method");
      model.generateNewPopulation();
    }
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
