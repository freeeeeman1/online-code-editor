package com.netcracker.edu.project.repository;

import com.netcracker.edu.project.user.project.Project;

import java.util.List;

public interface ProjectRepository {

    void addProject(Project project);

    Project getProject(String projectName);

    boolean contains(String projectName);

    List<String> getProjectNames(String username);

    void deleteProject(String projectName);
}

