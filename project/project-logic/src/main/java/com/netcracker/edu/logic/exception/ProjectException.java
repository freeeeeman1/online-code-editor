package com.netcracker.edu.logic.exception;

public class ProjectException extends Exception {

    private final String projectName;
    private final String message;

    public String getUsername() {
        return projectName;
    }

    public String getMessage() {
        return message;
    }

    public ProjectException(String projectName, String message) {
        super(projectName + " " + message);
        this.projectName = projectName;
        this.message = message;
    }
}
