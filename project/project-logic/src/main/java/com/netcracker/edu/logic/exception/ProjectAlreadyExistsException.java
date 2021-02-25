package com.netcracker.edu.logic.exception;

public class ProjectAlreadyExistsException extends Exception {

    public ProjectAlreadyExistsException(String projectName) {
        super("project: " + projectName + "already exists");
    }
}
