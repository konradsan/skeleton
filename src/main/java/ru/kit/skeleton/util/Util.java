package ru.kit.skeleton.util;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by mikha on 19.01.2017.
 */
public class Util {
    public static void writeJSON(String path, Map<String, String> map)
    {
        try {
            String jsonFileName = path + "skeleton.json";
            BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFileName));
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

    private static JSONObject createJSON(Map<String, String> map)
    {

        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<String, String> pair : map.entrySet()) {
            jsonObject.put(pair.getKey(), pair.getValue());
        }

        return jsonObject;
    }
}
