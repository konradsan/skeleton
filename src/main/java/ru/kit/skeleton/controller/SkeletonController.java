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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kit.skeleton.controller.frontal.ChromakeyImage;
import ru.kit.skeleton.controller.sagittal.ChromakeyImageSagital;
import ru.kit.skeleton.model.Skeleton;
import ru.kit.skeleton.model.Step;
import ru.kit.skeleton.repository.BackStepRepositoryImpl;
import ru.kit.skeleton.repository.ListRepository;
import ru.kit.skeleton.repository.SagittalStepRepositoryImpl;
import ru.kit.skeleton.util.Util;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mikha on 12.01.2017.
 */
public class SkeletonController {

    private static final Logger LOG = LoggerFactory.getLogger(SkeletonController.class);

    public Button buttonNext;
    public Button buttonNextSagittal;
    public Button buttonOk;
    public Label stepNextNameBack;
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
    public Label stepNextNameSagittal;

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
        LOG.info("------------------------------------------------------------");
        LOG.info("SAVE");
        LOG.info("------------------------------------------------------------");
        ChromakeyImage back = new ChromakeyImage(backStepRepository.getByName("Правая подмышка").getPoint(), backStepRepository.getByName("Левая подмышка").getPoint(),
                backStepRepository.getByName("Правое плечо").getPoint(), backStepRepository.getByName("Левое плечо").getPoint(), backStepRepository.getByName("Левая мочка уха").getPoint(),
                backStepRepository.getByName("Правая мочка уха").getPoint(), backStepRepository.getByName("Левая точка талии").getPoint(), backStepRepository.getByName("Правая точка талии").getPoint(),
                backStepRepository.getByName("Левая точка пояса").getPoint(), backStepRepository.getByName("Правая точка пояса").getPoint());
        String backResult = back.getRecommendation();
        LOG.info("back: {}", back);

