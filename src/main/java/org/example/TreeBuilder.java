package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class TreeBuilder {
    private TreeNode root;
    private final List<String> params;
    private final List<LabeledData> data;
    private final Double entropyThreshold;

    public TreeBuilder(List<LabeledData> data, Double entropyThreshold) {
        this.data = data;
        this.entropyThreshold = entropyThreshold;

        this.params = new ArrayList<>(data.getFirst().parameters().keySet());

        for (LabeledData d : data) {
            System.out.println(d);
        }
    }

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

    private List<LabeledData> filterByParamValue(List<LabeledData> data, String param, String value) {
        return data.stream()
                .filter(line -> Objects.equals(line.parameters().get(param), value))
                .toList();
    }

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

    private String chooseBestNode(List<LabeledData> curData, List<String> curParams) {
        return curParams.stream()
                .map(param -> Map.entry(param, calculateEntropyForParam(curData, param)))
                .min(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("unknown");
    }

    private List<String> getParameterValues(String param) {
        return data.stream()
                .map(line -> line.parameters().get(param))
                .distinct()
                .collect(Collectors.toList());
    }

    public TreeNode buildTree() {
        build(data, params, null, null);
        return root;
    }

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

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}