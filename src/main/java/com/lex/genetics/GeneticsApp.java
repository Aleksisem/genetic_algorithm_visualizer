package com.lex.genetics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class GeneticsApp extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("main"), 1100, 700);
        loadStyle(scene, "/com/lex/genetics/style.css");

        stage.setScene(scene);
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GeneticsApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    private void loadStyle(Scene scene, String styleUrl) {
        scene.getStylesheets().add(getClass().getResource(styleUrl).toExternalForm());
    }

    public static void main(String[] args) {
        launch();
    }

}