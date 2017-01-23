package ru.kit.skeleton.controller.sagittal;

//import ru.kit.photoanalysis.frontal.ImageContourModel;

import java.awt.*;

/**
 * Created by Anton on 28.06.2016.
 */
public class ChromakeyImageSagital {
    Point centerOfBody;
    int topDistance, middleDistance, bottomDistance;

    public ChromakeyImageSagital(Point heel, Point sock, Point waist, Point back, Point neck) {
        this.centerOfBody = new Point((heel.x + sock.x) / 2, (heel.y + sock.y) / 2);
        this.topDistance = Math.abs(neck.x - centerOfBody.x);
        this.middleDistance = Math.abs(back.x - centerOfBody.x);
        this.bottomDistance = Math.abs(waist.x - centerOfBody.x);
    }

    Point top, middle, bottom;

//    private BufferedImage drawPoints(BufferedImage img, ImageContourModel contourModel) {
//
//
//        topDistance = centerOfBody.x - top.x;
//        middleDistance = centerOfBody.x - middle.x;
//        bottomDistance = centerOfBody.x - bottom.x;
//
//
//    }



    // ============== RECOMMENDATIONS

    private String recommendation = "";

    public String getRecommendation() {
        if (recommendation.equals(""))
            initRecommendation();
        return recommendation;
    }

    private void initRecommendation() {
        // ОЦТ
        switch (compare(centerOfBody.x, centerOfBody.y, 10)) {
            case 1:
                recommendation += "Неоптимальный статический стереотип: общий центр тяжести смещен вперед.\n";
                break;
            case -1:
                recommendation += "Неоптимальный статический стереотип: общий центр тяжести смещен назад.\n";
                break;
            default:
                recommendation += "При боковом анализе смещения общего центра тяжести не выявлено.\n";
        }

        // грудной
        double coefMiddle = (double) middleDistance / (double) topDistance;
        if (coefMiddle > 2.5)
            recommendation += "В грудном отделе усилен кифоз.\n";
        else if (coefMiddle < 2)
            recommendation += "В грудном отделе сглажен кифоз.\n";

        // грудной
        double coefBottom = (double) bottomDistance / (double) topDistance;
        if (coefBottom > 1.5)
            recommendation += "В поясничном отделе усилен лордоз.\n";
        else if (coefBottom < 1)
            recommendation += "В поясничном отделе сглажен лордоз.\n";
    }


    private int compare(int x, int y, int eps) {
        if (equals(x, y, eps))
            return 0;
        else
            return x > y ? 1 : -1;
    }

    private boolean equals(int x, int y, int eps) {
        return Math.abs(x - y) <= eps;
    }

    @Override
    public String toString() {
        return "ChromakeyImageSagital{" +
                "centerOfBody=" + centerOfBody +
                ", topDistance=" + topDistance +
                ", middleDistance=" + middleDistance +
                ", bottomDistance=" + bottomDistance +
                ", top=" + top +
                ", middle=" + middle +
                ", bottom=" + bottom + System.lineSeparator() +
                "recommendation='" + recommendation + '\'' +
                '}';
    }
}
