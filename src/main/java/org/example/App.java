package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.Controller.Controller;
import org.example.Model.QueueManager;


import java.io.File;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(new File("C:\\Users\\Andrei\\Desktop\\Facultate Semestrul 2\\Programming Tecniques\\AssignmentTwo\\src\\main\\java\\org\\example\\View\\GUI.fxml").toURI().toURL());
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Queues Simulator");
        stage.setScene(scene);
        stage.show();
        Controller controller=new Controller();
        controller.printInstructions();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        launch(args);
    }
}

