package com.netcracker.edu.project.user.project;

import com.netcracker.edu.project.user.Permission;
import com.netcracker.edu.project.user.exception.FileException;
import com.netcracker.edu.project.user.tree.Tree;

import java.util.HashMap;
import java.util.Map;

public class Project {

    public enum Visibility {
        PRIVATE,
        PUBLIC
    }

    private final String projectName;
    private String ownerUsername;
    private final Tree<File> tree;
    private Visibility visibility;
    private final Map<String, Participation> participants;

    public Project(String projectName) {
        this.projectName = projectName;
        this.tree = new Tree<>(new Directory(projectName, "/" + projectName));
        this.participants = new HashMap<>();
    }

    public String getProjectName() {
        return projectName;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getTree() {
        return tree.toString();
    }

    public void addFile(String fileName, String path, String content) throws FileException {
        tree.add(new File(fileName, path, content));
    }

    public void addDirectory(String name, String path) throws FileException {
        tree.add(new Directory(name, path));
    }

    public String getFileContent(String path) throws FileException {
        File file = tree.find(path);
        return file.getContent();
    }

    public synchronized void update(String path, String content) throws FileException {
         File file = tree.find(path);
         file.setContent(content);
    }

    public void deleteFile(String path) {
        tree.delete(path);
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public Map<String, Participation> getParticipants() {
        return participants;
    }

    public void addParticipant(String username, Permission permission) {
        Participation participant = new Participation(permission);
        participant.activate();

        participants.put(username, participant);
    }

    public void addRequestParticipation(String username, Permission permission) {
        participants.put(username, new Participation(permission));
    }

    public Participation getParticipant(String username) {
        return participants.get(username);
    }

    public Permission getUserPermission(String username) {
        return participants.get(username).getPermission();
    }

    public void deleteParticipant(String username) {
        participants.remove(username);
    }

    public boolean containsParticipant(String username) {
        return participants.containsKey(username);
    }

    public boolean isOwner(String username) {
        return ownerUsername.equals(username);
    }

    public boolean isWriteAllowed(String username) {
        return !getUserPermission(username).equals(Permission.READ_ONLY)
                && isUserActivated(username);
    }

    public boolean isUserActivated(String username) {
        return participants.get(username).isActivated();
    }

    public static class Builder {
        private final Project project;

        public Builder(String projectName, String ownerUsername, Visibility visibility) {
            this.project = new Project(projectName);
            project.ownerUsername = ownerUsername;
            project.visibility = visibility;
        }

        public Builder addParticipant(String username, Permission permission) {
            Participation participant = new Participation(permission);
            participant.activate();

            project.participants.put(username, participant);

            return this;
        }

        public Project build() {
            return project;
        }
    }
}
