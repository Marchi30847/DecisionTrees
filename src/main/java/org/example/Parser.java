package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for parsing labeled data from an input stream.
 * <p>
 * This class provides a static method to parse data where each line represents a data instance
 * in the format: <br>
 * <pre>
 * {key1: value1, key2: value2, ...} - label
 * </pre>
 * Each line is parsed into a {@link LabeledData} object containing the parameters and the label.
 *
 * This class cannot be instantiated.
 */
public class Parser {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Parser() {}

    /**
     * Parses labeled data from the given input stream.
     * <p>
     * Each line must follow the format:<br>
     * <pre>
     * {feature1: value1, feature2: value2, ...} - label
     * </pre>
     * Lines that do not conform to this format will cause an {@link IllegalArgumentException}.
     *
     * @param input the {@link InputStream} to read data from (e.g., a file or resource stream)
     * @return a list of {@link LabeledData} objects parsed from the input
     * @throws RuntimeException if an {@link IOException} occurs during reading
     * @throws IllegalArgumentException if any line or feature-value pair has an invalid format
     */
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

                    String key = key_value[0].trim();
                    String value = key_value[1].trim();
                    paramVal.put(key, value);
                }

                String label = split[1].trim();
                data.add(new LabeledData(paramVal, label));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return data;
    }
}