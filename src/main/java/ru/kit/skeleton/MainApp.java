package ru.kit.skeleton;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by mikha on 12.01.2017.
 */
public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = new Pane();
        primaryStage.setTitle("hi");
        primaryStage.setScene(new Scene(root));
        Stage stage = new SkeletonStage(true, "C:\\combine\\Skeleton\\test\\");

        stage.show();
    }
}
