package org.example;

import java.util.*;

public class TreeNode {
    private final String value;
    private final Map<String, TreeNode> children;

    public TreeNode(String value) {
        this.value = value;
        this.children = new LinkedHashMap<>(); // сохраняем порядок добавления
    }

    public Optional<TreeNode> getChild(String key) {
        return Optional.ofNullable(children.get(key));
    }

    public void addChild(String key, TreeNode child) {
        children.put(key, child);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return toStringHelper("");
    }

    private String toStringHelper(String prefix) {
        StringBuilder sb = new StringBuilder();

        sb.append(prefix);
        if (!prefix.isEmpty()) {
            sb.append("└── ");
        }
        sb.append(value).append("\n");

        List<Map.Entry<String, TreeNode>> entries = new ArrayList<>(children.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, TreeNode> entry = entries.get(i);
            boolean last = i == entries.size() - 1;
            String childPrefix = prefix + (prefix.isEmpty() ? "" : ("    "));
            sb.append(childPrefix)
                    .append(last ? "└── " : "├── ")
                    .append("[").append(entry.getKey()).append("]").append("\n");
            sb.append(entry.getValue().toStringHelper(childPrefix + (last ? "    " : "│   ")));
        }

        return sb.toString();
    }
}