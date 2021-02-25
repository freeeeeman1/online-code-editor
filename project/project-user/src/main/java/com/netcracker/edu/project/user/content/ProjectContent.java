package com.netcracker.edu.project.user.content;

import com.netcracker.edu.project.user.project.Participation;
import com.netcracker.edu.project.user.project.Project;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProjectContent {

    private final String projectName;
    private String projectTree;
    private String ownerUsername;
    private Project.Visibility visibility;
    private Map<String, Participation> participants;

    public ProjectContent(String projectName) {
        this.projectName = projectName;
        this.participants = new HashMap<>();
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectTree() {
        return projectTree;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public Project.Visibility getVisibility() {
        return visibility;
    }

    public void addParticipant(String username, Participation participation) {
        participants.put(username, participation);
    }

    public Map<String, Participation> getParticipants() {
        return participants;
    }

    public static class Builder {

        private final ProjectContent projectContent;

        public Builder(String projectName, String ownerUsername, Project.Visibility visibility) {
            this.projectContent = new ProjectContent(projectName);
            projectContent.ownerUsername = ownerUsername;
            projectContent.visibility = visibility;
        }

        public Builder withProjectTree(String projectTree) {
            projectContent.projectTree = projectTree;
            return this;
        }

        public Builder withParticipants(Map<String, Participation> participants) {
            projectContent.participants = participants;
            return this;
        }

        public ProjectContent build() {
            return projectContent;
        }

    }

    @Override
    public String toString() {
        return "{" +
                " \"projectName\":" + "\"" + projectName + "\"," +
                " \"structureProject\":"  + projectTree + "," +
                " \"ownerUsername\":" + "\"" + ownerUsername + "\"," +
                " \"space\":" + "\"" + visibility + "\"," +
                " \"participants\":" + participants +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectContent that = (ProjectContent) o;
        return Objects.equals(projectName, that.projectName) &&
                Objects.equals(projectTree, that.projectTree) &&
                Objects.equals(ownerUsername, that.ownerUsername) &&
                visibility == that.visibility &&
                Objects.equals(participants, that.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectName, projectTree, ownerUsername, visibility, participants);
    }
}
