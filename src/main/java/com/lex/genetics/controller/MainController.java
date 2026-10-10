package com.lex.genetics.controller;

import com.lex.genetics.model.CanvasTool;
import com.lex.genetics.model.SimulationModel;
import com.lex.genetics.model.SimulationState;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

public class MainController {

  @FXML
  private Button startButton;
  @FXML
  private Button nextButton;
  @FXML
  private Button resetButton;
  
  @FXML 
  private Label genesLengthLabel;
  @FXML 
  private Label populationSizeLabel;
  @FXML 
  private Label mutationRateLabel;
  @FXML 
  private Label elitismQuantityLabel; 

  @FXML
  private Slider genesLengthSlider;
  @FXML
  private Slider populationSizeSlider;
  @FXML
  private Slider mutationRateSlider;
  @FXML
  private Slider elitismQuantitySlider;

  @FXML
  private ToggleButton selectNodeButton;
  @FXML
  private ToggleButton drawNodeButton;
  @FXML
  private ToggleButton removeNodeButton;
  ToggleGroup toolsGroup;
  
  @FXML
  private Canvas canvas;
  private GraphicsContext ctx;

  private SimulationController simulationController;
  private SimulationModel model;

  @FXML
  public void initialize() {
    ctx = canvas.getGraphicsContext2D();

    model = new SimulationModel(canvas);
    simulationController = new SimulationController(ctx, model);

    genesLengthSlider.valueProperty().bindBidirectional(model.genesLengthProperty()); 
    populationSizeSlider.valueProperty().bindBidirectional(model.populationSizeProperty());
    mutationRateSlider.valueProperty().bindBidirectional(model.mutationRateProperty());
    elitismQuantitySlider.valueProperty().bindBidirectional(model.elitismQuantityProperty());
    elitismQuantitySlider.maxProperty().bind(model.populationSizeProperty());

    genesLengthLabel.textProperty().bind(model.genesLengthProperty().asString());
    populationSizeLabel.textProperty().bind(model.populationSizeProperty().asString());
    mutationRateLabel.textProperty().bind(model.mutationRateProperty().asString("%.2f"));
    elitismQuantityLabel.textProperty().bind(model.elitismQuantityProperty().asString());
    
    startButton.textProperty().bind(
      // Bindings.when(model.simulationStateProperty().isEqualTo(SimulationState.RUNNING))
      // .then("Стоп")
      // .otherwise("Старт")
      Bindings.createStringBinding(() -> {
        switch (model.getSimulationState()) {
          case RUNNING:
            return "Стоп";
          case STOPPED:
            return "Продолжить";
          default:
            return "Старт";
        }
      }, model.simulationStateProperty())
    );
    nextButton.disableProperty().bind(
      model.simulationStateProperty().isEqualTo(SimulationState.IDLE)
    );
    
    toolsGroup = new ToggleGroup();
    selectNodeButton.setToggleGroup(toolsGroup); 
    drawNodeButton.setToggleGroup(toolsGroup); 
    removeNodeButton.setToggleGroup(toolsGroup); 
    toolsGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        CanvasTool selectedTool = getCanvasToolFromButton((ToggleButton) newValue); 
        if (selectedTool != null) {
          model.setActiveCanvasTool(selectedTool);
        }
      } 
    });
    model.activeCanvasToolProperty().addListener((observable, oldValue, newValue) -> {
      // TODO: изменить курсор
    });
  }
  
  private CanvasTool getCanvasToolFromButton(ToggleButton button) {
    if (button == selectNodeButton) return CanvasTool.MOVE;
    if (button == drawNodeButton) return CanvasTool.DRAW;
    if (button == removeNodeButton) return CanvasTool.REMOVE;
    return null;
  }
  
  @FXML
  private void handleStartButtonAction(ActionEvent event) {
    System.out.println("Start button clicked");
    if (model.getSimulationState() != SimulationState.RUNNING) {
      simulationController.startSimulation();
      setInputsDisable(true);
    } else {
      simulationController.stopSimulation();
      setInputsDisable(false);
    }
  }
  
  @FXML
  private void handleNextButtonAction(ActionEvent event) {
    System.out.println("Next button clicked");
    simulationController.skipIteration();
  }
  
  @FXML
  private void handleResetButtonAction(ActionEvent event) {
    System.out.println("Reset button clicked");
    simulationController.resetSimulation(); 
    setInputsDisable(false);
  }
  
  @FXML
  private void handleCanvasMousePressed(MouseEvent event) {
    System.out.println("Mouse pressing event invoked");
    CanvasTool tool = model.getActiveCanvasTool();
    
    switch (tool) {
      case MOVE:
        simulationController.selectNode(new Point2D(event.getX(), event.getY())); 
        break;
      case DRAW:
        break;
      case REMOVE:
        break;
    }
  }

  @FXML
  private void handleCanvasMouseDragged(MouseEvent event) {
    System.out.println("Mouse dragging event invoked");
    CanvasTool tool = model.getActiveCanvasTool();

    switch (tool) {
      case MOVE:
        simulationController.startNodeDragging(new Point2D(event.getX(), event.getY())); 
        break;
      case DRAW:
        break;
      case REMOVE:
        break;
    }
  }

  @FXML
  private void handleCanvasMouseReleased(MouseEvent event) {
    System.out.println("Mouse released");
    CanvasTool tool = model.getActiveCanvasTool();

    switch (tool) {
      case MOVE:
        simulationController.stopNodeDragging(); 
        break;
      case DRAW:
        break;
      case REMOVE:
        break;
    }
  }
  
  private void setInputsDisable(boolean disable) {
    genesLengthSlider.setDisable(disable);
    populationSizeSlider.setDisable(disable);
    mutationRateSlider.setDisable(disable);
    elitismQuantitySlider.setDisable(disable);
  }
}