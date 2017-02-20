package ru.kit.skeleton.report;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import ru.kit.report.ReportPart;
import ru.kit.report.WordReport;
import ru.kit.report.locomotorApparatus.BackConslusionKinect;
import ru.kit.report.util.IncreaseTable;
import ru.kit.report.util.Util;
import ru.kit.skeleton.model.Skeleton;

import java.io.File;

/**
 * Created by mikha on 01.02.2017.
 */
public class ReportAdapter {

    String backResult;
    String sagittalResult;

    public ReportAdapter(String backResult, String sagittalResult) {
        this.backResult = backResult;
        this.sagittalResult = sagittalResult;
        buildReport();
    }

    private void buildReport() {
        WordReport wordReport = new WordReport(Skeleton.getPath() + "шаблон\\шаблон.docx", Skeleton.getPath());
        System.err.println(wordReport.getDirectory());
        wordReport.addPart(new SpinalCurvatureImageKinect(Skeleton.getPath()))
                .addPart(IncreaseTable.getInstance())
                .addPart(new BackConslusionKinect(backResult, sagittalResult))
                .generate();
    }


    public class SpinalCurvatureImageKinect implements ReportPart {
        private String path;
        private static final String BACK_IMAGE = "back.svg.jpg";
        private static final String SAGITTAL_IMAGE = "sagittal.svg.jpg";

        public SpinalCurvatureImageKinect(String paht) {
            this.path = paht;
        }

        @Override
        public boolean modify(XWPFDocument doc, WordReport wordReport) {
            boolean result = true;

            try {
                File backImage = new File(path + BACK_IMAGE);
                File sagittalImage = new File(path + SAGITTAL_IMAGE);

                Util.insertImageInDoc(doc, backImage, wordReport.getTableNumber(), 0, 0,  Units.pointsToPixel(370),  Units.pointsToPixel(430));
                Util.insertImageInDoc(doc, sagittalImage, wordReport.getTableNumber(), 0, 1, Units.pointsToPixel(90),  Units.pointsToPixel(430));

            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }

            return result;
        }
    }
}
