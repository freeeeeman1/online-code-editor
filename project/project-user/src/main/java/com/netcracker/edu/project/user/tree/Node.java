package com.netcracker.edu.project.user.tree;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Node<T extends Element> {

    private final TreeSet<Node<T>> children;
    private final String path;
    private final Node<T> parent;
    private final T value;

    public Node(Node<T> parent, String path, T value) {
        this.parent = parent;
        this.value = value;
        this.path = path;
        this.children = new TreeSet<>(Comparator.comparing(Node::getPath));
    }

    public Set<Node<T>> getChildren() {
        return children;
    }

    public String getName() {
        return value.getName();
    }

    public T getValue() {
        return value;
    }

    public String getPath() {
        return path;
    }

    public Node<T> getRoot() {
        return this.parent == null ? this : parent.getRoot();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Node<T> that = (Node<T>) o;
        return Objects.equals(this.path, that.path)
                && Objects.equals(this.parent, that.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.path, this.parent);
    }

    @Override
    public String toString() {
        if (this.parent != null) {
            return "{" +
                    " \"path\":" + "\"" + this.path + "\"," +
                    " \"File\":" + this.value + "," +
                    " \"parent\":" + "\"" + parent.path + "\"," +
                    " \"children\":" + this.children +
                    "}";
        } else {
            return "{" +
                    " \"path\":" + "\"" + this.path + "\"," +
                    " \"File\":" + this.value + "," +
                    " \"parent\":" + "\"" + null + "\"," +
                    " \"children\":" + this.children +
                    "}";
        }
    }
}
