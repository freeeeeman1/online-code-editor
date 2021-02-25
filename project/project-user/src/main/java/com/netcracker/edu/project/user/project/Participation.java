package com.netcracker.edu.project.user.project;

import com.netcracker.edu.project.user.Permission;

public class Participation {

    private boolean activated = false;
    private final Permission permission;

    public Participation(Permission permission) {
        this.permission = permission;
    }

    public void activate() {
        activated = true;
    }

    public Permission getPermission() {
        return permission;
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public String toString() {
        return "{" +
                " \"active\":" + "\"" + this.activated + "\"," +
                " \"permission\":" + "\"" + this.permission + "\"";
    }
}
