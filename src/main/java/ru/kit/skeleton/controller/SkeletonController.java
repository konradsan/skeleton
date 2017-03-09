package ru.kit.skeleton.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kit.skeleton.SkeletonStage;
import ru.kit.skeleton.controller.back.ChromakeyImageBack;
import ru.kit.skeleton.controller.sagittal.ChromakeyImageSagittal;
import ru.kit.skeleton.model.SVG;
import ru.kit.skeleton.model.Skeleton;
import ru.kit.skeleton.model.Step;
import ru.kit.skeleton.report.ReportAdapter;
import ru.kit.skeleton.repository.*;
import ru.kit.skeleton.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;


public class SkeletonController {

    private static final Logger LOG = LoggerFactory.getLogger(SkeletonController.class);

    public ToggleButton editSagittal;
    public ToggleButton editBack;
    public ScrollPane scrollPaneBack;
    public ScrollPane scrollPaneSagittal;

    private List<Task> tasks = new LinkedList<>();
    private SkeletonStage stage;
    private Plane backPlane = new BackPlane();
    private Plane sagittalPlane = new SagittalPlane();
    private Skeleton skeleton;

    public Button buttonNextSagittal;
    public Button buttonNextBack;
    public Button buttonOk;
    public TabPane tabPane;
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

    private GraphicsContext gcBack;
    private GraphicsContext gcSagittal;

    private static final int CANVAS_WEIGHT = 350;
    private static final int CANVAS_HEIGHT = 580;

    public void setSkeleton(Skeleton skeleton) {
        this.skeleton = skeleton;
    }
    public void setStage(Stage stage) {
        this.stage = (SkeletonStage) stage;
    }
    public List<Task> getTasks() {
        return tasks;
    }

    @FXML
    public void initialize() {
        LOG.info("------------------------ INITIALIZE ---------------------------------");

        gcBack = canvasBack.getGraphicsContext2D();
        canvasBack.setWidth(CANVAS_WEIGHT);
        canvasBack.setHeight(CANVAS_HEIGHT);
        gcSagittal = canvasSagittal.getGraphicsContext2D();
        canvasSagittal.setWidth(CANVAS_WEIGHT);
        canvasSagittal.setHeight(CANVAS_HEIGHT);

        if (Skeleton.hasPhoto()) {
            buttonAnalizeOldPhoto.setVisible(true);

            insertImageOnBackCanvas();
            insertImageOnSagittalCanvas();

            //zoomImage(gcBack, 4);
            //TODO переделать
            zoomImage(gcBack, backPlane, 0, Skeleton.ORIGIN_IMAGE_BACK);
            zoomImage(gcBack, backPlane, 0, Skeleton.ORIGIN_IMAGE_BACK);
            zoomImage(gcBack, backPlane, 0, Skeleton.ORIGIN_IMAGE_BACK);
            zoomImage(gcBack, backPlane, 0, Skeleton.ORIGIN_IMAGE_BACK);

            zoomImage(gcSagittal, sagittalPlane, 1, Skeleton.ORIGIN_IMAGE_SAGITTAL);
            zoomImage(gcSagittal, sagittalPlane, 1, Skeleton.ORIGIN_IMAGE_SAGITTAL);
            zoomImage(gcSagittal, sagittalPlane, 1, Skeleton.ORIGIN_IMAGE_SAGITTAL);
            zoomImage(gcSagittal, sagittalPlane, 1, Skeleton.ORIGIN_IMAGE_SAGITTAL);
        }

        tasks.add(listenerWhenFinish);
        tasks.add(startPhotoMaker);

        Thread t = new Thread(listenerWhenFinish);
        t.setDaemon(true);
        t.start();
    }



    @FXML
    public void onCancel(ActionEvent event) {
        stage.close(!buttonOk.isDisable());
    }

