package ru.kit.skeleton.controller.back;

import ru.kit.skeleton.controller.ChromakeyImage;
import ru.kit.skeleton.model.SVG;

import java.awt.*;


/**
 * Created by Anton on 28.06.2016.
 */
public class ChromakeyImageBack extends ChromakeyImage {
    Point rightArmpit, leftArmpit,
            rightArmpitUp, leftArmpitUp, centerOfBody,
            leftEar, rightEar,
            leftWaist, rightWaist,
            leftBelt, rightBelt;
    double rightEarDist, leftEarDist;

    double angle_ears_shldrs, angle_shldrs_waist, angle_waist_belt;
    double angle_shoulders, angle_ears, angle_waist;

    public ChromakeyImageBack(Point rightArmpit, Point leftArmpit, Point rightArmpitUp, Point leftArmpitUp, Point leftEar, Point rightEar, Point leftWaist, Point rightWaist, Point leftBelt, Point rightBelt, Point leftFoot, Point rightFoot) {
        recommendation = new StringBuilder("");

        this.rightArmpit = rightArmpit;
        this.leftArmpit = leftArmpit;
        this.rightArmpitUp = rightArmpitUp;
        this.leftArmpitUp = leftArmpitUp;
        this.leftEar = leftEar;
        this.rightEar = rightEar;
        this.leftWaist = leftWaist;
        this.rightWaist = rightWaist;
        this.centerOfBody = new Point((int)((leftFoot.x + rightFoot.x) / 2), (int)((leftFoot.y + rightFoot.y) / 2));
        this.leftBelt = leftBelt;
        this.rightBelt = rightBelt;

        angle_shldrs_waist = Math.abs(calcAngle(getVector(leftArmpitUp, rightArmpitUp),
                getVector(leftWaist, rightWaist)));
        angle_waist_belt = Math.abs(calcAngle(getVector(leftWaist, rightWaist),
                getVector(leftBelt, rightBelt)));
        paintAngleGraphics();

        rightEarDist = Math.abs(rightEar.getX() - centerOfBody.getX());
        leftEarDist = Math.abs(leftEar.getX() - centerOfBody.getX());

    }

    private void paintAngleGraphics() {
        // angle between line of shoulders and line of ears
        angle_ears_shldrs = calcAngle(getVector(leftArmpitUp, rightArmpitUp), getVector(leftEar, rightEar));

        // angle between line of shoulders and center line
        angle_shoulders = Math.abs(calcAngle(getVector(leftArmpitUp, rightArmpitUp),
                getVector(centerOfBody, new Point(centerOfBody.x, 0))) - 90);

        // angle between line of ears and center line
        angle_ears = Math.abs(calcAngle(getVector(leftEar, rightEar),
                getVector(centerOfBody, new Point(centerOfBody.x, 0))) - 90);


    }

//    private void drawWaist(BufferedImage img, BufferedImage processedImg, ImageContourModel contourModel) {
//        angle_waist = Math.abs(calcAngle(getVector(leftWaist, rightWaist),
//                getVector(centerOfBody, new Point(centerOfBody.x, 0))) - 90);
//    }


    // ================= RECOMENDATIONS ========================
    public void initRecommendation() {
        // ОЦТ
        switch (compare((int) rightEarDist, (int) leftEarDist, 10)) {
            case 1:
                recommendation.append("Неоптимальный статический стереотип: общий центр тяжести смещен вправо, опора на правую ногу. ");
                break;
            case -1:
                recommendation.append("Неоптимальный статический стереотип: общий центр тяжести смещен влево, опора на левую ногу. ");
                break;
            default:
        }
        // шейный отдел
        boolean isNeck = false;
        String neckString = "";
        String frontNeckString = "Постуральный мышечный дисбаланс в шейном отделе позвоночника: ";
        if (angle_ears_shldrs <= 2) {
            if (leftEar.y > rightEar.y) {
                isNeck = true;
                neckString = "голова наклонена влево, правое плечо выше левого. ";
            } else if (leftEar.y < rightEar.y) {
                isNeck = true;
                neckString = "голова наклонена вправо, левое плечо выше правого. ";
            }
        } else {
            if (leftEar.y > rightEar.y) {
                isNeck = true;
                neckString = "голова наклонена влево, левое плечо выше правого. ";
            } else {
                isNeck = true;
                neckString = "голова наклонена вправо, правое плечо выше левого. ";
            }
        }
        // грудной отдел
        boolean isBrust = false;
        String brustString = "";
        String frontBrustString = "Постуральный мышечный дисбаланс в грудном отделе позвоночника: ";
        if (angle_shldrs_waist <= 2) {
            if (leftArmpitUp.y != rightArmpitUp.y) {
                isBrust = true;
                brustString = "границы грудного региона справа и слева асимметричны. ";
            }
        } else {
            if (angle_shoulders > 2)
                if (leftArmpitUp.y > rightArmpitUp.y) {
                    isBrust = true;
                    brustString = "верхняя и нижняя границы грудного региона слева приближены друг к другу,  справа - взаимоудалены. ";
                } else {
                    isBrust = true;
                    brustString = "верхняя и нижняя границы грудного региона справа приближены друг к другу, слева - взаимоудалены. ";
                }
        }
        // поясничный отдел
        boolean isWaist = false;
        String waistString = "";
        String frontWaistString = "Постуральный мышечный дисбаланс в поясничном отделе позвоночника: ";
        if (angle_waist_belt <= 2) {
            if (leftWaist.y != rightWaist.y) {
                isWaist = true;
                waistString = "границы поясничного региона справа и слева асимметричны. ";
            }
        } else {
            if (angle_waist > 2)
                if (leftWaist.y > rightWaist.y) {
                    isWaist = true;
                    waistString = "верхняя и нижняя границы поясничного региона слева приближены друг к другу,  справа - взаимоудалены. ";
                } else {
                    isWaist = true;
                    waistString = "верхняя и нижняя границы поясничного региона справа приближены друг к другу, слева - взаимоудалены. ";
                }
        }

//        if (isNeck && isBrust && !isWaist)
//            recommendation.append("Постуральный мышечный дисбаланс в шейном и грудном отделах позвоночника. ");
//        else if (isNeck && !isBrust && isWaist)
//            recommendation.append("Постуральный мышечный дисбаланс в шейном и поясничном отделах позвоночника. ");
//        else if (isNeck && isBrust && isWaist)
//            recommendation.append("Постуральный мышечный дисбаланс во всех трех отделах отделах позвоночника (шейный, грудной, поясничный). ");
//        else if (!isNeck && isBrust && isWaist)
//            recommendation.append("Постуральный мышечный дисбаланс в грудном и поясничном отделах позвоночника. ");
//        else if (isNeck)
//            recommendation.append(frontNeckString);
//        else if (isBrust)
//            recommendation.append(frontBrustString);
//        else if (isWaist)
//            recommendation.append(frontWaistString);

        if (isNeck) {
            recommendation.append(frontNeckString);
            recommendation.append(neckString);
        }

        if (isBrust) {
            recommendation.append(frontBrustString);
            recommendation.append(brustString);
        }

        if (isWaist) {
            recommendation.append(frontWaistString);
            recommendation.append(waistString);
        }

    }

