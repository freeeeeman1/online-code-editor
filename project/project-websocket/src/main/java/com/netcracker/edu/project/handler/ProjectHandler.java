package com.netcracker.edu.project.handler;

import com.google.gson.Gson;
import com.netcracker.edu.logic.ProjectService;
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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.log4j.Logger;

import java.util.Map;

public class ProjectHandler extends ChannelInboundHandlerAdapter {
    private static final String USERNAME = "username";
    private static final String PROJECT_NAME = "project_name";
    private static final String PROJECT_VISIBILITY = "visibility";
    private static final String FILE_NAME = "file_name";
    private static final String PATH = "path";
    private static final String CONTENT = "content";
    private static final String CREATOR = "creator";
    private static final String PERMISSION = "permission";
    private static final String STATUS = "status";
    private static final String SUCCESSFUL = "successful";
    private static final String UNKNOWN_OPERATION = "unknown_operation";

    private static final String OPERATION = "operation";
    private static final String GET_USER_CONTENT = "get_user_content";
    private static final String GET_PROJECT_CONTENT = "get_project_content";
    private static final String CREATE_PROJECT = "create_project";
    private static final String DELETE_PROJECT = "delete_project";
    private static final String ADD_DIRECTORY = "add_directory";
    private static final String ADD_FILE = "add_file";
    private static final String READ_FILE = "read_file";
    private static final String UPDATE_FILE = "update_file";
    private static final String DELETE_FILE = "delete_file";
    private static final String ADD_PARTICIPANT = "add_participant";
    private static final String DELETE_PARTICIPANT = "delete_participant";
    private static final String REQUEST_PARTICIPATION = "request_participation";
    private static final String GET_PARTICIPATION_REQUEST = "get_participation_request";
    private final Logger logger = Logger.getLogger(ProjectHandler.class);
    private final ProjectService projectService;

    public ProjectHandler(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("connected to project handler");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        if (!(message instanceof CloseWebSocketFrame)) {
            Map<String, String> request = parseRequest(message);
            String operationResult = executeOperation(request);

            ctx.channel().writeAndFlush(new TextWebSocketFrame(operationResult));
        } else {
            ctx.close();
        }
    }

    private Map<String, String> parseRequest(Object message) {
        ByteBuf buf = ((WebSocketFrame) message).content();
        byte[] bytes = new byte[buf.readableBytes()];

        buf.duplicate().readBytes(bytes);

        return new Gson().fromJson(new String(bytes), Map.class);
    }

    private String executeOperation(Map<String, String> request) {
        String operation = request.get(OPERATION);

        switch (operation) {
            case GET_USER_CONTENT:
                return getUserContent(request);
            case GET_PROJECT_CONTENT:
                return getProjectContent(request);
            case CREATE_PROJECT:
                return createProject(request);
            case DELETE_PROJECT:
                return deleteProject(request);
            case ADD_DIRECTORY:
                return addDirectory(request);
            case ADD_FILE:
                return addFile(request);
            case READ_FILE:
                return readFile(request);
            case UPDATE_FILE:
                return updateFile(request);
            case DELETE_FILE:
                return deleteFile(request);
            case ADD_PARTICIPANT:
                return addParticipant(request);
            case DELETE_PARTICIPANT:
                return deleteParticipant(request);
            case REQUEST_PARTICIPATION:
                return requestParticipation(request);
            case GET_PARTICIPATION_REQUEST:
                return getParticipationRequest(request);
            default:
                return createUnknownOperationJSON();
        }
    }

    private String getUserContent(Map<String, String> request) {
        String username = request.get(USERNAME);

        UserContent content = projectService.getUserContent(username);

        return createRequestJSON(content);
    }

    private String getProjectContent(Map<String, String> request) {
        String projectName = request.get(PROJECT_NAME);
        ProjectContent content;

        try {
            content = projectService.getProjectContent(projectName);
        } catch (ProjectException e) {
            return createExceptionJSON(e);
        }

        return createRequestJSON(content);
    }

    private String createProject(Map<String, String> request) {
        String username = request.get(USERNAME);
        String projectName = request.get(PROJECT_NAME);
        Project.Visibility visibility = Project.Visibility.valueOf(request.get(PROJECT_VISIBILITY));

        try {
            projectService.createProject(username, projectName, visibility);
        } catch (ProjectException e) {
            return createExceptionJSON(e);
        }

        return createSuccessfulJSON();
    }

