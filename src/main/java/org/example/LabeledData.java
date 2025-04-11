package org.example;

import java.util.Map;

/**
 * Represents a single labeled data instance used for training or testing a decision tree.
 * <p>
 * Each instance consists of:
 * <ul>
 *   <li>A map of feature names to their values (e.g., "humidity" → "high")</li>
 *   <li>A classification label (e.g., "yes" or "no")</li>
 * </ul>
 *
 * This class is used to store one row of input in a decision tree algorithm,
 * where the features are used for branching and the label is the target outcome.
 *
 * @param parameters a map of feature names and their corresponding values
 * @param label      the class label or decision associated with this instance
 */
public record LabeledData(Map<String, String> parameters, String label) {

    /**
     * Returns a human-readable string representation of this {@code LabeledData} instance,
     * including its parameters and label.
     *
     * @return a formatted string showing the parameter map and the label
     */
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