    @Override
    public String toString() {
        return "ChromakeyImageBack{" +
                "rightArmpit=" + rightArmpit +
                ", leftArmpit=" + leftArmpit +
                ", rightArmpitUp=" + rightArmpitUp +
                ", leftArmpitUp=" + leftArmpitUp +
                ", centerOfBody=" + centerOfBody +
                ", leftEar=" + leftEar +
                ", rightEar=" + rightEar +
                ", leftWaist=" + leftWaist +
                ", rightWaist=" + rightWaist +
                ", leftBelt=" + leftBelt +
                ", rightBelt=" + rightBelt +
                ", rightEarDist=" + rightEarDist +
                ", leftEarDist=" + leftEarDist +
                ", angle_ears_shldrs=" + angle_ears_shldrs +
                ", angle_shldrs_waist=" + angle_shldrs_waist +
                ", angle_waist_belt=" + angle_waist_belt +
                ", angle_shoulders=" + angle_shoulders +
                ", angle_ears=" + angle_ears +
                ", angle_waist=" + angle_waist +
                '}';
    }

    public void initSVGpic() {
        addPartOfBody("HEAD", getAngle(rightEar, leftEar));
        addPartOfBody("SHOULDERS", getAngle(rightArmpitUp, leftArmpitUp));
        addPartOfBody("WAIST", getAngle(rightWaist, leftWaist));
        addPartOfBody("LOVER_BACK", getAngle(rightBelt, leftBelt));

        if (compare((int) rightEarDist, (int) leftEarDist, 10) == 1) {
            svgParts.add(SVG.SVGPart.BACK_FOOT_LEFT);
        } else if (compare((int) rightEarDist, (int) leftEarDist, 10) == -1) {
            svgParts.add(SVG.SVGPart.BACK_FOOT_RIGHT);
        } else {
            svgParts.add(SVG.SVGPart.BACK_FOOT_NORM);
        }

        svgParts.add(SVG.SVGPart.BACK_HEAD_SHOULDERS_ADAPTER);
        svgParts.add(SVG.SVGPart.BACK_SHOULDERS_WAIST_ADAPTER);
        svgParts.add(SVG.SVGPart.BACK_WAIST_LOVER_BACK_ADAPTER);
        svgParts.add(SVG.SVGPart.BACK_LOVER_BACK_FOOT_ADAPTER);
    }

    private void addPartOfBody(String name, double angle) {
        String namePart = "BACK_%s_%s_%d";
        int minLimit = 0;
        int maxLimit = 10;

        if (angle > minLimit) {
            if (angle < maxLimit) svgParts.add(SVG.SVGPart.valueOf(String.format(namePart, name.toUpperCase(), "RIGHT", 5)));
            else svgParts.add(SVG.SVGPart.valueOf(String.format(namePart, name.toUpperCase(), "RIGHT", 10)));
        } else if (angle < minLimit) {
            if (angle > -maxLimit) svgParts.add(SVG.SVGPart.valueOf(String.format(namePart, name.toUpperCase(), "LEFT", 5)));
            else svgParts.add(SVG.SVGPart.valueOf(String.format(namePart, name.toUpperCase(), "LEFT", 10)));
        } else {
            svgParts.add(SVG.SVGPart.valueOf(String.format(namePart, name.toUpperCase(), "NORM", 0)));
        }
    }
}
