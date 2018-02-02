package ru.kit.skeleton.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by mikha on 13.01.2017.
 */
public class Skeleton {

    private static final Logger LOG = LoggerFactory.getLogger(Skeleton.class);

    public static final String ORIGIN_IMAGE_BACK = "back_photo.png";
    public static final String ORIGIN_IMAGE_SAGITTAL = "sagittal_photo.png";
    public static final String SVG_BACK = "back.svg";
    public static final String SVG_SAGITTAL = "sagittal.svg";
    public static final String RESULT_IMAGE_BACK = SVG_BACK + ".jpg";
    public static final String RESULT_IMAGE_SAGITTAL = SVG_SAGITTAL + ".jpg";
    private static String PATH;
    private boolean isMan;

    public Skeleton(String path, boolean isMan) {
        Skeleton.PATH = path;
        this.isMan = isMan;
    }

    public static boolean hasPhoto() {
        LOG.info(new File(PATH + ORIGIN_IMAGE_BACK).getAbsolutePath());
        boolean filesExists = new File(PATH + ORIGIN_IMAGE_BACK).exists() && new File(PATH + ORIGIN_IMAGE_SAGITTAL).exists();
        LOG.info("Old files " + (filesExists ? "exists" : "not exists"));
        return filesExists;
    }

    public static String getPath() {
        return PATH;
    }

    public boolean isMan() {
        return isMan;
    }
}
