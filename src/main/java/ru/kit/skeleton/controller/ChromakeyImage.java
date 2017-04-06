package ru.kit.skeleton.controller;

import ru.kit.skeleton.model.SVG;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by mikha on 27.01.2017.
 */
public abstract class ChromakeyImage {
    protected Set<SVG.SVGPart> svgParts = new HashSet<>();
    protected StringBuilder recommendation;

    public String getRecommendation() {
        if (recommendation == null || recommendation.toString().equals(""))
            initRecommendation();
        return recommendation.toString();
    }

    protected int compare(int x, int y, int eps) {
        if (equals(x, y, eps))
            return 0;
        else
            return x > y ? 1 : -1;
    }

    protected boolean equals(int x, int y, int eps) {
        return Math.abs(x - y) <= eps;
    }

    protected abstract void initRecommendation();

    protected static Point getVector(Point begin, Point end) {
        return new Point(end.x - begin.x, end.y - begin.y);
    }

    protected static double scalarMult(Point v1, Point v2) {
        return (double) (v1.x * v2.x + v1.y * v2.y);
    }

    protected static double lenVector(Point v) {
        return Math.sqrt((double) (v.x * v.x) + (double) (v.y * v.y));
    }

    protected static double distance(Point p1, Point p2) {
        return lenVector(new Point(p1.x - p2.x, p1.y - p2.y));
    }

    public static double calcAngle(Point v1, Point v2) {
        double cos = scalarMult(v1, v2) / (lenVector(v1) * lenVector(v2));
        return Math.toDegrees(Math.acos(cos));
    }

    protected static double calcAngle(Point begin1, Point end1, Point begin2, Point end2) {
        return calcAngle(getVector(begin1, end1), getVector(begin2, end2));
    }

    public static float getAngle(Point v1, Point v2) {
        float angle = (float) Math.toDegrees(Math.atan2(v1.y - v2.y, v1.x - v2.x));

        return angle;
    }



    public Set<SVG.SVGPart> getSvgParts() {
        if (svgParts.size() == 0) {
            initSVGpic();
        }
        return svgParts;
    }

    protected abstract void initSVGpic();
}

class Test{
    public static void main(String[] args) {
        Point leftEar = new Point(50, 40);
        Point rightEar = new Point(80, 20);

        Point leftShl = new Point(30, 60);
        Point rigtShl = new Point(90, 40);



    }
}
