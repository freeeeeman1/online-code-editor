package com.netcracker.edu.logic;

import com.netcracker.edu.logic.event.EventRouter;
import com.netcracker.edu.logic.event.EventRouterImpl;
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
import com.netcracker.edu.project.user.project.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectServiceImplTest {
    private ProjectService projectService;
    private ProjectRepository projectRepository;
    private Project project1;
    private List<String> projectNames;

    @Before
    public void beforeTest() throws FileException {
        this.projectNames = new ArrayList<>();

        this.projectRepository = mock(ProjectRepository.class);
        FileFlusher updateExecutorImpl = mock(FileFlusher.class);
        FileRepository fileRepository = mock(FileRepository.class);
        ParticipantRepository participantRepository = mock(ParticipantRepository.class);
        EventRouter eventRouter = new EventRouterImpl();

        this.projectService = new ProjectServiceImpl(
                projectRepository,
                fileRepository,
                participantRepository,
                updateExecutorImpl,
                eventRouter);

        this.project1 = new Project.Builder("projectName", "max", Project.Visibility.PUBLIC)
                .addParticipant("max", Permission.CREATOR)
                .addParticipant("max00", Permission.READ_ONLY)
                .build();
        project1.addFile("readme.txt", "/projectName/readme.txt", "TEXT");
        project1.addDirectory("b", "/projectName/b");
        project1.addFile("text.txt", "/projectName/b/text.txt", null);

        projectNames.add(project1.getProjectName());
    }

    @Test
    public void Should_ReturnUserContent_ForUser() {
        UserContent expectedUserContent = new UserContent(project1.getOwnerUsername(), projectNames);
        when(projectRepository.getProjectNames(Mockito.any())).thenReturn(projectNames);

        UserContent responseUserContent = projectService.getUserContent("max");

        assertEquals(expectedUserContent, responseUserContent);
    }

    @Test
    public void Should_ReturnProjectContent_When_ProjectExists() throws ProjectException {
        ProjectContent expectedUserContent = new ProjectContent
                .Builder(project1.getProjectName(), project1.getOwnerUsername(), project1.getVisibility())
                .withParticipants(project1.getParticipants())
                .withProjectTree(project1.getTree())
                .build();
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);
        when(projectRepository.contains(Mockito.any())).thenReturn(true);

        ProjectContent responseProject = projectService.getProjectContent("projectName");

        assertEquals(expectedUserContent, responseProject);
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectNotExists() throws ProjectException {
        ProjectContent expectedUserContent = new ProjectContent
                .Builder(project1.getProjectName(), project1.getOwnerUsername(), project1.getVisibility())
                .withParticipants(project1.getParticipants())
                .withProjectTree(project1.getTree())
                .build();
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);
        when(projectRepository.contains(Mockito.any())).thenReturn(false);

        ProjectContent responseProject = projectService.getProjectContent("projectName");

        assertEquals(expectedUserContent, responseProject);
    }

    @Test
    public void Should_CreateProject_When_ProjectNotExists() throws ProjectException {
        when(projectRepository.contains(Mockito.any())).thenReturn(false);

        projectService.createProject("max", "projectName2", Project.Visibility.PUBLIC);
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectExists() throws ProjectException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);

        projectService.createProject("max", "projectName", Project.Visibility.PUBLIC);
    }

    @Test
    public void Should_DeleteProject_When_ProjectExists() throws PermissionException, ProjectException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.deleteProject("max", "projectName");
    }

    @Test(expected = PermissionException.class)
    public void Should_ThrowsException_When_UserHasNotPermission() throws PermissionException, ProjectException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.deleteProject("max00", "projectName");
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectNotExists_DeletingProject() throws PermissionException, ProjectException {
        projectService.deleteProject("max", "projectName11");
    }

    @Test
    public void Should_AddFile_When_FileNotExists() throws FileException, ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.addFile("max", "projectName", "text1.txt", "/projectName/text1.txt");
    }

    @Test(expected = FileException.class)
    public void Should_ThrowsException_When_FileExists() throws FileException, ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.addFile("max", "projectName", "text.txt", "/projectName/b/text.txt");
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectNotExists_AddingFile()
            throws FileException, ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(false);

        projectService.addFile("max", "projectName", "text.txt", "/projectName/hello/qwe/text.txt");
    }

    @Test(expected = PermissionException.class)
    public void Should_ThrowsException_When_UserHasNotPermission_AddingFile()
            throws FileException, ProjectException, PermissionException {
        Project project = new Project.Builder("projectName", "max", Project.Visibility.PUBLIC)
                .addParticipant("max", Permission.READ_ONLY)
                .build();

        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project);

        projectService.addFile("max", "projectName", "text.txt", "/projectName/text.txt");
    }

    @Test
    public void Should_AddDirectory_When_ProjectExists() throws FileException, ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.addDirectory("max", "projectName", "hello", "/projectName/hello");
    }

    @Test(expected = FileException.class)
    public void Should_ThrowsException_When_DirectoryExists() throws FileException, ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.addFile("max", "projectName", "b", "/projectName/b");
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectNotExists_AddingDirectory()
            throws FileException, ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(false);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.addDirectory("max", "projectName", "text.txt", "/projectName/hello/qwe/text.txt");
    }

    @Test(expected = PermissionException.class)
    public void Should_ThrowsException_When_UserHasNotPermission_AddingDirectory()
            throws FileException, ProjectException, PermissionException {
        Project project = new Project.Builder("projectName", "max", Project.Visibility.PUBLIC)
                .addParticipant("max", Permission.READ_ONLY)
                .build();
        project.addFile("readme.txt", "/projectName/readme.txt", null);
        project.addDirectory("b", "/projectName/b");
        project.addFile("text.txt", "/projectName/b/text.txt", null);

        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project);

        projectService.addDirectory("max", "projectName", "hello", "/projectName/hello");
    }

    @Test
    public void Should_ReturnFileContent_When_FileExists() throws ProjectException, FileException {
        String expectedContent = "TEXT";
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        String actualContent = projectService.readFile("projectName", "/projectName/readme.txt", "Max").getContent();

        assertEquals(expectedContent, actualContent);
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectNotExists_ReadingFile() throws ProjectException, FileException {
        when(projectRepository.contains(Mockito.any())).thenReturn(false);

        projectService.readFile("projectName", "/projectName/readme.txt", "Max");
    }

    @Test
    public void Should_ReturnUpdatedContent_When_FileUpdated() throws ProjectException, FileException, FlusherException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);
        project1.addFile("1.txt", "/projectName/b/1.txt", null);
        FileContent expectedContent = new FileContent("/projectName/b/1.txt", "TEXT");

        projectService.readFile("projectName", "/projectName/b/1.txt", "Max");
        projectService.updateFile("projectName", "/projectName/b/1.txt", "TEXT", "username");
        FileContent actualContent = projectService.readFile("projectName", "/projectName/b/1.txt", "Max");

        assertEquals(expectedContent, actualContent);
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectNotExists_UpdatingFile()
            throws ProjectException, FlusherException, FileException {
        when(projectRepository.contains(Mockito.any())).thenReturn(false);

        projectService.updateFile("projectName", "/projectName/b/1.txt", "TEXT", "username");
    }

    @Test
    public void Should_DeleteFile_When_ProjectExists() throws ProjectException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.deleteFile("/projectName/hello/b/hello.txt", "projectName");
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectNotExists_DeletingFile() throws ProjectException {
        when(projectRepository.contains(Mockito.any())).thenReturn(false);

        projectService.deleteFile("/projectName/hello/b/hello.txt", "projectName");
    }

    @Test
    public void Should_AddParticipant_When_ProjectExists() throws ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.addParticipant("projectName", "max", "Kate", Permission.READ_ONLY);
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectNotExists_AddingParticipant()
            throws ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(false);

        projectService.addParticipant("project", "max", "Kate", Permission.READ_ONLY);
    }

    @Test(expected = PermissionException.class)
    public void Should_ThrowsException_When_UserHasNotPermission_AddingParticipant()
            throws ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.addParticipant("projectName", "max00", "Kate", Permission.READ_ONLY);
    }

    @Test
    public void Should_DeleteParticipant_When_ProjectExists() throws ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);
        projectService.addParticipant("projectName", "max", "Kate", Permission.READ_ONLY);

        projectService.deleteParticipant("projectName", "max", "Kate");
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectNotExists_DeletingParticipant()
            throws ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(false);

        projectService.deleteParticipant("projectName", "max", "Kate");
    }

    @Test(expected = PermissionException.class)
    public void Should_ThrowsException_When_UserHasNotPermission_DeletingParticipant()
            throws ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.deleteParticipant("projectName", "max00", "Kate");
    }

    @Test
    public void Should_requestParticipation_When_ProjectExists() throws ProjectException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.requestParticipation("max", "projectName", Permission.READ_ONLY);
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectNotExists_RequestingParticipation() throws ProjectException {
        when(projectRepository.contains(Mockito.any())).thenReturn(false);

        projectService.requestParticipation("max", "project", Permission.READ_ONLY);
    }

    @Test
    public void Should_GetParticipationRequest_When_ProjectExists() throws ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        assertEquals(project1.getParticipants(), projectService.getParticipationRequest("max", "projectName"));
    }

    @Test(expected = PermissionException.class)
    public void Should_ThrowsException_When_UserHasNotPermission_GettingParticipationRequest()
            throws ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(true);
        when(projectRepository.getProject(Mockito.any())).thenReturn(project1);

        projectService.getParticipationRequest("max00", "projectName");
    }

    @Test(expected = ProjectException.class)
    public void Should_ThrowsException_When_ProjectNotExists_GettingParticipationRequest()
            throws ProjectException, PermissionException {
        when(projectRepository.contains(Mockito.any())).thenReturn(false);

        projectService.getParticipationRequest("max", "project");
    }
}
