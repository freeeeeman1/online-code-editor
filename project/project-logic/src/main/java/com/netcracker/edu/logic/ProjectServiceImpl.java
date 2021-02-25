package com.netcracker.edu.logic;

import com.netcracker.edu.logic.event.Event;
import com.netcracker.edu.logic.event.EventRouter;
import com.netcracker.edu.logic.exception.FlusherException;
import com.netcracker.edu.logic.exception.PermissionException;
import com.netcracker.edu.logic.exception.ProjectException;
import com.netcracker.edu.logic.executor.FileFlusher;
import com.netcracker.edu.project.repository.FileRepository;
import com.netcracker.edu.project.repository.ParticipantRepository;
import com.netcracker.edu.project.repository.ProjectRepository;
import com.netcracker.edu.project.user.Permission;
import com.netcracker.edu.project.user.content.FileContent;
import com.netcracker.edu.project.user.content.ProjectContent;
import com.netcracker.edu.project.user.content.UserContent;
import com.netcracker.edu.project.user.exception.FileException;
import com.netcracker.edu.project.user.project.Directory;
import com.netcracker.edu.project.user.project.File;
import com.netcracker.edu.project.user.project.Participation;
import com.netcracker.edu.project.user.project.Project;

import java.util.List;
import java.util.Map;

