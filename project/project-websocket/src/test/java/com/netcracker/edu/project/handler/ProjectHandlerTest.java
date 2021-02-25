package com.netcracker.edu.project.handler;

import com.google.gson.Gson;
import com.netcracker.edu.logic.ProjectService;
import com.netcracker.edu.logic.exception.FlusherException;
import com.netcracker.edu.logic.exception.PermissionException;
import com.netcracker.edu.logic.exception.ProjectException;
import com.netcracker.edu.project.client.WebSocketClient;
import com.netcracker.edu.project.server.Server;
import com.netcracker.edu.project.user.Permission;
import com.netcracker.edu.project.user.content.FileContent;
import com.netcracker.edu.project.user.content.ProjectContent;
import com.netcracker.edu.project.user.content.UserContent;
import com.netcracker.edu.project.user.exception.FileException;
import com.netcracker.edu.project.user.project.File;
import com.netcracker.edu.project.user.project.Participation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.netcracker.edu.project.client.Requests.ADD_DIRECTORY;
import static com.netcracker.edu.project.client.Requests.ADD_FILE;
import static com.netcracker.edu.project.client.Requests.ADD_PARTICIPANT;
import static com.netcracker.edu.project.client.Requests.CREATE_PROJECT;
import static com.netcracker.edu.project.client.Requests.DELETE_FILE;
import static com.netcracker.edu.project.client.Requests.DELETE_PARTICIPANT;
import static com.netcracker.edu.project.client.Requests.DELETE_PROJECT;
import static com.netcracker.edu.project.client.Requests.GET_PARTICIPATION_REQUEST;
import static com.netcracker.edu.project.client.Requests.GET_PROJECT_CONTENT;
import static com.netcracker.edu.project.client.Requests.GET_USER_CONTENT;
import static com.netcracker.edu.project.client.Requests.READ_FILE;
import static com.netcracker.edu.project.client.Requests.REQUEST_PARTICIPATION;
import static com.netcracker.edu.project.client.Requests.STATUS_SUCCESSFUL;
import static com.netcracker.edu.project.client.Requests.STATUS_UNKNOWN_OPERATION;
import static com.netcracker.edu.project.client.Requests.UNKNOWN_OPERATION;
import static com.netcracker.edu.project.client.Requests.UPDATE_FILE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class ProjectHandlerTest {

    private static final String USERNAME = "username";
    private static final String MESSAGE = "message";
    private static final String PROJECT = "project";
    private final long waitTime = 100L;
    private Thread serverThread;
    private WebSocketClient webSocketClient;
    private ProjectService projectService;

    @Before
    public void setUp() {
        this.projectService = Mockito.mock(ProjectService.class);

        this.serverThread = new Thread(() -> {
            Server server = new Server(projectService);
            server.start();

        });

        serverThread.start();
        this.webSocketClient = new WebSocketClient();
    }

    @Test
    public void shouldReturnUserContentOnGetUserContent() throws InterruptedException {
        List<String> projectsName = new ArrayList<>();
        projectsName.add("111");
        projectsName.add("222");
        projectsName.add("333");
        UserContent content = new UserContent("Vadim", projectsName);
        String expected = new Gson().toJson(content);
        when(projectService.getUserContent("Vadim"))
                .thenReturn(content);

        webSocketClient.sendRequest(GET_USER_CONTENT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnProjectContentOnGetProjectContent() throws ProjectException, InterruptedException {
        ProjectContent content = new ProjectContent("NetCracker");
        String expected = new Gson().toJson(content);
        when(projectService.getProjectContent("NetCracker"))
                .thenReturn(content);

        webSocketClient.sendRequest(GET_PROJECT_CONTENT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnGetProjectContent() throws ProjectException, InterruptedException {
        when(projectService.getProjectContent(Mockito.any()))
                .thenThrow(new ProjectException(PROJECT, MESSAGE));

        webSocketClient.sendRequest(GET_PROJECT_CONTENT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnSuccessfulOnCreateProject() throws InterruptedException {
        webSocketClient.sendRequest(CREATE_PROJECT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(STATUS_SUCCESSFUL, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnCreateContent() throws ProjectException, InterruptedException {
        doThrow(new ProjectException(PROJECT, MESSAGE)).when(projectService).createProject(Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(CREATE_PROJECT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnSuccessfulOnDeleteProject() throws InterruptedException {
        webSocketClient.sendRequest(DELETE_PROJECT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(STATUS_SUCCESSFUL, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnDeleteProject() throws ProjectException, PermissionException, InterruptedException {
        doThrow(new ProjectException(PROJECT, MESSAGE)).when(projectService).deleteProject(Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(DELETE_PROJECT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnPermissionExceptionOnDeleteProject() throws ProjectException, PermissionException, InterruptedException {
        doThrow(new PermissionException(USERNAME, Permission.CREATOR, MESSAGE)).when(projectService).deleteProject(Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(DELETE_PROJECT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new PermissionException(USERNAME, Permission.CREATOR, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnSuccessfulOnAddDirectory() throws InterruptedException {
        webSocketClient.sendRequest(ADD_DIRECTORY);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(STATUS_SUCCESSFUL, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnAddDirectory() throws PermissionException, ProjectException, FileException, InterruptedException {
        doThrow(new ProjectException(PROJECT, MESSAGE)).when(projectService).addDirectory(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(ADD_DIRECTORY);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnPermissionExceptionOnAddDirectory() throws PermissionException, ProjectException, FileException, InterruptedException {
        doThrow(new PermissionException(USERNAME, Permission.READ_ONLY, MESSAGE)).when(projectService).addDirectory(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(ADD_DIRECTORY);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new PermissionException(USERNAME, Permission.READ_ONLY, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnFileExceptionOnAddDirectory() throws PermissionException, ProjectException, FileException, InterruptedException {
        doThrow(new FileException(new File("name", "a/a", ""), MESSAGE)).when(projectService).addDirectory(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(ADD_DIRECTORY);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new FileException(new File("name", "a/a", ""), MESSAGE)), actual);
    }

    @Test
    public void shouldReturnSuccessfulOnAddFile() throws InterruptedException {
        webSocketClient.sendRequest(ADD_FILE);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(STATUS_SUCCESSFUL, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnAddFile() throws PermissionException, ProjectException, FileException, InterruptedException {
        doThrow(new ProjectException(PROJECT, MESSAGE)).when(projectService).addFile(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(ADD_FILE);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnPermissionExceptionOnAddFile() throws PermissionException, ProjectException, FileException, InterruptedException {
        doThrow(new PermissionException(USERNAME, Permission.READ_ONLY, MESSAGE)).when(projectService).addFile(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(ADD_FILE);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new PermissionException(USERNAME, Permission.READ_ONLY, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnFileExceptionOnAddFile() throws PermissionException, ProjectException, FileException, InterruptedException {
        doThrow(new FileException(new File("name", "a/a", ""), MESSAGE)).when(projectService).addFile(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(ADD_FILE);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new FileException(new File("name", "a/a", ""), MESSAGE)), actual);
    }

    @Test
    public void shouldReturnFileContentOnReadFile() throws InterruptedException, ProjectException, FileException {
        FileContent content = new FileContent("path", "Hello, my name is Vadim");
        String expected = new Gson().toJson(content);
        when(projectService.readFile("NetCracker", "path", "Vadim"))
                .thenReturn(content);

        webSocketClient.sendRequest(READ_FILE);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnReadFile() throws ProjectException, InterruptedException, FileException {
        doThrow(new ProjectException(PROJECT, MESSAGE)).when(projectService).readFile(Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(READ_FILE);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnSuccessfulOnUpdateFile() throws InterruptedException {
        webSocketClient.sendRequest(UPDATE_FILE);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(STATUS_SUCCESSFUL, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnUpdateFile() throws InterruptedException, ProjectException, FileException, FlusherException {
        doThrow(new ProjectException(PROJECT, MESSAGE)).when(projectService).updateFile(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(UPDATE_FILE);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnSuccessfulOnDeleteFile() throws InterruptedException {
        webSocketClient.sendRequest(DELETE_FILE);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(STATUS_SUCCESSFUL, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnDeleteFile() throws ProjectException, InterruptedException {
        doThrow(new ProjectException(PROJECT, MESSAGE)).when(projectService).deleteFile(Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(DELETE_FILE);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnSuccessfulOnAddParticipant() throws InterruptedException {
        webSocketClient.sendRequest(ADD_PARTICIPANT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(STATUS_SUCCESSFUL, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnAddParticipant() throws ProjectException, PermissionException, InterruptedException {
        doThrow(new ProjectException(PROJECT, MESSAGE)).when(projectService).addParticipant(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(ADD_PARTICIPANT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnPermissionExceptionOnAddParticipant() throws ProjectException, PermissionException, InterruptedException {
        doThrow(new PermissionException(USERNAME, Permission.READ_WRITE, MESSAGE)).when(projectService).addParticipant(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(ADD_PARTICIPANT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new PermissionException(USERNAME, Permission.READ_WRITE, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnSuccessfulOnDeleteParticipant() throws InterruptedException {
        webSocketClient.sendRequest(DELETE_PARTICIPANT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(STATUS_SUCCESSFUL, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnDeleteParticipant() throws ProjectException, PermissionException, InterruptedException {
        doThrow(new ProjectException(PROJECT, MESSAGE)).when(projectService).deleteParticipant(Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(DELETE_PARTICIPANT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnPermissionExceptionOnDeleteParticipant() throws ProjectException, PermissionException, InterruptedException {
        doThrow(new PermissionException(USERNAME, Permission.CREATOR, MESSAGE)).when(projectService).deleteParticipant(Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(DELETE_PARTICIPANT);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new PermissionException(USERNAME, Permission.CREATOR, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnSuccessfulOnRequestParticipation() throws InterruptedException {
        webSocketClient.sendRequest(REQUEST_PARTICIPATION);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(STATUS_SUCCESSFUL, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnRequestParticipation() throws ProjectException, InterruptedException {
        doThrow(new ProjectException(PROJECT, MESSAGE)).when(projectService).requestParticipation(Mockito.any(), Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(REQUEST_PARTICIPATION);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnSuccessfulOnGetParticipationRequest() throws InterruptedException, ProjectException, PermissionException {
        Map<String, Participation> usersParticipations = new HashMap<>();
        usersParticipations.put("Ayaz", new Participation(Permission.valueOf("CREATOR")));
        usersParticipations.put("Max", new Participation(Permission.valueOf("READ_WRITE")));
        usersParticipations.put("Vadim", new Participation(Permission.valueOf("READ_ONLY")));
        String expected = new Gson().toJson(usersParticipations);
        when(projectService.getParticipationRequest("Ayaz", "NetCracker"))
                .thenReturn(usersParticipations);

        webSocketClient.sendRequest(GET_PARTICIPATION_REQUEST);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnProjectExceptionOnGetParticipationRequest() throws ProjectException, PermissionException, InterruptedException {
        doThrow(new ProjectException(PROJECT, MESSAGE)).when(projectService).getParticipationRequest(Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(GET_PARTICIPATION_REQUEST);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new ProjectException(PROJECT, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnPermissionExceptionOnGetParticipationRequest() throws ProjectException, PermissionException, InterruptedException {
        doThrow(new PermissionException(PROJECT, Permission.READ_WRITE, MESSAGE)).when(projectService).getParticipationRequest(Mockito.any(), Mockito.any());

        webSocketClient.sendRequest(GET_PARTICIPATION_REQUEST);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(toJSON(new PermissionException(PROJECT, Permission.READ_WRITE, MESSAGE)), actual);
    }

    @Test
    public void shouldReturnUnknownOperation() throws InterruptedException {
        webSocketClient.sendRequest(UNKNOWN_OPERATION);

        String actual = webSocketClient.getResponses().poll(waitTime, TimeUnit.MILLISECONDS);
        assertEquals(STATUS_UNKNOWN_OPERATION, actual);
    }

    @After
    public void tearDown() {
        serverThread.interrupt();
    }

    private String toJSON(Exception e) {
        return "{" +
                '"' + "status" + '"' + ':' + '"' + e.getMessage() + '"' +
                '}';
    }
}
