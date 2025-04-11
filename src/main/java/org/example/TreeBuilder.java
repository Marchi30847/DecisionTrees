package org.example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class responsible for building a decision tree based on a list of labeled training data.
 * <p>
 * The tree is constructed using the concept of information gain (entropy) to determine
 * the best parameter to split on at each node. The building process stops when the entropy
 * of a node falls below a specified threshold.
 */
public class TreeBuilder {
    private TreeNode root;
    private final List<String> params;
    private final List<LabeledData> data;
    private final Double entropyThreshold;

    /**
     * Constructs a new TreeBuilder instance.
     *
     * @param data              the labeled training data to use for building the tree
     * @param entropyThreshold  the threshold below which no further splits will occur (used as stopping condition)
     */
    public TreeBuilder(List<LabeledData> data, Double entropyThreshold) {
        this.data = data;
        this.entropyThreshold = entropyThreshold;
        this.params = new ArrayList<>(data.getFirst().parameters().keySet());
    }

    /**
     * Calculates the expected entropy if the data is split based on the given parameter.
     *
     * @param curData the data to analyze
     * @param param   the parameter to calculate entropy for
     * @return the expected entropy value after splitting by the parameter
     */
    private Double calculateEntropyForParam(List<LabeledData> curData, String param) {
        int totalCount = curData.size();

        Map<String, Integer> valOccur = curData.stream()
                .map(line -> line.parameters().get(param))
                .collect(Collectors.toMap(
                        val -> val,
                        _ -> 1,
                        Integer::sum
                ));

        Map<String, Map<String, Integer>> valLabelOccur = curData.stream()
                .collect(Collectors.groupingBy(
                        line -> line.parameters().get(param),
                        Collectors.groupingBy(
                                LabeledData::label,
                                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                        )
                ));

        double entropy = 0.0;

        for (Map.Entry<String, Integer> entry : valOccur.entrySet()) {
            String paramValue = entry.getKey();
            int valueCount = entry.getValue();
            double pValue = (double) valueCount / totalCount;

            Map<String, Integer> labelCounts = valLabelOccur.get(paramValue);
            double valueEntropy = 0.0;
            for (int labelCount : labelCounts.values()) {
                double pLabel = (double) labelCount / valueCount;
                valueEntropy -= pLabel * log2(pLabel);
            }

            entropy += pValue * valueEntropy;
        }

        return entropy;
    }

    /**
     * Filters a list of labeled data to include only entries with a specific parameter value.
     *
     * @param data   the list of labeled data to filter
     * @param param  the parameter to filter by
     * @param value  the value the parameter must have
     * @return a list of labeled data matching the specified parameter and value
     */
    private List<LabeledData> filterByParamValue(List<LabeledData> data, String param, String value) {
        return data.stream()
                .filter(line -> Objects.equals(line.parameters().get(param), value))
                .toList();
    }

    /**
     * Determines the most frequent label among the entries where a given parameter equals a specified value.
     *
     * @param curData the data subset to examine
     * @param param   the parameter to match
     * @param value   the value of the parameter to filter by
     * @return the most common label for the filtered entries
     */
    private String getDominantLabelForParamValue(List<LabeledData> curData, String param, String value) {
        return filterByParamValue(curData, param, value).stream()
                .collect(Collectors.groupingBy(
                        LabeledData::label,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
    }

    /**
     * Chooses the parameter with the lowest expected entropy when splitting the data.
     *
     * @param curData    the current dataset
     * @param curParams  the list of remaining parameters to consider for splitting
     * @return the name of the best parameter to split on
     */
    private String chooseBestNode(List<LabeledData> curData, List<String> curParams) {
        return curParams.stream()
                .map(param -> Map.entry(param, calculateEntropyForParam(curData, param)))
                .min(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("unknown");
    }

    /**
     * Returns the distinct values for a given parameter across all data points.
     *
     * @param param the parameter name
     * @return a list of distinct values for that parameter
     */
    private List<String> getParameterValues(String param) {
        return data.stream()
                .map(line -> line.parameters().get(param))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Builds and returns the root of the decision tree.
     *
     * @return the root node of the generated decision tree
     */
    public TreeNode buildTree() {
        build(data, params, null, null);
        return root;
    }

    /**
     * Recursively builds the decision tree from the current data and parameter list.
     *
     * @param curData     the current subset of data to split
     * @param curParams   the list of parameters remaining for splitting
     * @param curNode     the current node in the tree (null if it's the root)
     * @param branchName  the branch label that leads to this node (null for the root)
     */
    private void build(List<LabeledData> curData, List<String> curParams, TreeNode curNode, String branchName) {
        String nodeValue = chooseBestNode(curData, curParams);
        TreeNode node = new TreeNode(nodeValue);

        if (root == null) {
            root = node;
        } else {
            curNode.addChild(branchName, node);
        }

        for (String val : getParameterValues(nodeValue)) {
            List<LabeledData> filtered = filterByParamValue(curData, nodeValue, val);

            if (calculateEntropyForParam(filtered, nodeValue) < entropyThreshold) {
                String label = getDominantLabelForParamValue(filtered, nodeValue, val);
                TreeNode child = new TreeNode(label);
                node.addChild(val, child);
                continue;
            }

            List<String> newParams = curParams.stream()
                    .filter(p -> !p.equals(nodeValue))
                    .toList();

            build(filtered, newParams, node, val);
        }
    }

    /**
     * Calculates the base-2 logarithm of a given number.
     *
     * @param x the input value
     * @return the base-2 logarithm of x
     */
    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}