package ru.kit.skeleton.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.kit.skeleton.controller.frontal.ChromakeyImage;
import ru.kit.skeleton.controller.sagittal.ChromakeyImageSagital;
import ru.kit.skeleton.model.Skeleton;
import ru.kit.skeleton.model.Step;
import ru.kit.skeleton.repository.BackStepRepositoryImpl;
import ru.kit.skeleton.repository.ListRepository;
import ru.kit.skeleton.repository.SagittalStepRepositoryImpl;

import java.awt.*;
import java.util.Arrays;

/**
 * Created by mikha on 12.01.2017.
 */
public class SkeletonController {

    public Button buttonNext;
    public Button buttonNextSagittal;
    public Button buttonOk;
    private Stage stage;
    public TabPane tabPane;

    private ListRepository backStepRepository = new BackStepRepositoryImpl();
    private ListRepository sagittalStepRepository = new SagittalStepRepositoryImpl();

    public Tab tabBack;
    public Canvas canvasBack;
    public Label stepNameBack;
    public TextArea stepDescriptionBack;
    public Tab tabSagittal;
    public Canvas canvasSagittal;
    public Label stepNameSagittal;
    public TextArea stepDescriptionSagittal;
    public Button buttonAnalizeOldPhoto;
    public AnchorPane anchorBlockLayout;

    private GraphicsContext graphicsContextBack;
    private GraphicsContext graphicsContextSagittal;

    private static final int CANVAS_WEIGHT = 450;
    private static final int CANVAS_HEIGHT = 780;

    private Skeleton skeleton;
    private boolean isFinish = false;

