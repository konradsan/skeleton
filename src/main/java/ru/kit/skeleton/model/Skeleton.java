package ru.kit.skeleton.model;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by mikha on 13.01.2017.
 */
public class Skeleton {

    public static final String IMAGE_NAME_BACK = "back_photo.png";
    public static final String IMAGE_NAME_SAGITTAL = "sagittal_photo.png";
    private static String path;

    public Skeleton(String path) {
        Skeleton.path = path;
    }

    public static boolean hasPhoto() {
        return new File(path + IMAGE_NAME_BACK).exists() && new File(path + IMAGE_NAME_SAGITTAL).exists();
    }

    public static String getPath() {
        return path;
    }
}