public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final FileRepository fileRepository;
    private final ParticipantRepository participantRepository;
    private final FileFlusher fileFlusher;
    private final EventRouter eventRouter;

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            FileRepository fileRepository,
            ParticipantRepository participantRepository,
            FileFlusher fileFlusher,
            EventRouter eventRouter) {

        this.projectRepository = projectRepository;
        this.fileRepository = fileRepository;
        this.participantRepository = participantRepository;
        this.fileFlusher = fileFlusher;
        this.eventRouter = eventRouter;

        fileFlusher.start();
    }

    @Override
    public UserContent getUserContent(String username) {
        List<String> projectNames = projectRepository.getProjectNames(username);

        return new UserContent(username, projectNames);
    }

    @Override
    public ProjectContent getProjectContent(String projectName) throws ProjectException {
        if (projectRepository.contains(projectName)) {
            Project project = projectRepository.getProject(projectName);

            return new ProjectContent
                    .Builder(project.getProjectName(), project.getOwnerUsername(), project.getVisibility())
                    .withParticipants(project.getParticipants())
                    .withProjectTree(project.getTree())
                    .build();
        } else {
            throw new ProjectException(projectName, "does not exist");
        }
    }

    @Override
    public void createProject(String username, String projectName, Project.Visibility visibility)
            throws ProjectException {
        if (!projectRepository.contains(projectName)) {
            projectRepository.addProject(new Project
                    .Builder(projectName, username, visibility)
                    .addParticipant(username, Permission.CREATOR)
                    .build());
        } else {
            throw new ProjectException(projectName, "already exist");
        }
    }

    @Override
    public void deleteProject(String username, String projectName) throws PermissionException, ProjectException {
        if (projectRepository.contains(projectName)) {
            Project project = projectRepository.getProject(projectName);

            if (project.isOwner(username)) {
                projectRepository.deleteProject(projectName);
            } else {
                throw new PermissionException(username, project.getUserPermission(username), " has wrong permission");
            }
        } else {
            throw new ProjectException(projectName, "does not exist");
        }
    }

    @Override
    public void addFile(String username, String projectName, String fileName, String filePath)
            throws ProjectException, FileException, PermissionException {
        if (projectRepository.contains(projectName)) {
            Project project = projectRepository.getProject(projectName);

            if (project.isWriteAllowed(username)) {
                project.addFile(fileName, filePath, null);

                fileRepository.addFile(new File(fileName, filePath, null), projectName);
            } else {
                throw new PermissionException(username, project.getUserPermission(username), " has wrong permission");
            }
        } else {
            throw new ProjectException(projectName, "does not exist");
        }
    }

    @Override
    public void addDirectory(String username, String projectName, String fileName, String filePath)
            throws ProjectException, FileException, PermissionException {
        if (projectRepository.contains(projectName)) {
            Project project = projectRepository.getProject(projectName);

            if (project.isWriteAllowed(username)) {
                project.addDirectory(fileName, filePath);

                fileRepository.addFile(new Directory(fileName, filePath), projectName);
            } else {
                throw new PermissionException(username, project.getUserPermission(username), " has wrong permission");
            }
        } else {
            throw new ProjectException(projectName, "does not exist");
        }
    }

    @Override
    public FileContent readFile(String projectName, String filePath, String username) throws ProjectException, FileException {
        if (projectRepository.contains(projectName)) {
            Project project = projectRepository.getProject(projectName);
            String fileContent = project.getFileContent(filePath);

            eventRouter.subscribe(username, filePath);

            return new FileContent(filePath, fileContent);
        } else {
            throw new ProjectException(projectName, "does not exist");
        }
    }

    @Override
    public void updateFile(String projectName, String filePath, String content, String username)
            throws ProjectException, FlusherException, FileException {
        if (projectRepository.contains(projectName)) {
            Project project = projectRepository.getProject(projectName);
            project.update(filePath, content);

            eventRouter.push(new Event(content, filePath));

            fileFlusher.addEvent(new Event(content, filePath));
        } else {
            throw new ProjectException(projectName, "does not exist");
        }
    }

    @Override
    public void deleteFile(String filePath, String projectName) throws ProjectException {
        if (projectRepository.contains(projectName)) {
            Project project = projectRepository.getProject(projectName);
            project.deleteFile(filePath);

            fileRepository.deleteFile(filePath);
        } else {
            throw new ProjectException(projectName, "does not exist");
        }
    }

    @Override
    public void addParticipant(String projectName, String creatorUsername, String username, Permission permission)
            throws ProjectException, PermissionException {
        if (projectRepository.contains(projectName)) {
            Participation participant = new Participation(permission);
            participant.activate();

            Project project = projectRepository.getProject(projectName);

            if (project.isOwner(creatorUsername)) {
                if (project.containsParticipant(username)) {
                    project.deleteParticipant(username);

                    participantRepository.updateParticipant(projectName, username, participant);
                } else {
                    participantRepository.addParticipant(projectName, username, participant);
                }

                project.addParticipant(username, permission);

            } else {
                throw new PermissionException(creatorUsername, null, "has wrong permission");
            }
        } else {
            throw new ProjectException(projectName, "does not exist");
        }
    }

    @Override
    public void deleteParticipant(String projectName, String creatorUsername, String username)
            throws ProjectException, PermissionException {
        if (projectRepository.contains(projectName)) {
            Project project = projectRepository.getProject(projectName);

            if (project.isOwner(creatorUsername)) {
                project.deleteParticipant(username);

                participantRepository.deleteParticipant(projectName, username);
            } else {
                throw new PermissionException(creatorUsername, null, "has wrong permission");
            }
        } else {
            throw new ProjectException(projectName, "does not exist");
        }
    }

    @Override
    public void requestParticipation(String username, String projectName, Permission permission) throws
            ProjectException {
        if (projectRepository.contains(projectName)) {
            Project project = projectRepository.getProject(projectName);
            project.addRequestParticipation(username, permission);

            participantRepository.addParticipant(projectName, username, new Participation(permission));
        } else {
            throw new ProjectException(projectName, "does not exist");
        }
    }

    @Override
    public Map<String, Participation> getParticipationRequest(String username, String projectName) throws
            PermissionException, ProjectException {
        if (projectRepository.contains(projectName)) {
            Project project = projectRepository.getProject(projectName);

            if (project.isOwner(username)) {
                return project.getParticipants();
            } else {
                throw new PermissionException(username, project.getUserPermission(username), " has wrong permission");
            }
        } else {
            throw new ProjectException(projectName, "does not exist");
        }
    }
}
