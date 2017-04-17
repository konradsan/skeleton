package ru.kit.skeleton.controller.sagittal;

//import ru.kit.photoanalysis.back.ImageContourModel;

import ru.kit.skeleton.controller.ChromakeyImage;
import ru.kit.skeleton.controller.SkeletonController;
import ru.kit.skeleton.model.SVG;
import ru.kit.skeleton.model.Skeleton;

import java.awt.*;

/**
 * Created by Anton on 28.06.2016.
 */
public class ChromakeyImageSagittal extends ChromakeyImage {
    Point centerOfBody;
    Point top;
    private int topDistance, middleDistance, bottomDistance;
    private double coefMiddle, coefBottom;

    public ChromakeyImageSagittal(Point heel, Point sock, Point waist, Point back, Point neck, Point top) {
        recommendation = new StringBuilder("");
        this.centerOfBody = new Point((heel.x + sock.x) / 2, (heel.y + sock.y) / 2);
        this.topDistance = Math.abs(neck.x - centerOfBody.x);
        this.middleDistance = Math.abs(back.x - centerOfBody.x);
        this.bottomDistance = Math.abs(waist.x - centerOfBody.x);
        this.top = top;
        this.coefMiddle = (double) middleDistance / (double) topDistance;
        this.coefBottom = (double) bottomDistance / (double) topDistance;
    }

    //Point top, middle, bottom;

    // ============== RECOMMENDATIONS ==============================
    public void initRecommendation() {
        // ОЦТ
        switch (compare(centerOfBody.x, (int) top.getX(), 10)) {
            case 1:
                if(SkeletonController.commonCentrOfGravity.equals("right")){
                    recommendation.append("Неоптимальный статический стереотип: общий центр тяжести смещен вперед и вправо, опора на правую ногу. ");
                }else if(SkeletonController.commonCentrOfGravity.equals("left")){
                    recommendation.append("Неоптимальный статический стереотип: общий центр тяжести смещен вперед и влево, опора на левую ногу. ");
                }
                else {
                    recommendation.append("Общий центр тяжести смещен вперед. ");
                }
                break;
            case -1:
                if(SkeletonController.commonCentrOfGravity.equals("right")){
                    recommendation.append("Неоптимальный статический стереотип: общий центр тяжести смещен назад и вправо, опора на правую ногу. ");
                }else if(SkeletonController.commonCentrOfGravity.equals("left")){
                    recommendation.append("Неоптимальный статический стереотип: общий центр тяжести смещен назад и влево, опора на левую ногу. ");
                }
                else {
                    recommendation.append("Общий центр тяжести смещен назад. ");
                }
                break;
            case 0:
                if(SkeletonController.commonCentrOfGravity.equals("right")){
                    recommendation.append("Неоптимальный статический стереотип: общий центр тяжести смещен вправо, опора на правую ногу. ");
                }else if(SkeletonController.commonCentrOfGravity.equals("left")){
                    recommendation.append("Неоптимальный статический стереотип: общий центр тяжести смещен влево, опора на левую ногу. ");
                }
                break;
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
