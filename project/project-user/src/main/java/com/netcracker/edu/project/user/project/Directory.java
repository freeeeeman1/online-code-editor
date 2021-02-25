package com.netcracker.edu.project.user.project;

public class Directory extends File {

    public Directory(String name, String path) {
        super(name, path, null);

        this.isDirectory = true;
    }
}
