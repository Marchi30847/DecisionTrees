package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<LabeledData> data = Parser.parse(
                Main.class.getClassLoader().getResourceAsStream("data.txt")
        );

        for (LabeledData dataItem : data) {
            System.out.println(dataItem);
        }

        TreeBuilder builder = new TreeBuilder(data, 0.1);
        TreeNode root = builder.buildTree();

        System.out.println(root);
    }
}