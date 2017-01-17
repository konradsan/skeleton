package ru.kit.skeleton.model;

import java.awt.*;

/**
 * Created by mikha on 13.01.2017.
 */
public class Step {
    private String name;
    private String description;
    private Point point;

    public Step(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "Step{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", point=" + point +
                '}';
    }
}
