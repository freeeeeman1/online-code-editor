package com.netcracker.edu.project.user.content;

import java.util.Objects;

public class FileContent {
    private final String path;
    private final String content;

    public FileContent(String path, String content) {
        this.path = path;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileContent that = (FileContent) o;
        return Objects.equals(path, that.path) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, content);
    }
}