    private String deleteProject(Map<String, String> request) {
        String username = request.get(USERNAME);
        String projectName = request.get(PROJECT_NAME);

        try {
            projectService.deleteProject(username, projectName);
        } catch (PermissionException | ProjectException e) {
            return createExceptionJSON(e);
        }

        return createSuccessfulJSON();
    }

    private String addDirectory(Map<String, String> request) {
        String username = request.get(USERNAME);
        String projectName = request.get(PROJECT_NAME);
        String fileName = request.get(FILE_NAME);
        String path = request.get(PATH);

        try {
            projectService.addDirectory(username, projectName, fileName, path);
        } catch (FileException | PermissionException | ProjectException e) {
            return createExceptionJSON(e);
        }

        return createSuccessfulJSON();
    }

    private String addFile(Map<String, String> request) {
        String username = request.get(USERNAME);
        String projectName = request.get(PROJECT_NAME);
        String fileName = request.get(FILE_NAME);
        String path = request.get(PATH);

        try {
            projectService.addFile(username, projectName, fileName, path);
        } catch (FileException | PermissionException | ProjectException e) {
            return createExceptionJSON(e);
        }

        return createSuccessfulJSON();
    }

    private String readFile(Map<String, String> request) {
        String projectName = request.get(PROJECT_NAME);
        String path = request.get(PATH);
        String username = request.get(USERNAME);
        FileContent content;

        try {
            content = projectService.readFile(projectName, path, username);
        } catch (ProjectException | FileException e) {
            return createExceptionJSON(e);
        }

        return createRequestJSON(content);
    }

    private String updateFile(Map<String, String> request) {
        String projectName = request.get(PROJECT_NAME);
        String path = request.get(PATH);
        String content = request.get(CONTENT);
        String username = request.get(USERNAME);

        try {
            projectService.updateFile(projectName, path, content, username);
        } catch (ProjectException | FlusherException | FileException e) {
            return createExceptionJSON(e);
        }

        return createSuccessfulJSON();
    }

    private String deleteFile(Map<String, String> request) {
        String path = request.get(PATH);
        String projectName = request.get(PROJECT_NAME);

        try {
            projectService.deleteFile(path, projectName);
        } catch (ProjectException e) {
            return createExceptionJSON(e);
        }

        return createSuccessfulJSON();
    }

    private String addParticipant(Map<String, String> request) {
        String projectName = request.get(PROJECT_NAME);
        String creator = request.get(CREATOR);
        String username = request.get(USERNAME);
        Permission permission = Permission.valueOf(request.get(PERMISSION));

        try {
            projectService.addParticipant(projectName, creator, username, permission);
        } catch (ProjectException | PermissionException e) {
            return createExceptionJSON(e);
        }

        return createSuccessfulJSON();
    }

    private String deleteParticipant(Map<String, String> request) {
        String projectName = request.get(PROJECT_NAME);
        String creator = request.get(CREATOR);
        String username = request.get(USERNAME);

        try {
            projectService.deleteParticipant(projectName, creator, username);
        } catch (ProjectException | PermissionException e) {
            return createExceptionJSON(e);
        }

        return createSuccessfulJSON();
    }

    private String requestParticipation(Map<String, String> request) {
        String username = request.get(USERNAME);
        String projectName = request.get(PROJECT_NAME);
        Permission permission = Permission.valueOf(request.get(PERMISSION));

        try {
            projectService.requestParticipation(username, projectName, permission);
        } catch (ProjectException e) {
            return createExceptionJSON(e);
        }

        return createSuccessfulJSON();
    }

    private String getParticipationRequest(Map<String, String> request) {
        Map<String, Participation> usersParticipations;
        String username = request.get(USERNAME);
        String projectName = request.get(PROJECT_NAME);

        try {
            usersParticipations = projectService.getParticipationRequest(username, projectName);
        } catch (PermissionException | ProjectException e) {
            return createExceptionJSON(e);
        }

        return createRequestJSON(usersParticipations);
    }

    private String createExceptionJSON(Exception e) {
        return "{" +
                '"' + STATUS + '"' + ':' + '"' + e.getMessage() + '"' +
                '}';
    }

    private String createSuccessfulJSON() {
        return "{" +
                '"' + STATUS + '"' + ':' + '"' + SUCCESSFUL + '"' +
                '}';
    }

    private String createRequestJSON(Object object) {
        return new Gson().toJson(object);
    }

    private String createUnknownOperationJSON() {
        return "{" +
                '"' + STATUS + '"' + ':' + '"' + UNKNOWN_OPERATION + '"' +
                '}';
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(ctx, cause);
    }
}
