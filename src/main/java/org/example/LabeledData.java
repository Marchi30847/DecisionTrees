package org.example;

import java.util.Map;

public record LabeledData(Map<String, String> parameters, String label) {
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LabeledData:\n");
        builder.append("parameters=");
        builder.append("\n{\n");
        builder.append(parameters.entrySet().stream()
                .map(e -> "\t" + e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + ", \n" + b)
                .orElse(""));
        builder.append("\n}\n");
        builder.append("label=");
        builder.append(label);
        builder.append("\n");
        return builder.toString();
    }
}
