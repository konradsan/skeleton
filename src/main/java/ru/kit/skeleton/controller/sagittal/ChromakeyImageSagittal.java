package ru.kit.skeleton.controller.sagittal;

//import ru.kit.photoanalysis.back.ImageContourModel;

import ru.kit.skeleton.controller.ChromakeyImage;
import ru.kit.skeleton.model.SVG;

import java.awt.*;

/**
 * Created by Anton on 28.06.2016.
 */
public class ChromakeyImageSagittal extends ChromakeyImage {
    Point centerOfBody;
    Point top;
    int topDistance, middleDistance, bottomDistance;

    public ChromakeyImageSagittal(Point heel, Point sock, Point waist, Point back, Point neck, Point top) {
        recommendation = new StringBuilder("");
        this.centerOfBody = new Point((heel.x + sock.x) / 2, (heel.y + sock.y) / 2);
        this.topDistance = Math.abs(neck.x - centerOfBody.x);
        this.middleDistance = Math.abs(back.x - centerOfBody.x);
        this.bottomDistance = Math.abs(waist.x - centerOfBody.x);
        this.top = top;
    }

    //Point top, middle, bottom;

    // ============== RECOMMENDATIONS ==============================
    private double coefMiddle = (double) middleDistance / (double) topDistance;
    private double coefBottom = (double) bottomDistance / (double) topDistance;

    public void initRecommendation() {
        // ОЦТ
        switch (compare(centerOfBody.x, (int) top.getX(), 10)) {
            case 1:
                recommendation.append("Общий центр тяжести смещен вперед. ");
                break;
            case -1:
                recommendation.append("Общий центр тяжести смещен назад. ");
                break;
            default:
                recommendation.append("При боковом анализе смещения общего центра тяжести не выявлено. ");
        }

        // грудной
        if (coefMiddle > 2.5)
            recommendation.append("В грудном отделе усилен кифоз. ");
        else if (coefMiddle < 2)
            recommendation.append("В грудном отделе сглажен кифоз. ");

        // поясничный
        if (coefBottom > 1.5)
            recommendation.append("В поясничном отделе усилен лордоз. ");
        else if (coefBottom < 1)
            recommendation.append("В поясничном отделе сглажен лордоз. ");
    }

    @Override
    protected void initSVGpic() {
        if (coefMiddle > 2.5 && coefBottom > 1.5) svgParts.add(SVG.SVGPart.SAGITTAL_UKIF_ULOR);
        else if (coefMiddle < 2 && coefBottom < 1) svgParts.add(SVG.SVGPart.SAGITTAL_SKIF_SLOR);
        else if (coefMiddle > 2.5) svgParts.add(SVG.SVGPart.SAGITTAL_UKIF);
        else svgParts.add(SVG.SVGPart.SAGITTAL_NORM);

    }

    @Override
    public String toString() {
        return "ChromakeyImageSagittal{" +
                "centerOfBody=" + centerOfBody +
                ", top=" + top +
                ", topDistance=" + topDistance +
                ", middleDistance=" + middleDistance +
                ", bottomDistance=" + bottomDistance +
                ", coefMiddle=" + coefMiddle +
                ", coefBottom=" + coefBottom +
                '}';
    }
}
