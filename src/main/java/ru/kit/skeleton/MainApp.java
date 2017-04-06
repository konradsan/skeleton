package ru.kit.skeleton;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.kit.skeleton.repository.BackPlane;
import ru.kit.skeleton.repository.Plane;
import ru.kit.skeleton.repository.SagittalPlane;

import java.io.IOException;


/**
 * Created by mikha on 12.01.2017.
 */
public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Кинект");
        btn.setOnAction(event -> {
            Stage s = null;
            s =  new SkeletonStage(true, "D:\\nikitaSolovyevProjects\\skeleton\\test\\");
            s.show();
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();

    }
}
