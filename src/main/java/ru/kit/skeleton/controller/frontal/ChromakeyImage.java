package ru.kit.skeleton.controller.frontal;

import java.awt.*;

/**
 * Created by Anton on 28.06.2016.
 */
public class ChromakeyImage {
    Point rightArmpit, leftArmpit,
            rightArmpitUp, leftArmpitUp, centerOfBody,
            leftEar, rightEar,
            leftWaist, rightWaist,
            leftBelt, rightBelt;
    double rightEarDist, leftEarDist;

    double angle_ears_shldrs, angle_shldrs_waist, angle_waist_belt;
    double angle_shoulders, angle_ears, angle_waist;

    public ChromakeyImage(String path) {

        angle_shldrs_waist = Math.abs(calcAngle(getVector(leftArmpitUp, rightArmpitUp),
                getVector(leftWaist, rightWaist)));
        angle_waist_belt = Math.abs(calcAngle(getVector(leftWaist, rightWaist),
                getVector(leftBelt, rightBelt)));
    }

    public ChromakeyImage(Point rightArmpit, Point leftArmpit, Point rightArmpitUp, Point leftArmpitUp, Point leftEar, Point rightEar, Point leftWaist, Point rightWaist, Point leftBelt, Point rightBelt) {
        this.rightArmpit = rightArmpit;
        this.leftArmpit = leftArmpit;
        this.rightArmpitUp = rightArmpitUp;
        this.leftArmpitUp = leftArmpitUp;
        this.leftEar = leftEar;
        this.rightEar = rightEar;
        this.leftWaist = leftWaist;
        this.rightWaist = rightWaist;
        this.centerOfBody = new Point((int)((leftWaist.x + rightWaist.x) / 2), (int)((leftWaist.y + rightWaist.y) / 2));
        this.leftBelt = leftBelt;
        this.rightBelt = rightBelt;

        angle_shldrs_waist = Math.abs(calcAngle(getVector(leftArmpitUp, rightArmpitUp),
                getVector(leftWaist, rightWaist)));
        angle_waist_belt = Math.abs(calcAngle(getVector(leftWaist, rightWaist),
                getVector(leftBelt, rightBelt)));
        paintAngleGraphics();
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




    private static Point getVector(Point begin, Point end) {
        return new Point(end.x - begin.x, end.y - begin.y);
    }

    private static double scalarMult(Point v1, Point v2) {
        return (double) (v1.x * v2.x + v1.y * v2.y);
    }

    private static double lenVector(Point v) {
        return Math.sqrt((double) (v.x * v.x) + (double) (v.y * v.y));
    }

    private static double distance(Point p1, Point p2) {
        return lenVector(new Point(p1.x - p2.x, p1.y - p2.y));
    }

    private static double calcAngle(Point v1, Point v2) {
        double cos = scalarMult(v1, v2) / (lenVector(v1) * lenVector(v2));
        return Math.toDegrees(Math.acos(cos));
    }

    private static double calcAngle(Point begin1, Point end1, Point begin2, Point end2) {
        return calcAngle(getVector(begin1, end1), getVector(begin2, end2));
    }








    // ================= RECOMENDATIONS ========================


    private String recommendation = "";

    public String getRecommendation() {
        if (recommendation.equals(""))
            initRecommendation();
        return recommendation;
    }

    private void initRecommendation() {
        // ОЦТ
        switch (compare(rightEarDist, leftEarDist, 10)) {
            case 1:
                recommendation += "Неоптимальный статический стереотип: общий центр тяжести смещен вправо, опора на правую ногу.\n";
                break;
            case -1:
                recommendation += "Неоптимальный статический стереотип: общий центр тяжести смещен влево, опора преимущественно на левую ногу.\n";
                break;
        }
        // шейный отдел
        boolean isNeck = false;
        String neckString = "";
        String frontNeckString = "Постуральный мышечный дисбаланс в шейном отделе позвоночника.\n";
        if (angle_ears_shldrs <= 2) {
            if (leftEar.y > rightEar.y) {
                isNeck = true;
                neckString = "Голова наклонена влево, правое плечо выше левого.\n";
            } else if (leftEar.y < rightEar.y) {
                isNeck = true;
                neckString = "Голова наклонена вправо, левое плечо выше правого.\n";
            }
        } else {
            if (leftEar.y > rightEar.y) {
                isNeck = true;
                neckString = "Голова наклонена влево, левое плечо выше правого.\n";
            } else {
                isNeck = true;
                neckString = "Голова наклонена вправо, правое плечо выше левого.\n";
            }
        }
        // грудной отдел
        boolean isBrust = false;
        String brustString = "";
        String frontBrustString = "Постуральный мышечный дисбаланс в грудном отделе позвоночника.\n";
        if (angle_shldrs_waist <= 2) {
            if (leftArmpitUp.y != rightArmpitUp.y) {
                isBrust = true;
                brustString = "Границы грудного региона справа и слева асимметричны.\n";
            }
        } else {
            if (angle_shoulders > 2)
                if (leftArmpitUp.y > rightArmpitUp.y) {
                    isBrust = true;
                    brustString = "Верхняя и нижняя границы грудного региона слева приближены друг к другу,  справа - взаимоудалены.\n";
                } else {
                    isBrust = true;
                    brustString = "Верхняя и нижняя границы грудного региона справа приближены друг к другу, слева - взаимоудалены.\n";
                }
        }
        // поясничный отдел
        boolean isWaist = false;
        String waistString = "";
        String frontWaistString = "Постуральный мышечный дисбаланс в поясничном отделе позвоночника.\n";
        if (angle_waist_belt <= 2) {
            if (leftWaist.y != rightWaist.y) {
                isWaist = true;
                waistString = "Границы поясничного региона справа и слева асимметричны.\n";
            }
        } else {
            if (angle_waist > 2)
                if (leftWaist.y > rightWaist.y) {
                    isWaist = true;
                    waistString = "Верхняя и нижняя границы поясничного региона слева приближены друг к другу,  справа - взаимоудалены.\n";
                } else {
                    isWaist = true;
                    waistString = "Верхняя и нижняя границы поясничного региона справа приближены друг к другу, слева - взаимоудалены.\n";
                }
        }

        if (isNeck && isBrust && !isWaist)
            recommendation += "Постуральный мышечный дисбаланс в шейном и грудном отделах позвоночника.\n";
        else if (isNeck && !isBrust && isWaist)
            recommendation += "Постуральный мышечный дисбаланс в шейном и поясничном отделах позвоночника.\n";
        else if (isNeck && isBrust && isWaist)
            recommendation += "Постуральный мышечный дисбаланс во всех трех отделах отделах позвоночника (шейный, грудной, поясничный)\n";
        else if (!isNeck && isBrust && isWaist)
            recommendation += "Постуральный мышечный дисбаланс в грудном и поясничном отделах позвоночника.\n";
        else if (isNeck)
            recommendation += frontNeckString;
        else if (isBrust)
            recommendation += frontBrustString;
        else if (isWaist)
            recommendation += frontWaistString;

        if (isNeck)
            recommendation += neckString;
        if (isBrust)
            recommendation += brustString;
        if (isWaist)
            recommendation += waistString;
    }

    private int compare(double x, double y, double eps) {
        if (equals(x, y, eps))
            return 0;
        else
            return x > y ? 1 : -1;
    }

    private boolean equals(double x, double y, double eps) {
        return Math.abs(x - y) <= eps;
    }

    @Override
    public String toString() {
        return "ChromakeyImage{" +
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
                ", recommendation='" + recommendation + '\'' +
                '}';
    }
}