    public void setSkeleton(Skeleton skeleton) {
        this.skeleton = skeleton;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void onCancel(ActionEvent event) {
        close();
    }

    @FXML
    public void onSave(ActionEvent event) {
        ChromakeyImage back = new ChromakeyImage(backStepRepository.getByName("Правая подмышка").getPoint(), backStepRepository.getByName("Левая подмышка").getPoint(),
                backStepRepository.getByName("Правое плечо").getPoint(), backStepRepository.getByName("Левое плечо").getPoint(), backStepRepository.getByName("Левая мочка уха").getPoint(),
                backStepRepository.getByName("Правая мочка уха").getPoint(), backStepRepository.getByName("Левая точка талии").getPoint(), backStepRepository.getByName("Правая точка талии").getPoint(),
                backStepRepository.getByName("Левая точка пояса").getPoint(), backStepRepository.getByName("Правая точка пояса").getPoint());
        String backResult = back.getRecommendation();
        System.out.println(backResult);

        ChromakeyImageSagital sagittal = new ChromakeyImageSagital(sagittalStepRepository.getByName("Пятка").getPoint(), sagittalStepRepository.getByName("Носок").getPoint(),
                sagittalStepRepository.getByName("Талия").getPoint(), sagittalStepRepository.getByName("Выпуклая часть спины").getPoint(), sagittalStepRepository.getByName("Шея").getPoint());
        String sagittalResult = sagittal.getRecommendation();
        System.out.println(sagittalResult);

        close();
    }

    @FXML
    public void initialize() {
        graphicsContextBack = canvasBack.getGraphicsContext2D();
        graphicsContextSagittal = canvasSagittal.getGraphicsContext2D();

        if (Skeleton.hasPhoto()) {
            buttonAnalizeOldPhoto.setVisible(true);

            insertImagesToBack();
            insertImageToSagittal();
        } else {
            graphicsContextBack.drawImage(new Image(getClass().getClassLoader().getResource("ru/kit/skeleton/image/photo_not_available.png").toString()), 10, 200);
        }

        Thread t = new Thread(listenerWhenFinish);
        t.setDaemon(true);
        t.start();
    }

    private void insertImagesToBack() {
        graphicsContextBack.drawImage(cropImage(new Image("file:\\" + Skeleton.getPath() + Skeleton.IMAGE_NAME_BACK)), 0, 0);
    }

    private void insertImageToSagittal() {
        graphicsContextSagittal.drawImage(cropImage(new Image("file:\\" + Skeleton.getPath() + Skeleton.IMAGE_NAME_SAGITTAL)), 0, 0);
    }

    private Image cropImage(Image image) {
        WritableImage result = null;
        try {
            PixelReader reader = image.getPixelReader();
            result = new WritableImage(reader, 575, 19, CANVAS_WEIGHT, CANVAS_HEIGHT);
        } catch (Exception e) {
            return image;
        }
        return result;
    }

    @FXML
    public void nextPhoto(ActionEvent event) {
        if (tabBack.isSelected()) {
            tabPane.getSelectionModel().select(tabSagittal);
        } else {
            onSave(null);
        }
    }

    @FXML
    public void resetAllSteps(ActionEvent event) {
        if (tabBack.isSelected()) {
            backStepRepository.setDefault();
            graphicsContextBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());
            insertImagesToBack();
            initialFieldsForBack();
        } else {
            sagittalStepRepository.setDefault();
            graphicsContextSagittal.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());
            insertImageToSagittal();
            initialFieldsForSagittal();
        }
    }

    @FXML
    public void cancelLastStep(ActionEvent event) {
        Step step = null;
        if (tabBack.isSelected()) {
            step = backStepRepository.getPrev();
            step.setPoint(null);

            stepNameBack.setText(step.getName());
            stepDescriptionBack.setText(step.getDescription());
            graphicsContextBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());

            insertImagesToBack();
            backStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextBack, s, s.getPoint().getX(), s.getPoint().getY()));
        } else {
            step = sagittalStepRepository.getPrev();
            step.setPoint(null);

            stepNameSagittal.setText(step.getName());
            stepDescriptionSagittal.setText(step.getDescription());
            graphicsContextSagittal.clearRect(0, 0, canvasSagittal.getWidth(), canvasSagittal.getHeight());

            insertImageToSagittal();
            sagittalStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextSagittal, s, s.getPoint().getX(), s.getPoint().getY()));
        }
    }

    @FXML
    public void onClickCanvasSagittal(MouseEvent event) {
        Point point = null;
        if (maximiseCounters[1] == 0) {
            point = new Point((int)event.getX(), (int)event.getY());
        } else {
            double multiplier = 1;
            for (int i = 0; i < maximiseCounters[1]; i++) {
                multiplier *= 1.2;
            }
            point = new Point((int)(event.getX() / multiplier), (int)(event.getY() / multiplier));
        }

        Step step = sagittalStepRepository.getThis();

        if (step != null) {
            step.setPoint(point);

            System.out.println(point.getX() + " " + point.getY());
            putPoint(graphicsContextSagittal, step, point.getX(), point.getY());
            initialFieldsForSagittal();
        }
    }

    @FXML
    public void onClickCanvasBack(MouseEvent event) {
        Point point = null;
        if (maximiseCounters[0] == 0) {
            point = new Point((int)event.getX(), (int)event.getY());
        } else {
            double multiplier = 1;
            for (int i = 0; i < maximiseCounters[0]; i++) {
                multiplier *= 1.2;
            }
            point = new Point((int)(event.getX() / multiplier), (int)(event.getY() / multiplier));
        }

        Step step = backStepRepository.getThis();

        if (step != null) {
            step.setPoint(point);

            System.out.println(point.getX() + " " + point.getY());
            putPoint(graphicsContextBack, step, point.getX(), point.getY());
            initialFieldsForBack();
        }

    }

    private Task<Void> startPhotoMaker = new Task<Void>() {
        @Override
        protected Void call() throws Exception {

            ProcessBuilder builder = new ProcessBuilder("cmd", "/C", "\"" + System.getProperty("java.home") + "/bin/java\"" +" -jar kinect\\Kinect-photo-maker.jar " + Skeleton.getPath());
            Process process = builder.start();
            while (process.isAlive() && !this.isCancelled()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (Skeleton.hasPhoto()) {
                insertImagesToBack();
                insertImageToSagittal();
                anchorBlockLayout.setVisible(false);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        initialFieldsForBack();
                        initialFieldsForSagittal();
                    }
                });
            }
            return null;
        }
    };

    private Task<Void> listenerWhenFinish = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            while (!this.isCancelled()) {
                checkStepsFinish(backStepRepository, stepNameBack, stepDescriptionBack, buttonNext);
                checkStepsFinish(sagittalStepRepository, stepNameSagittal, stepDescriptionSagittal, buttonNextSagittal);

                if (backStepRepository.isFullPoint() && sagittalStepRepository.isFullPoint()) {
                    buttonOk.setDisable(false);
                }
                try{
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    };

    private void checkStepsFinish(ListRepository repository, Label label1, TextArea textArea, Button button) {
        if (repository.isFullPoint()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    label1.setText("Финиш");
                    textArea.setText("Переходите к следующему шагу");
                    button.setDisable(false);
                }
            });
        } else {
            button.setDisable(true);
            buttonOk.setDisable(true);
        }
    }

    @FXML
    public void onActionPhoto(ActionEvent event) {
        Thread t = new Thread(startPhotoMaker);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    public void onActionAnalyzePhoto(ActionEvent event) {
        anchorBlockLayout.setVisible(false);
        initialFieldsForBack();
        initialFieldsForSagittal();
    }

    private void initialFieldsForBack() {
        Step step = backStepRepository.getNext();
        if (step != null) {
            stepNameBack.setText(step.getName());
            stepDescriptionBack.setText(step.getDescription());
        }
    }

    private void initialFieldsForSagittal() {
        Step step = sagittalStepRepository.getNext();
        if (step != null) {
            stepNameSagittal.setText(step.getName());
            stepDescriptionSagittal.setText(step.getDescription());
        }
    }

    private Point centerLineLegs;
    private void putPoint(GraphicsContext gc, Step step, double x, double y) {
        Step step2 = null;

        graphicsContextSagittal.setStroke(Color.AQUA);
        graphicsContextBack.setStroke(Color.AQUA);
        if (step.getName().equals("Правая подмышка") || step.getName().equals("Левая подмышка")) {
            graphicsContextBack.strokeLine(x + 1.5, y, x + 1.5, y - 120);

        } else if (step.getName().equals("Правая граница ног") && (step2 = backStepRepository.getByName("Левая граница ног")) != null) {
            graphicsContextBack.strokeLine(step.getPoint().getX(), step.getPoint().getY() + 1.5, step2.getPoint().getX(), step2.getPoint().getY() + 1.5);
            double centerX = (step.getPoint().getX() + step2.getPoint().getX()) / 2;
            double centerY = (step.getPoint().getY() + step2.getPoint().getY()) / 2;
            graphicsContextBack.strokeLine(centerX, centerY, centerX, -canvasBack.getHeight());

        } else if (step.getName().equals("Носок") && (step2 = sagittalStepRepository.getByName("Пятка")) != null) {
            graphicsContextSagittal.strokeLine(step.getPoint().getX(), step.getPoint().getY() + 1.5, step2.getPoint().getX(), step2.getPoint().getY() + 1.5);
            double centerX = (step.getPoint().getX() + step2.getPoint().getX()) / 2;
            double centerY = (step.getPoint().getY() + step2.getPoint().getY()) / 2;
            centerLineLegs = new Point();
            centerLineLegs.setLocation(centerX, centerY);
            graphicsContextSagittal.strokeLine(centerX, centerY, centerX, -canvasBack.getHeight());

        } else if (step.getName().equals("Шея") || step.getName().equals("Талия") || step.getName().equals("Выпуклая часть спины") && centerLineLegs != null) {
            graphicsContextSagittal.strokeLine(step.getPoint().getX(), step.getPoint().getY() + 1.5, centerLineLegs.getX(), step.getPoint().getY() + 1.5);
        }

        gc.setFill(Color.GREENYELLOW);
        gc.fillRect(x, y, 4, 4);
    }

    private void close() {

        if (buttonOk.isDisable()) {
            // =========== ALERT DIALOG ==============
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Выход");
            alert.setHeaderText("Вы действительно хотите выйти?");
            alert.setContentText("Данные не были сохранены. При выходе данные будут потеряны.");
            // =======================================

            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                startPhotoMaker.isCancelled();
                listenerWhenFinish.isCancelled();
                stage.close();
            }
        } else {
            startPhotoMaker.isCancelled();
            listenerWhenFinish.isCancelled();
            stage.close();
        }

    }


    private int maximiseCounters[] = new int[2];
    public void maximise(ActionEvent event) {
        if (tabBack.isSelected() && maximiseCounters[0] < 5) {

            graphicsContextBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());
            graphicsContextBack.scale(1.2, 1.2);
            canvasBack.setWidth(canvasBack.getWidth() * 1.2);
            canvasBack.setHeight(canvasBack.getHeight() * 1.2);

            insertImagesToBack();
            backStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextBack, s, s.getPoint().getX(), s.getPoint().getY()));
            maximiseCounters[0]++;

        } else if (maximiseCounters[1] < 5) {

            graphicsContextSagittal.clearRect(0, 0 , canvasSagittal.getWidth(), canvasSagittal.getHeight());
            graphicsContextSagittal.scale(1.2, 1.2);
            canvasSagittal.setWidth(canvasSagittal.getWidth() * 1.2);
            canvasSagittal.setHeight(canvasSagittal.getHeight() * 1.2);
            
            insertImageToSagittal();
            sagittalStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextSagittal, s, s.getPoint().getX(), s.getPoint().getY()));
            maximiseCounters[1]++;
        }
    }

    public void minimise(ActionEvent event) {
        if (tabBack.isSelected() && maximiseCounters[0] > 0) {

            graphicsContextBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());
            graphicsContextBack.scale(0.83334, 0.83334);
            canvasBack.setWidth(canvasBack.getWidth() / 1.2);
            canvasBack.setHeight(canvasBack.getHeight() / 1.2);

            insertImagesToBack();
            backStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextBack, s, s.getPoint().getX(), s.getPoint().getY()));
            maximiseCounters[0]--;

        } else if (maximiseCounters[1] > 0) {

            graphicsContextSagittal.clearRect(0, 0 , canvasSagittal.getWidth(), canvasSagittal.getHeight());
            graphicsContextSagittal.scale(0.83334, 0.83334);
            canvasSagittal.setWidth(canvasSagittal.getWidth() / 1.2);
            canvasSagittal.setHeight(canvasSagittal.getHeight() / 1.2);

            insertImageToSagittal();
            sagittalStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextSagittal, s, s.getPoint().getX(), s.getPoint().getY()));
            maximiseCounters[1]--;
        }
    }
}
