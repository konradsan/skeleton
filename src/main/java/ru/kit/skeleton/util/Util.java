package ru.kit.skeleton.util;

import javafx.scene.shape.Rectangle;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.json.JSONObject;
import ru.kit.skeleton.model.SVG;
import ru.kit.skeleton.model.Skeleton;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by mikha on 19.01.2017.
 */
public class Util {
    public static void writeJSON(String path, Map<String, String> map) {
        try {
            String jsonFileName = path + "skeleton.json";
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFileName), "UTF-8"));
            Throwable var2 = null;

            try {
                writer.write(createJSON(map).toString());
            } catch (Throwable var12) {
                var2 = var12;
                throw var12;
            } finally {
                if (var2 != null) {
                    try {
                        writer.close();
                    } catch (Throwable var11) {
                        var2.addSuppressed(var11);
                    }
                } else {
                    writer.close();
                }
            }
        } catch (IOException var14) {
            var14.printStackTrace();
        }
    }

    private static JSONObject createJSON(Map<String, String> map) {

        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<String, String> pair : map.entrySet()) {
            jsonObject.put(pair.getKey(), pair.getValue());
        }

        return jsonObject;
    }

    public static void writeSVG(String path, Set<SVG.SVGPart> svgParts, String currentFileName) {
        try {
            List<String> fileNames = svgParts.stream().map(svgPart -> path + svgPart.getFileName()).collect(Collectors.toList());
            sculptSVG(fileNames, currentFileName);
            System.out.println("Starting converting " + currentFileName + " to JPG");
            svgToJpgConvert(currentFileName, 900, 1000);
            System.out.println("SVG Converted to JPG");
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }

    }

    public static void sculptSVG(List<String> fileNames, String currentFileName) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(currentFileName))) {
            writer.append("<svg version=\"1.1\" id=\"Layer_1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\"\n" +
                    "\t viewBox=\"0 0 612 792\" enable-background=\"new 0 0 612 792\" xml:space=\"preserve\">");

            for (String fileName : fileNames) {
                try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                    while (reader.ready()) {
                        String data = reader.readLine();
                        writer.append(data);
                    }
                }
            }
            writer.append("</svg>");

        } catch (IOException e)  {
            e.printStackTrace();
        }

    }

//    public static void svgToJpgConvert(String fileName) throws FileNotFoundException {
//        new SVGExport().setInput(new FileInputStream(fileName + ".png"))
//                .setOutput(new FileOutputStream(fileName + ".eps"))
//                .setTranscoder(Format.SVG)
//                .transcode();
//    }

    public static void svgToJpgConvert(String fileName, double width, double height) throws IOException, TranscoderException {
        String svgURI = Paths.get(fileName).toUri().toURL().toString();
        TranscoderInput inputSvgImage = new TranscoderInput(svgURI);

        OutputStream jpgOutputStream = new FileOutputStream(fileName + ".jpg");
        TranscoderOutput outputPngImage = new TranscoderOutput(jpgOutputStream);

        JPEGTranscoder transcoder = new JPEGTranscoder();

        transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
                new Float(1.0));
        transcoder.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(width));
        transcoder.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, new Float(height));
        transcoder.transcode(inputSvgImage, outputPngImage);

        jpgOutputStream.flush();
        jpgOutputStream.close();

//        new File(fileName).delete();
    }


    public static BufferedImage cropImage(String fileName) {
        BufferedImage result = null;
        try {
            BufferedImage originImage = ImageIO.read(new File(fileName));
            Point center = new Point(originImage.getWidth() / 2, originImage.getHeight() / 2);
            System.out.println(originImage.getWidth());
            System.out.println(originImage.getHeight());
            Rectangle rect = null;
            if (fileName.contains(Skeleton.RESULT_IMAGE_BACK)) {
                rect = new Rectangle(center.getX() - 380, center.getY() - 400, 740, 860);
            } else {
                rect = new Rectangle(center.getX() - 110, center.getY() - 400, 180, 860);
            }
            result = originImage.getSubimage((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void writeImage(BufferedImage image, String fileName) {
        try {
            ImageIO.write(image, "jpg", new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        /* Util.svgToJpgConvert("D:/nikitaSolovyevProjects/skeleton/test/back.svg", 900, 1000);
         BufferedImage imageBack = Util.cropImage("D:/nikitaSolovyevProjects/skeleton/test/" + Skeleton.RESULT_IMAGE_BACK);
         Util.writeImage(imageBack, "D:/nikitaSolovyevProjects/skeleton/test/" + Skeleton.RESULT_IMAGE_BACK);*/

        try {
            Util.svgToJpgConvert("D:/nikitaSolovyevProjects/skeleton/test/back.svg", 900, 1000);
            BufferedImage imageBack = null;//Util.cropImage(Skeleton.getPath() + Skeleton.RESULT_IMAGE_BACK);
            imageBack = ImageIO.read(new File("D:/nikitaSolovyevProjects/skeleton/test/" + Skeleton.RESULT_IMAGE_BACK));
            Util.writeImage(imageBack, "D:/nikitaSolovyevProjects/skeleton/test/" + Skeleton.RESULT_IMAGE_BACK);
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
          /*  Util.svgToJpgConvert("D:/projects/ae/skeleton1/test/sagittal.svg", 900, 1000);
            BufferedImage sagittalBack = Util.cropImage("D:/projects/ae/skeleton1/test/" + Skeleton.RESULT_IMAGE_SAGITTAL);
            Util.writeImage(sagittalBack, "D:/projects/ae/skeleton1/test/" + Skeleton.RESULT_IMAGE_SAGITTAL);*/


    }

}