    @FXML
    public void onSave(ActionEvent event) {
        LOG.info("------------------------ SAVE ------------------------");
        String backResult = getRecommendationAndWriteSvgAndImageBack();
        String sagittalResult = getRecommendationAndWriteSvgAndImageSagittal();

        Map<String, String> map = new HashMap<>();
        map.put("back", backResult);
        map.put("sagittal", sagittalResult);
        Util.writeJSON(Skeleton.getPath(), map);
        LOG.info("create JSON file {}", Skeleton.getPath() + "skeleton.json");

        stage.close(!buttonOk.isDisable());
    }

    private String getRecommendationAndWriteSvgAndImageBack() {
        ChromakeyImageBack back = new ChromakeyImageBack(backPlane.getByName("Наивысшая точка подмышки справа").getPoint(), backPlane.getByName("Наивысшая точка подмышки слева").getPoint(),
                backPlane.getByName("Правое плечо").getPoint(), backPlane.getByName("Левое плечо").getPoint(), backPlane.getByName("Мочка левого уха").getPoint(),
                backPlane.getByName("Мочка правого уха").getPoint(), backPlane.getByName("Изгиб талии слева").getPoint(), backPlane.getByName("Изгиб талии справа").getPoint(),
                backPlane.getByName("Край подвздошной кости слева").getPoint(), backPlane.getByName("Край подвздошной кости справа").getPoint(), backPlane.getByName("Центр пятки слева").getPoint(), backPlane.getByName("Центр пятки справа").getPoint());
        String backResult = back.getRecommendation();
        LOG.info("back recommendation: {}", backResult);
        Util.writeSVG(SVG.getPath(skeleton.isMan(), backPlane), back.getSvgParts(), Skeleton.getPath() + Skeleton.SVG_BACK);
        LOG.info("write SVG {}", Skeleton.getPath() + Skeleton.SVG_BACK);
        BufferedImage imageBack = Util.cropImage(Skeleton.getPath() + Skeleton.RESULT_IMAGE_BACK);
        Util.writeImage(imageBack, Skeleton.getPath() + Skeleton.RESULT_IMAGE_BACK);
        LOG.info("write image: {}", Skeleton.getPath() + Skeleton.RESULT_IMAGE_BACK);
        return backResult;
    }

    private String getRecommendationAndWriteSvgAndImageSagittal() {
        ChromakeyImageSagittal sagittal = new ChromakeyImageSagittal(sagittalPlane.getByName("Пятка").getPoint(), sagittalPlane.getByName("Носок").getPoint(),
                sagittalPlane.getByName("Поясничный лордоз").getPoint(), sagittalPlane.getByName("Грудной кифоз").getPoint(), sagittalPlane.getByName("Шейный лордоз").getPoint(), sagittalPlane.getByName("Наивысшая точка на голове").getPoint());
        String sagittalResult = sagittal.getRecommendation();
        LOG.info("sagittal recommendation: {}", sagittalResult);
        Util.writeSVG(SVG.getPath(skeleton.isMan(), sagittalPlane), sagittal.getSvgParts(), Skeleton.getPath() + "sagittal.svg");
        LOG.info("write SVG {}", Skeleton.getPath() + Skeleton.SVG_SAGITTAL);
        BufferedImage imageSagittal = Util.cropImage(Skeleton.getPath() + Skeleton.RESULT_IMAGE_SAGITTAL);
        Util.writeImage(imageSagittal, Skeleton.getPath() + Skeleton.RESULT_IMAGE_SAGITTAL);
        LOG.info("write image: {}", Skeleton.getPath() + Skeleton.RESULT_IMAGE_SAGITTAL);
        return sagittalResult;
    }

