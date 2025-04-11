package org.example;

import java.util.*;

/**
 * Represents a node in a decision tree.
 * <p>
 * Each node contains a string value (which could be a parameter name or a decision label)
 * and a map of child nodes indexed by the branch value (e.g., "sunny", "high", "true").
 * <p>
 * This class is used during tree construction and later for tree traversal or visualization.
 */
public class TreeNode {
    private final String value;
    private final Map<String, TreeNode> children;

    /**
     * Constructs a TreeNode with the specified value.
     *
     * @param value the value stored in this node (e.g., a parameter name or a label)
     */
    public TreeNode(String value) {
        this.value = value;
        this.children = new LinkedHashMap<>();
    }

    /**
     * Returns the child node corresponding to a specific branch value.
     *
     * @param key the branch value to look for
     * @return an {@link Optional} containing the child node if it exists, or empty otherwise
     */
    public Optional<TreeNode> getChild(String key) {
        return Optional.ofNullable(children.get(key));
    }

    /**
     * Adds a child node associated with a specific branch value.
     *
     * @param key   the branch value (e.g., "yes", "no", "high")
     * @param child the child node to be added
     */
    public void addChild(String key, TreeNode child) {
        children.put(key, child);
    }

    /**
     * Returns the value stored in this node.
     *
     * @return the node's value
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns a tree-like string representation of this node and its descendants.
     *
     * @return a formatted string showing the structure of the subtree rooted at this node
     */
    @Override
    public String toString() {
        return toStringHelper("");
    }

    /**
     * Recursive helper method that builds a tree-like string representation.
     * Uses line-drawing characters to represent tree branches.
     *
     * @param prefix the string prefix used for formatting and indentation
     * @return a formatted string representing this node and its children
     */
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