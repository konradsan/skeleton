package ru.kit.skeleton;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.kit.skeleton.controller.SkeletonController;
import ru.kit.skeleton.model.Skeleton;

import java.io.IOException;

/**
 * Created by mikha on 12.01.2017.
 */
public class SkeletonStage extends Stage {

    public SkeletonStage(boolean isMan, String path) {
        try {
            Skeleton skeleton = new Skeleton(path);
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ru/kit/skeleton/fxml/main_layout.fxml"));
            Parent root = loader.load();
            SkeletonController controller = loader.getController();
            controller.setSkeleton(skeleton);
            controller.setStage(this);

            this.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
