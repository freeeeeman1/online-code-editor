package com.netcracker.edu.logic.exception;

public class ProjectDoesNotExistException extends Exception{

    public ProjectDoesNotExistException(String projectName) {
        super("Project: " + projectName + "does not exist");
    }
}
