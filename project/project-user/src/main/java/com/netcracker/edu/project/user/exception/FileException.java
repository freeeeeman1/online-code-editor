package com.netcracker.edu.project.user.exception;

import com.netcracker.edu.project.user.project.File;

public class FileException extends Exception {

    private final File file;
    private final String message;

    public File getFile() {
        return file;
    }

    public String getMessage() {
        return file.getName() + " " + message;
    }

    public FileException(Object file, String message) {
        super(file + "" + message);
        this.file = (File) file;
        this.message = message;
    }
}