    @FXML
    public void nextStep(ActionEvent event) {
        if (tabBack.isSelected()) {
            Step step = backPlane.getNext();
            if (step == null) {
                step = new Step("Точки проставлены", "Переходите к сагиттальному анализу.");
                initialFields(step, stepNameBack, stepDescriptionBack);
                buttonNextBack.setDisable(true);
            }
            initialFields(step, stepNameBack, stepDescriptionBack);
            if (step.getName().equals("Изгиб талии слева")) {
                scrollPaneBack.setVvalue(0.2);
            } else if (step.getName().equals("Край подвздошной кости слева")) {
                scrollPaneBack.setVvalue(0.4);
            } else if (step.getName().equals("Центр пятки слева")) {
                scrollPaneBack.setVvalue(1.0);
            }
        } else if (tabSagittal.isSelected()) {
            Step step = sagittalPlane.getNext();
            if (step == null) {
                step = new Step("Точки проставлены", "Можете завершить тест.");
                initialFields(step, stepNameBack, stepDescriptionBack);
                buttonNextSagittal.setDisable(true);
            }
            if (step.getName().equals("Пятка")) {
                scrollPaneSagittal.setVvalue(1.0);
            } else if (step.getName().equals("Поясничный лордоз")) {
                scrollPaneSagittal.setVvalue(0.4);
            } else if (step.getName().equals("Грудной кифоз")) {
                scrollPaneSagittal.setVvalue(0.0);
            }
            initialFields(step, stepNameSagittal, stepDescriptionSagittal);
        }
    }

    @FXML
    public void resetAllSteps(ActionEvent event) {
        if (tabBack.isSelected()) {
            LOG.info("reset back image");
            backPlane.setDefault();
            insertImageOnBackCanvas();
            initialFields(backPlane.getThis(), stepNameBack, stepDescriptionBack);
        } else if (tabSagittal.isSelected()) {
            LOG.info("reset sagittal image");
            sagittalPlane.setDefault();
            insertImageOnSagittalCanvas();
            initialFields(sagittalPlane.getThis(), stepNameSagittal, stepDescriptionSagittal);
        }
    }

    private void insertImageOnBackCanvas() {
        gcBack.clearRect(0, 0, gcBack.getCanvas().getWidth(), gcBack.getCanvas().getHeight());
        insertImage(gcBack, Skeleton.ORIGIN_IMAGE_BACK);
    }

    private void insertImageOnSagittalCanvas() {
        gcSagittal.clearRect(0, 0, gcSagittal.getCanvas().getWidth(), gcSagittal.getCanvas().getHeight());
        insertImage(gcSagittal, Skeleton.ORIGIN_IMAGE_SAGITTAL);
    }

