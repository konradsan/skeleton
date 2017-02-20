package ru.kit.skeleton.model;

import ru.kit.skeleton.repository.BackPlane;
import ru.kit.skeleton.repository.Plane;
import ru.kit.skeleton.repository.SagittalPlane;

/**
 * Created by mikha on 27.01.2017.
 */
public class SVG {

    public static String getPath(boolean isMan, Plane plane) {
        StringBuilder path = new StringBuilder("SVGpart\\");
        if (isMan) path.append("male\\");
        else path.append("woman\\");

        if (plane instanceof BackPlane) path.append("back\\");
        else if (plane instanceof SagittalPlane) path.append("sagittal\\");

        return path.toString();
    }

    public enum SVGPart {

        BACK_HEAD_NORM_0("head_norm.svg"),
        BACK_HEAD_LEFT_5("head_left_5.svg"),
        BACK_HEAD_LEFT_10("head_left_10.svg"),
        BACK_HEAD_RIGHT_5("head_right_5.svg"),
        BACK_HEAD_RIGHT_10("head_right_10.svg"),
        BACK_HEAD_SHOULDERS_ADAPTER("head_shoulders_adapter.svg"),

        BACK_SHOULDERS_NORM_0("shoulders_norm.svg"),
        BACK_SHOULDERS_LEFT_5("shoulders_left_5.svg"),
        BACK_SHOULDERS_LEFT_10("shoulders_left_10.svg"),
        BACK_SHOULDERS_RIGHT_5("shoulders_right_5.svg"),
        BACK_SHOULDERS_RIGHT_10("shoulders_right_10.svg"),
        BACK_SHOULDERS_WAIST_ADAPTER("sholders_waist_adapter.svg"),

        BACK_WAIST_NORM_0("waist_norm.svg"),
        BACK_WAIST_LEFT_5("waist_left_5.svg"),
        BACK_WAIST_LEFT_10("waist_left_10.svg"),
        BACK_WAIST_RIGHT_5("waist_right_5.svg"),
        BACK_WAIST_RIGHT_10("waist_right_10.svg"),
        BACK_WAIST_LOVER_BACK_ADAPTER("waist_lover_back_adapter.svg"),

        BACK_LOVER_BACK_NORM_0("lover_back_norm.svg"),
        BACK_LOVER_BACK_LEFT_5("lover_back_left_5.svg"),
        BACK_LOVER_BACK_LEFT_10("lover_back_left_10.svg"),
        BACK_LOVER_BACK_RIGHT_5("lover_back_right_5.svg"),
        BACK_LOVER_BACK_RIGHT_10("lover_back_right_10.svg"),
        BACK_LOVER_BACK_FOOT_ADAPTER("lover_back_foot_adapter.svg"),

        BACK_FOOT_NORM("foot_norm.svg"),
        BACK_FOOT_LEFT("foot_left.svg"),
        BACK_FOOT_RIGHT("foot_right.svg"),

        SAGITTAL_NORM("norm.svg"),
        SAGITTAL_SKIF_SLOR("skif-slor.svg"),
        SAGITTAL_UKIF("ukif.svg"),
        SAGITTAL_UKIF_ULOR("ukif-ulor.svg")
        ;
        public String fileName;

        SVGPart(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
