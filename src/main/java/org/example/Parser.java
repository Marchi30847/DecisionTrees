package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private Parser() {}

    public static List<LabeledData> parse(InputStream input) {
        List<LabeledData> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("} - ");
                if (split.length != 2) {
                    throw new IllegalArgumentException("Invalid line format");
                }

                Map<String, String> paramVal = new HashMap<>();
                String[] pairs = split[0]
                        .replace("{", "")
                        .split(", ");
                for (String pair : pairs) {
                    String[] key_value = pair.split(":");
                    if (key_value.length != 2) {
                        throw new IllegalArgumentException("Invalid parameter format");
                    }

                    String key = key_value[0]
                            .trim();

                    String value = key_value[1]
                            .trim();

                    paramVal.put(key, value);
                }

                String label = split[1]
                        .trim();

                data.add(new LabeledData(paramVal, label));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return data;
    }
}
