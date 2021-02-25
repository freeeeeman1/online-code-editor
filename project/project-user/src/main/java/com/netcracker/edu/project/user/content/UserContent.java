package com.netcracker.edu.project.user.content;

import java.util.List;
import java.util.Objects;

public class UserContent {

    private final List<String> projectNames;
    private final String username;

    public UserContent(String username, List<String> projectNames) {
        this.projectNames = projectNames;
        this.username = username;
    }

    public List<String> getProjectNames() {
        return projectNames;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "{" +
                " \"projectNames\":" + "\"" + projectNames + "\"," +
                " \"username\":" + "\"" + username + "\"" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserContent that = (UserContent) o;
        return Objects.equals(projectNames, that.projectNames) &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectNames, username);
    }
}