        ChromakeyImageSagital sagittal = new ChromakeyImageSagital(sagittalStepRepository.getByName("Пятка").getPoint(), sagittalStepRepository.getByName("Носок").getPoint(),
                sagittalStepRepository.getByName("Талия").getPoint(), sagittalStepRepository.getByName("Выпуклая часть спины").getPoint(), sagittalStepRepository.getByName("Шея").getPoint());
        String sagittalResult = sagittal.getRecommendation();
        LOG.info("sagittal: {}", sagittal);
        Map<String, String> map = new HashMap<>();
        map.put("back", backResult);
        map.put("sagittal", sagittalResult);
        Util.writeJSON(Skeleton.getPath(), map);
        LOG.info("create JSON file {}", Skeleton.getPath() + "skeleton.json");
        close();
    }

    @FXML
    public void initialize() {
        LOG.info("------------------------------------------------------------");
        LOG.info("INITIALIZE");
        LOG.info("------------------------------------------------------------");
        graphicsContextBack = canvasBack.getGraphicsContext2D();
        graphicsContextSagittal = canvasSagittal.getGraphicsContext2D();

        if (Skeleton.hasPhoto()) {
            LOG.info("have old photo");
            buttonAnalizeOldPhoto.setVisible(true);

            insertImage(graphicsContextBack, Skeleton.IMAGE_NAME_BACK);
            insertImage(graphicsContextSagittal, Skeleton.IMAGE_NAME_SAGITTAL);
        } else {
            graphicsContextBack.drawImage(new Image(getClass().getClassLoader().getResource("ru/kit/skeleton/image/photo_not_available.png").toString()), 10, 200);
        }

        Thread t = new Thread(listenerWhenFinish);
        t.setDaemon(true);
        t.start();
    }

    private void insertImage(GraphicsContext gc, String imageName) {
        gc.drawImage(cropImage(new Image("file:\\" + Skeleton.getPath() + imageName)), 0, 0);
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
            tabPane.getSelectionModel().select(tabBack);
        }
    }

    @FXML
    public void resetAllSteps(ActionEvent event) {
        if (tabBack.isSelected()) {
            LOG.info("reset back image");
            backStepRepository.setDefault();
            graphicsContextBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());
            insertImage(graphicsContextBack, Skeleton.IMAGE_NAME_BACK);
            initialFields(backStepRepository, stepNameBack, stepDescriptionBack, stepNextNameBack);
        } else {
            LOG.info("reset sagittal image");
            sagittalStepRepository.setDefault();
            graphicsContextSagittal.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());
            insertImage(graphicsContextSagittal, Skeleton.IMAGE_NAME_SAGITTAL);
            initialFields(sagittalStepRepository, stepNameSagittal, stepDescriptionSagittal, stepNextNameSagittal);
        }
    }

    @FXML
    public void cancelLastStep(ActionEvent event) {
        Step step = null;
        if (tabBack.isSelected()) {
            step = backStepRepository.getPrev();
            if (step != null) {
                step.setPoint(null);
                backStepRepository.getPrev();

                initialFields(backStepRepository, stepNameBack, stepDescriptionBack, stepNextNameBack);
                graphicsContextBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());

                insertImage(graphicsContextBack, Skeleton.IMAGE_NAME_BACK);
                backStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextBack, s, s.getPoint().getX(), s.getPoint().getY()));
            }

        } else {
            step = sagittalStepRepository.getPrev();
            if (step != null) {
                step.setPoint(null);
                sagittalStepRepository.getPrev();

                initialFields(sagittalStepRepository, stepNameSagittal, stepDescriptionSagittal, stepNextNameSagittal);
                graphicsContextSagittal.clearRect(0, 0, canvasSagittal.getWidth(), canvasSagittal.getHeight());

                insertImage(graphicsContextSagittal, Skeleton.IMAGE_NAME_SAGITTAL);
                sagittalStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextSagittal, s, s.getPoint().getX(), s.getPoint().getY()));
            }
        }
    }

    @FXML
    public void onClickCanvasSagittal(MouseEvent event) {

        if (event.getClickCount() == 2) {
            LOG.info("------------------------------------------------------------");
            LOG.info("ON CLICK SAGITTAL CANVAS");
            LOG.info("------------------------------------------------------------");
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

            LOG.info("Point{x={}, y={}}", point.getX(), point.getY());

            Step step = sagittalStepRepository.getThis();

            if (step != null) {
                step.setPoint(point);
                putPoint(graphicsContextSagittal, step, point.getX(), point.getY());
                initialFields(sagittalStepRepository, stepNameSagittal, stepDescriptionSagittal, stepNextNameSagittal);
            }
            LOG.info("{}", step);
        }

    }

    @FXML
    public void onClickCanvasBack(MouseEvent event) {
        if (event.getClickCount() == 2) {
            LOG.info("------------------------------------------------------------");
            LOG.info("ON CLICK BAG CANVAS");
            LOG.info("------------------------------------------------------------");
            Point point = null;
            /* вычисляем множитель */
            if (maximiseCounters[0] == 0) {
                point = new Point((int)event.getX(), (int)event.getY());
            } else {
                double multiplier = 1;
                for (int i = 0; i < maximiseCounters[0]; i++) {
                    multiplier *= 1.2;
                }
                point = new Point((int)(event.getX() / multiplier), (int)(event.getY() / multiplier));
            }

            LOG.info("Point{x={}, y={}}", point.getX(), point.getY());

            Step step = backStepRepository.getThis();

            if (step != null) {
                step.setPoint(point);
                putPoint(graphicsContextBack, step, point.getX(), point.getY());
                initialFields(backStepRepository, stepNameBack, stepDescriptionBack, stepNextNameBack);
            }
            LOG.info("{}", step);
        }

    }

    private Task<Void> startPhotoMaker = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            LOG.info("start photo maker");

            ProcessBuilder builder = new ProcessBuilder("cmd", "/C", "\"" + System.getProperty("java.home") + "/bin/java\"" +" -jar kinect\\Kinect-photo-maker.jar " + Skeleton.getPath());
            Process process = builder.start();
            while (process.isAlive() && !this.isCancelled()) {
                LOG.info("wait...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LOG.info("close photo maker");

            if (Skeleton.hasPhoto()) {
                insertImage(graphicsContextBack, Skeleton.IMAGE_NAME_BACK);
                insertImage(graphicsContextSagittal, Skeleton.IMAGE_NAME_SAGITTAL);
                anchorBlockLayout.setVisible(false);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        initialFields(backStepRepository, stepNameBack, stepDescriptionBack, stepNextNameBack);
                        initialFields(sagittalStepRepository, stepNameSagittal, stepDescriptionSagittal, stepNextNameSagittal);
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
//                    label1.setText("Финиш");
//                    textArea.setText("Переходите к следующему шагу");
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
        initialFields(backStepRepository, stepNameBack, stepDescriptionBack, stepNextNameBack);
        initialFields(sagittalStepRepository, stepNameSagittal, stepDescriptionSagittal, stepNextNameSagittal);
    }

    private void initialFields(ListRepository repository, Label stepName, TextArea stepDescription, Label nextStepName) {
        Step step = repository.getThis();
        Step nextStep = repository.getNext();
        if (step != null) {
            LOG.info("initial fields: {}, {}", step.getName(), step.getDescription());
            stepName.setText(step.getName());
            stepDescription.setText(step.getDescription());
        } else if(repository.getAllStepWhichPointNotNull().size() == 0) {
            stepName.setText("Поставьте точку");
            stepDescription.setText("Что бы начать поставьте точку двойным нажатием, далее двигайте точку пальцем, что бы установить ее в место согласно указаниям.");
        } else {
            stepName.setText("");
            stepDescription.setText("");
        }
        if (nextStep != null) {
            nextStepName.setText("Выберите: " + nextStep.getName());
        } else {
            nextStepName.setText("Переходите к следующему шагу");
        }
    }

    private Point centerLineLegs;
    private void putPoint(GraphicsContext gc, Step step, double x, double y) {
        Step step2 = null;

        graphicsContextSagittal.setStroke(Color.AQUA);
        graphicsContextBack.setStroke(Color.AQUA);
        if (step.getName().equals("Правая подмышка") || step.getName().equals("Левая подмышка")) {
            graphicsContextBack.strokeLine(x + 1.5, y, x + 1.5, y - 120);

        } else if (step.getName().equals("Правая мочка уха") && (step2 = backStepRepository.getByName("Левая мочка уха")) != null) {
            graphicsContextBack.strokeLine(step.getPoint().getX(), step.getPoint().getY() + 1.5, step2.getPoint().getX(), step2.getPoint().getY() + 1.5);
        } else if (step.getName().equals("Правое плечо") && (step2 = backStepRepository.getByName("Левое плечо")) != null) {
            graphicsContextBack.strokeLine(step.getPoint().getX(), step.getPoint().getY() + 1.5, step2.getPoint().getX(), step2.getPoint().getY() + 1.5);
        } else if (step.getName().equals("Правая точка талии") && (step2 = backStepRepository.getByName("Левая точка талии")) != null) {
            graphicsContextBack.strokeLine(step.getPoint().getX(), step.getPoint().getY() + 1.5, step2.getPoint().getX(), step2.getPoint().getY() + 1.5);
        } else if (step.getName().equals("Правая точка пояса") && (step2 = backStepRepository.getByName("Левая точка пояса")) != null) {
            graphicsContextBack.strokeLine(step.getPoint().getX(), step.getPoint().getY() + 1.5, step2.getPoint().getX(), step2.getPoint().getY() + 1.5);
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
                LOG.info("CLOSE STAGE, NOT SAVE");
                stage.close();
            }
        } else {
            startPhotoMaker.isCancelled();
            listenerWhenFinish.isCancelled();
            LOG.info("CLOSE STAGE AND SAVE");
            stage.close();
        }

    }


    private int maximiseCounters[] = new int[2];
    private final double ZOOM = 1.2;
    public void maximise(ActionEvent event) {
        if (tabBack.isSelected() && maximiseCounters[0] < 7) {
            LOG.info("back maximise x {}", (maximiseCounters[0] + 1) * ZOOM);

            graphicsContextBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());
            graphicsContextBack.scale(1.2, 1.2);
            canvasBack.setWidth(canvasBack.getWidth() * ZOOM);
            canvasBack.setHeight(canvasBack.getHeight() * ZOOM);

            insertImage(graphicsContextBack, Skeleton.IMAGE_NAME_BACK);
            backStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextBack, s, s.getPoint().getX(), s.getPoint().getY()));
            maximiseCounters[0]++;

        } else if (tabSagittal.isSelected() && maximiseCounters[1] < 7) {
            LOG.info("sagittal maximise x {}", (maximiseCounters[1] + 1) * ZOOM);

            graphicsContextSagittal.clearRect(0, 0 , canvasSagittal.getWidth(), canvasSagittal.getHeight());
            graphicsContextSagittal.scale(ZOOM, ZOOM);
            canvasSagittal.setWidth(canvasSagittal.getWidth() * ZOOM);
            canvasSagittal.setHeight(canvasSagittal.getHeight() * ZOOM);

            insertImage(graphicsContextSagittal, Skeleton.IMAGE_NAME_SAGITTAL);
            sagittalStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextSagittal, s, s.getPoint().getX(), s.getPoint().getY()));
            maximiseCounters[1]++;
        }
    }

    public void minimise(ActionEvent event) {
        if (tabBack.isSelected() && maximiseCounters[0] > 0) {
            LOG.info("back maximise x {}", (maximiseCounters[0] - 1) * ZOOM);

            graphicsContextBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());
            graphicsContextBack.scale(0.83334, 0.83334);
            canvasBack.setWidth(canvasBack.getWidth() / ZOOM);
            canvasBack.setHeight(canvasBack.getHeight() / ZOOM);

            insertImage(graphicsContextBack, Skeleton.IMAGE_NAME_BACK);
            backStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextBack, s, s.getPoint().getX(), s.getPoint().getY()));
            maximiseCounters[0]--;

        } else if (tabSagittal.isSelected() && maximiseCounters[1] > 0) {
            LOG.info("sagittal maximise x {}", (maximiseCounters[1] - 1) * ZOOM);

            graphicsContextSagittal.clearRect(0, 0 , canvasSagittal.getWidth(), canvasSagittal.getHeight());
            graphicsContextSagittal.scale(0.83334, 0.83334);
            canvasSagittal.setWidth(canvasSagittal.getWidth() / ZOOM);
            canvasSagittal.setHeight(canvasSagittal.getHeight() / ZOOM);

            insertImage(graphicsContextSagittal, Skeleton.IMAGE_NAME_SAGITTAL);
            sagittalStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextSagittal, s, s.getPoint().getX(), s.getPoint().getY()));
            maximiseCounters[1]--;
        }
    }

    public void onDraggedCanvasBack(MouseEvent event) {
        Step step = backStepRepository.changeLast();
        Point point = null;

        /* вычисляем множитель */
        if (maximiseCounters[0] == 0) {
            point = new Point((int)event.getX(), (int)event.getY() - 30);
        } else {
            double multiplier = 1;
            for (int i = 0; i < maximiseCounters[0]; i++) {
                multiplier *= 1.2;
            }
            point = new Point((int)(event.getX() / multiplier), (int)(event.getY() / multiplier) - 30);
        }

        LOG.info("moved to Point{x={}, y={}}", point.getX(), point.getY());

        if (step != null) {
            step.setPoint(point);

            graphicsContextBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());

            insertImage(graphicsContextBack, Skeleton.IMAGE_NAME_BACK);
            backStepRepository.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(graphicsContextBack, s, s.getPoint().getX(), s.getPoint().getY()));
        }
    }
}
