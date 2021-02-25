package com.netcracker.edu.logic.exception;

public class FlusherException extends Exception {
    private final String filePath;
    private final String message;

    public String getUsername() {
        return filePath;
    }

    public String getMessage() {
        return message;
    }

    public FlusherException(String filePath, String message) {
        super(filePath + " " + message);
        this.filePath = filePath;
        this.message = message;
    }
}
