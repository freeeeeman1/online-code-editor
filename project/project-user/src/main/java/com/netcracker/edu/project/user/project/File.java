package com.netcracker.edu.project.user.project;

import com.netcracker.edu.project.user.tree.Element;

import java.util.Objects;

public class File implements Element {

    private String content;
    private final String path;
    private final String name;
    protected boolean isDirectory;

    public File(String name, String path, String content) {
        this.name = name;
        this.path = path;
        this.content = content;
        this.isDirectory = false;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{" +
                " \"content\":" + "\"" + content + "\"," +
                " \"path\":" + "\"" + path + "\"," +
                " \"name\":" + "\"" + name + "\"," +
                " \"isDirectory\":" + "\"" + isDirectory + "\"" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return isDirectory == file.isDirectory &&
                Objects.equals(content, file.content) &&
                path.equals(file.path) &&
                name.equals(file.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, path, name, isDirectory);
    }
}
