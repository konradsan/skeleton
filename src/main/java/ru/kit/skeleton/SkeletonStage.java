package ru.kit.skeleton;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import ru.kit.skeleton.controller.SkeletonController;
import ru.kit.skeleton.model.Skeleton;

import java.io.IOException;
import java.util.List;

/**
 * Created by mikha on 12.01.2017.
 */
public class SkeletonStage extends Stage {

    public static Skeleton skeleton;
    private SkeletonController controller;

    public SkeletonStage(boolean isMan, String path) {
        try {
            skeleton = new Skeleton(path, isMan);
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ru/kit/skeleton/fxml/main_layout.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            controller.setStage(this);
            setTitle("Опорно-двигательный аппарат");

            this.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(boolean isCompleteTest) {

        if (!isCompleteTest) {
            // =========== ALERT DIALOG ==============
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Выход");
            alert.setHeaderText("Вы действительно хотите выйти?");
            alert.setContentText("Данные не были сохранены. При выходе данные будут потеряны.");
            // =======================================

            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                cancelTasks();
                this.close();
            }
        } else {
            cancelTasks();
            this.close();
        }
    }

    private void cancelTasks() {
        List<Task> tasks = controller.getTasks();
        for (Task task : tasks) {
            task.isCancelled();
        }
    }

}
