package com.netcracker.edu.project.user.tree;

import com.netcracker.edu.project.user.exception.FileException;
import com.netcracker.edu.project.user.exception.NodeException;
import com.netcracker.edu.project.user.project.File;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Tree<T extends Element> {

    private final Node<T> root;
    private final Lock lock;

    public Tree(T value) {
        this.lock = new ReentrantLock();
        if (!value.getPath().isEmpty()) {
            this.root = new Node<>(null, value.getPath(), value);
        } else {
            throw new NullPointerException();
        }
    }

    public void add(T element) throws FileException {
        try {
            lock.lock();
            addInternal(element, root);
        } finally {
            lock.unlock();
        }
    }

    public T find(String path) throws FileException {
        try {
            lock.lock();
            return findInternal(path, root);

        } catch (NodeException e) {
            throw new FileException(new File(path, path, ""), "file was not found");

        } finally {
            lock.unlock();
        }
    }

    public void delete(String path) {
        try {
            lock.lock();
            deleteInternal(path, root);
        } finally {
            lock.unlock();
        }
    }

    private boolean addInternal(T element, Node<T> node) throws FileException {
        if (belongsTo(element.getPath(), node)) {

            if (!node.getChildren().isEmpty()) {
                childExists(node, element);
            }
            node.getChildren().add(new Node<>(node, element.getPath(), element));
            return true;
        }

        for (Node<T> child : node.getChildren()) {
            if (addInternal(element, child)) {
                return true;
            }
        }

        return false;
    }

    private void childExists(Node<T> node, T element) throws FileException {
        if (node.getChildren().stream()
                .anyMatch(child -> child.getPath().equals(element.getPath()))) {

            throw new FileException(node.getValue(), "File already exists");
        }
    }

    private boolean belongsTo(String path, Node<T> node) {
        if (path.startsWith(node.getPath())) {
            String elementMinusParentPath = path.substring(node.getPath().length());

            return !elementMinusParentPath.substring(1).contains("/");
        }

        return false;
    }

    private T findInternal(String path, Node<T> node) {
        if (belongsTo(path, node)) {
            String nodeName = path.substring(path.lastIndexOf("/") + 1);

            for (Node<T> child : node.getChildren()) {
                if (child.getName().equals(nodeName)) {
                    return child.getValue();
                }
            }

            throw new NodeException("Node not found");
        }

        for (Node<T> child : node.getChildren()) {
            return findInternal(path, child);
        }

        throw new NodeException("Node not found");
    }

    private void deleteInternal(String path, Node<T> node) {
        if (belongsTo(path, node)) {
            String nodeName = path.substring(path.lastIndexOf("/") + 1);

            node.getChildren().removeIf(child -> child.getName().equals(nodeName));
        }

        for (Node<T> child : node.getChildren()) {
            deleteInternal(path, child);
        }
    }

    @Override
    public String toString() {
        return root.getRoot().toString();
    }
}
