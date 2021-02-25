package com.netcracker.edu.logic;

import com.netcracker.edu.logic.exception.FlusherException;
import com.netcracker.edu.logic.exception.PermissionException;
import com.netcracker.edu.logic.exception.ProjectException;
import com.netcracker.edu.project.user.Permission;
import com.netcracker.edu.project.user.content.FileContent;
import com.netcracker.edu.project.user.content.ProjectContent;
import com.netcracker.edu.project.user.content.UserContent;
import com.netcracker.edu.project.user.exception.FileException;
import com.netcracker.edu.project.user.project.Participation;
import com.netcracker.edu.project.user.project.Project;

import java.util.Map;

public interface ProjectService {

    /**
     * @param username to return all content
     * @return UserContent
     */
    UserContent getUserContent(String username);

    /**
     * @param projectName to return project
     * @return ProjectContent
     * @throws ProjectException when project does not exist
     */
    ProjectContent getProjectContent(String projectName) throws ProjectException;

    /**
     * @param username    to create project
     * @param projectName to create project
     * @param space       to create project
     * @throws ProjectException when project already exists
     */
    void createProject(String username, String projectName, Project.Visibility space)
            throws ProjectException;

    /**
     * @param username    to delete project
     * @param projectName to return project
     * @throws PermissionException when user permission is not CREATOR
     * @throws ProjectException    when project does not exist
     */
    void deleteProject(String username, String projectName)
            throws PermissionException, ProjectException;

    /**
     * @param username    to add file to project
     * @param projectName to add file to project
     * @param fileName    to add file to project
     * @param filePath        to add file to project
     * @throws ProjectException    when project does not exist
     * @throws FileException       when project already exists
     * @throws PermissionException when user permission is not CREATOR
     */
    void addFile(String username, String projectName, String fileName, String filePath)
            throws ProjectException, FileException, PermissionException;

    /**
     * @param username    to add Director to project
     * @param projectName to add Director to project
     * @param fileName    to add Director to project
     * @param filePath        to add Director to project
     * @throws ProjectException    when project does not exist
     * @throws FileException       when project already exists
     * @throws PermissionException when user permission is not CREATOR
     */
    void addDirectory(String username, String projectName, String fileName, String filePath)
            throws ProjectException, FileException, PermissionException;

    FileContent readFile(String projectName, String filePath, String username) throws ProjectException, FileException;

    void updateFile(String projectName, String filePath, String content, String username)
            throws ProjectException, FlusherException, FileException;

    /**
     * @param filePath        to delete file in project
     * @param projectName to delete file in project
     * @throws ProjectException when project does not exist
     */
    void deleteFile(String filePath, String projectName) throws ProjectException;

    /**
     * @param projectName to add participant to project
     * @param permission  to add participant to project
     * @throws ProjectException when project does not exist
     */
    void addParticipant(String projectName, String creatorUsername, String username, Permission permission)
    throws ProjectException, PermissionException;

    void deleteParticipant(String projectName, String creatorUsername, String username)
            throws ProjectException, PermissionException;

    /**
     * @param username    to request to become a participant
     * @param projectName to request to become a participant
     * @param permission  to request to become a participant
     * @throws ProjectException when project does not exist
     */
    void requestParticipation(String username, String projectName, Permission permission) throws ProjectException;

    /**
     * @param username    to get participation request
     * @param projectName to get participation request
     * @return Map<String, Participation>
     * @throws PermissionException when user permission is not CREATOR
     * @throws ProjectException    when project does not exist
     */
    Map<String, Participation> getParticipationRequest(String username, String projectName)
            throws ProjectException, PermissionException;
}
