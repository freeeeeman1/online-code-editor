package com.netcracker.edu.logic.exception;

import com.netcracker.edu.project.user.Permission;

public class PermissionException extends Exception {

    private final String username;
    private final Permission permission;
    private final String message;

    public String getUsername() {
        return username;
    }

    public Permission getPermission() {
        return permission;
    }

    public String getMessage() {
        return message;
    }

    public PermissionException(String username, Permission permission, String message) {
        super(username + message + permission);
        this.username = username;
        this.permission = permission;
        this.message = message;
    }
}