    @FXML
    public void cancelLastStep(ActionEvent event) {
        Step step = null;
        if (tabBack.isSelected()) {
            step = backPlane.getThis();
            if (step != null) {
                step.setPoint(null);

                initialFields(step, stepNameBack, stepDescriptionBack);
                gcBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());

                insertImage(gcBack, Skeleton.ORIGIN_IMAGE_BACK);
                backPlane.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(gcBack, s, s.getPoint().getX(), s.getPoint().getY()));
            }

        } else {
            step = sagittalPlane.getThis();
            if (step != null) {
                step.setPoint(null);

                initialFields(step, stepNameSagittal, stepDescriptionSagittal);
                gcSagittal.clearRect(0, 0, canvasSagittal.getWidth(), canvasSagittal.getHeight());

                insertImage(gcSagittal, Skeleton.ORIGIN_IMAGE_SAGITTAL);
                sagittalPlane.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(gcSagittal, s, s.getPoint().getX(), s.getPoint().getY()));
            }
        }
    }

    @FXML
    public void onClickCanvasSagittal(MouseEvent event) {
        LOG.info("------------------------------------------------------------");
        LOG.info("ON CLICK SAGITTAL CANVAS");
        LOG.info("------------------------------------------------------------");
        Point point = null;
        Step step = sagittalPlane.getThis();

        if (step != null && step.getPoint() == null) {
            double multiplier = getMultiplier(maximiseCounters[1]);
            point = new Point((int)(event.getX() / multiplier), (int)(event.getY() / multiplier));

            LOG.info("Point{x={}, y={}}", point.getX(), point.getY());

            step.setPoint(point);
            putPoint(gcSagittal, step, point.getX(), point.getY());
            initialFields(step, stepNameSagittal, stepDescriptionSagittal);
        }
    }

    @FXML
    public void onClickCanvasBack(MouseEvent event) {
        LOG.info("------------------------------------------------------------");
        LOG.info("ON CLICK BAG CANVAS");
        LOG.info("------------------------------------------------------------");
        Point point = null;
        Step step = backPlane.getThis();
            /* вычисляем множитель */

        if (step != null && step.getPoint() == null) {
            double multiplier = getMultiplier(maximiseCounters[0]);
            point = new Point((int)(event.getX() / multiplier), (int)(event.getY() / multiplier));

            LOG.info("Point{x={}, y={}}", point.getX(), point.getY());

            step.setPoint(point);
            putPoint(gcBack, step, point.getX(), point.getY());
            initialFields(step, stepNameBack, stepDescriptionBack);
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
        initialFields(backPlane.getThis(), stepNameBack, stepDescriptionBack);
        initialFields(sagittalPlane.getThis(), stepNameSagittal, stepDescriptionSagittal);
    }

    private void initialFields(Step step, Label stepName, TextArea stepDescription) {
        if (step != null) {
            LOG.info("initial fields: {}, {}", step.getName(), step.getDescription());
            stepName.setText(step.getName());
            stepDescription.setText(step.getDescription());
        } else {
            stepName.setText("");
            stepDescription.setText("");
        }
    }

    private Point centerLineLegs;
    private void putPoint(GraphicsContext gc, Step step, double x, double y) {
        Step step2 = null;
        double centerPoint = 2.0;
        gc.setStroke(Color.AQUA);

        if (step.getName().equals("Правое плечо")) {
            x = backPlane.getByName("Наивысшая точка подмышки справа").getPoint().getX();
            step.getPoint().x = (int) x;
        } else if (step.getName().equals("Левое плечо")) {
            x = backPlane.getByName("Наивысшая точка подмышки слева").getPoint().getX();
            step.getPoint().x = (int) x;
        }

        if (step.getName().equals("Наивысшая точка подмышки справа") || step.getName().equals("Наивысшая точка подмышки слева")) {
            gcBack.strokeLine(x + centerPoint, y, x + centerPoint, y - 120);
        } else if (step.getName().equals("Мочка правого уха") && (step2 = backPlane.getByName("Мочка левого уха")) != null) {
            double a = ChromakeyImageBack.getAngle(step.getPoint(), step2.getPoint());
            LOG.info("Угол ухо: " + String.valueOf(a));
            gcBack.strokeLine(step.getPoint().getX(), step.getPoint().getY() + centerPoint, step2.getPoint().getX(), step2.getPoint().getY() + centerPoint);
        } else if (step.getName().equals("Правое плечо") && (step2 = backPlane.getByName("Левое плечо")) != null) {
            double a = ChromakeyImageBack.calcAngle(step.getPoint(), step2.getPoint());
            LOG.info("Угол плечи: " + String.valueOf(a));
            gcBack.strokeLine(step.getPoint().getX(), step.getPoint().getY() + centerPoint, step2.getPoint().getX(), step2.getPoint().getY() + centerPoint);
        } else if (step.getName().equals("Изгиб талии справа") && (step2 = backPlane.getByName("Изгиб талии слева")) != null) {
            double a = ChromakeyImageBack.calcAngle(step.getPoint(), step2.getPoint());
            LOG.info("Угол талия: " + String.valueOf(a));
            gcBack.strokeLine(step.getPoint().getX(), step.getPoint().getY() + centerPoint, step2.getPoint().getX(), step2.getPoint().getY() + centerPoint);
        } else if (step.getName().equals("Край подвздошной кости справа") && (step2 = backPlane.getByName("Край подвздошной кости слева")) != null) {
            gcBack.strokeLine(step.getPoint().getX(), step.getPoint().getY() + centerPoint, step2.getPoint().getX(), step2.getPoint().getY() + centerPoint);
        } else if (step.getName().equals("Центр пятки справа") && (step2 = backPlane.getByName("Центр пятки слева")) != null) {
            gcBack.strokeLine(step.getPoint().getX(), step.getPoint().getY() + centerPoint, step2.getPoint().getX(), step2.getPoint().getY() + centerPoint);
            double centerX = (step.getPoint().getX() + step2.getPoint().getX()) / 2;
            double centerY = (step.getPoint().getY() + step2.getPoint().getY()) / 2;
            gcBack.strokeLine(centerX, centerY, centerX, -canvasBack.getHeight());

        } else if (step.getName().equals("Носок") && (step2 = sagittalPlane.getByName("Пятка")) != null) {
            gcSagittal.strokeLine(step.getPoint().getX(), step.getPoint().getY() + 1.5, step2.getPoint().getX(), step2.getPoint().getY() + 1.5);
            double centerX = (step.getPoint().getX() + step2.getPoint().getX()) / 2;
            double centerY = (step.getPoint().getY() + step2.getPoint().getY()) / 2;
            centerLineLegs = new Point();
            centerLineLegs.setLocation(centerX, centerY);
            gcSagittal.strokeLine(centerX, centerY, centerX, -canvasBack.getHeight());

        } else if (step.getName().equals("Шейный лордоз") || step.getName().equals("Грудной кифоз") || step.getName().equals("Поясничный лордоз") && centerLineLegs != null) {
            gcSagittal.strokeLine(step.getPoint().getX(), step.getPoint().getY() + 1.5, centerLineLegs.getX(), step.getPoint().getY() + 1.5);
        }

        gc.setFill(Color.GREENYELLOW);
        gc.fillRect(x, y, 4, 4);
    }


    private int maximiseCounters[] = new int[2];
    private double getMultiplier(int maximiseCounter) {
        double multiplier = 1;
        if (maximiseCounter != 0) {
            for (int i = 0; i < maximiseCounter; i++) {
                multiplier *= 1.2;
            }
        }
        return multiplier;
    }
    private final double ZOOM = 1.2;
    public void maximise(ActionEvent event) {
        if (tabBack.isSelected() && maximiseCounters[0] < 7) {
            zoomImage(gcBack, backPlane, 0, Skeleton.ORIGIN_IMAGE_BACK);

        } else if (tabSagittal.isSelected() && maximiseCounters[1] < 7) {
            zoomImage(gcSagittal, sagittalPlane, 1, Skeleton.ORIGIN_IMAGE_SAGITTAL);
        }
    }

    private void zoomImage(GraphicsContext gc, int zoom) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        for (int i = 0; i < zoom; i++) {
            gc.scale(1.2, 1.2);
        }
        gc.getCanvas().setWidth(gc.getCanvas().getWidth() * ZOOM *4);
        gc.getCanvas().setHeight(gc.getCanvas().getHeight() * ZOOM *4);
        insertImageOnBackCanvas();
        backPlane.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(gcBack, s, s.getPoint().getX(), s.getPoint().getY()));
        maximiseCounters[0] += zoom;
    }

    public void zoomImage(GraphicsContext gc, Plane plane, int counters, String imageName) {
        LOG.info("maximise x {}", (maximiseCounters[counters] + 1) * ZOOM);

        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.scale(1.2, 1.2);
        gc.getCanvas().setWidth(gc.getCanvas().getWidth() * ZOOM);
        gc.getCanvas().setHeight(gc.getCanvas().getHeight() * ZOOM);

        insertImage(gc, imageName);
        plane.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(gcBack, s, s.getPoint().getX(), s.getPoint().getY()));
        maximiseCounters[counters]++;
    }

    public void minimise(ActionEvent event) {
        if (tabBack.isSelected() && maximiseCounters[0] > 0) {
            LOG.info("back maximise x {}", (maximiseCounters[0] - 1) * ZOOM);

            gcBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());
            gcBack.scale(0.83334, 0.83334);
            canvasBack.setWidth(canvasBack.getWidth() / ZOOM);
            canvasBack.setHeight(canvasBack.getHeight() / ZOOM);

            insertImage(gcBack, Skeleton.ORIGIN_IMAGE_BACK);
            backPlane.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(gcBack, s, s.getPoint().getX(), s.getPoint().getY()));
            maximiseCounters[0]--;

        } else if (tabSagittal.isSelected() && maximiseCounters[1] > 0) {
            LOG.info("sagittal maximise x {}", (maximiseCounters[1] - 1) * ZOOM);

            gcSagittal.clearRect(0, 0 , canvasSagittal.getWidth(), canvasSagittal.getHeight());
            gcSagittal.scale(0.83334, 0.83334);
            canvasSagittal.setWidth(canvasSagittal.getWidth() / ZOOM);
            canvasSagittal.setHeight(canvasSagittal.getHeight() / ZOOM);

            insertImage(gcSagittal, Skeleton.ORIGIN_IMAGE_SAGITTAL);
            sagittalPlane.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(gcSagittal, s, s.getPoint().getX(), s.getPoint().getY()));
            maximiseCounters[1]--;
        }
    }

    public void onDraggedCanvasBack(MouseEvent event) {

        if (editBack.isSelected()) {
            scrollPaneBack.addEventFilter(TouchEvent.ANY, eh -> event.consume());
            scrollPaneBack.addEventFilter(ScrollEvent.ANY, eh -> event.consume());

            Step step = backPlane.getThis();
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
                gcBack.clearRect(0, 0, canvasBack.getWidth(), canvasBack.getHeight());
                insertImage(gcBack, Skeleton.ORIGIN_IMAGE_BACK);
                backPlane.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(gcBack, s, s.getPoint().getX(), s.getPoint().getY()));
            }
        } else {
            scrollPaneBack.removeEventFilter(TouchEvent.ANY, eh -> event.consume());
            scrollPaneBack.removeEventFilter(ScrollEvent.ANY, eh -> event.consume());
        }
    }

    public void onDraggedCanvasSagittal(MouseEvent event) {

        if (editSagittal.isSelected()) {
            scrollPaneSagittal.addEventFilter(TouchEvent.ANY, eh -> event.consume());
            Step step = sagittalPlane.getThis();
            Point point = null;

        /* вычисляем множитель */
            if (maximiseCounters[1] == 0) {
                point = new Point((int) event.getX(), (int) event.getY() - 30);
            } else {
                double multiplier = 1;
                for (int i = 0; i < maximiseCounters[1]; i++) {
                    multiplier *= 1.2;
                }
                point = new Point((int) (event.getX() / multiplier), (int) (event.getY() / multiplier) - 30);
            }

            LOG.info("moved to Point{x={}, y={}}", point.getX(), point.getY());

            if (step != null) {
                step.setPoint(point);

                gcSagittal.clearRect(0, 0, canvasSagittal.getWidth(), canvasSagittal.getHeight());

                insertImage(gcSagittal, Skeleton.ORIGIN_IMAGE_SAGITTAL);
                sagittalPlane.getAllStepWhichPointNotNull().stream().forEach(s -> putPoint(gcSagittal, s, s.getPoint().getX(), s.getPoint().getY()));
            }
        } else {
            scrollPaneSagittal.removeEventFilter(TouchEvent.ANY, eh -> event.consume());
        }
    }

    private void insertImage(GraphicsContext gc, String imageName) {
        Image image = new Image("file:\\" + Skeleton.getPath() + imageName);
        PixelReader reader = image.getPixelReader();
        WritableImage writableImage = new WritableImage(reader, 100, 300, 350, 580);
        gc.drawImage(writableImage, 0, 0);
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
                insertImage(gcBack, Skeleton.ORIGIN_IMAGE_BACK);
                insertImage(gcSagittal, Skeleton.ORIGIN_IMAGE_SAGITTAL);

                anchorBlockLayout.setVisible(false);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        initialFields(backPlane.getThis(), stepNameBack, stepDescriptionBack);
                        initialFields(sagittalPlane.getThis(), stepNameSagittal, stepDescriptionSagittal);
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

                if (backPlane.getThis().getPoint() != null) {
                    buttonNextBack.setDisable(false);
                } else {
                    buttonNextBack.setDisable(true);
                }
                if (sagittalPlane.getThis().getPoint() != null) {
                    buttonNextSagittal.setDisable(false);
                } else {
                    buttonNextSagittal.setDisable(true);
                }

                if (backPlane.isFullPoint() && sagittalPlane.isFullPoint()) {
                    buttonOk.setDisable(false);
                } else {
                    buttonOk.setDisable(true);
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

}
