package com.netcracker.edu.project.repository;

import com.netcracker.edu.auth.pool.ConnectionPool;
import com.netcracker.edu.project.user.exception.FileException;
import com.netcracker.edu.project.user.project.Participation;
import com.netcracker.edu.project.user.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.netcracker.edu.project.database.SqlRequests.ADD_PROJECT;
import static com.netcracker.edu.project.database.SqlRequests.ADD_ROOT_DIRECTORY;
import static com.netcracker.edu.project.database.SqlRequests.ADD_ROOT_PARTICIPANT;
import static com.netcracker.edu.project.database.SqlRequests.CONTAINS_PROJECT;
import static com.netcracker.edu.project.database.SqlRequests.DELETE_PROJECT;
import static com.netcracker.edu.project.database.SqlRequests.GET_PROJECT;
import static com.netcracker.edu.project.database.SqlRequests.GET_PROJECT_NAMES;

public class ProjectRepositoryImpl implements ProjectRepository {

    private final Map<String, Project> projects;
    private final ConnectionPool connectionPool;
    static final Logger LOGGER = LoggerFactory.getLogger(ProjectRepositoryImpl.class);

    public ProjectRepositoryImpl(ConnectionPool connectionPool) {
        this.projects = new ConcurrentHashMap<>();
        this.connectionPool = connectionPool;
    }

    @Override
    public void addProject(Project project) {
        String projectName = project.getProjectName();
        String ownerUsername = project.getOwnerUsername();

        projects.put(projectName, project);

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_PROJECT)) {
            preparedStatement.setString(1, projectName);
            preparedStatement.setString(2, ownerUsername);
            preparedStatement.setObject(3, project.getVisibility().toString());

            preparedStatement.executeUpdate();

            Participation participant = project.getParticipant(ownerUsername);
            addRootParticipant(connection, projectName, participant, ownerUsername);

            addRootDirectory(connection, projectName);

        } catch (SQLException e) {
            LOGGER.error("Project {} has not been saved", projectName, e);

        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public Project getProject(String projectName) {
        if (projects.containsKey(projectName)) {
            return projects.get(projectName);
        }

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_PROJECT)) {
            preparedStatement.setString(1, projectName);
            ResultSet result = preparedStatement.executeQuery();

            result.next();
            String resultName = result.getString("PROJECT_NAME");
            String ownerUsername = result.getString("OWNER_USERNAME");
            Project.Visibility visibility = Project.Visibility.valueOf(result.getString("VISIBILITY"));

            Project project = new Project.Builder(resultName, ownerUsername, visibility).build();

            while (result.next()) {
                String fileName = result.getString("FILENAME");
                String path = result.getString("PATH");
                String content = result.getString("CONTENT");

                if (result.getBoolean("IS_DIRECTORY")) {
                    project.addFile(fileName, path, content);
                } else {
                    project.addDirectory(fileName, path);
                }
            }

            projects.put(projectName, project);

            return project;

        } catch (SQLException | FileException e) {
            LOGGER.error("Project {} was not fetched from database", projectName, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return null;
    }

    @Override
    public boolean contains(String projectName) {
        if (projects.containsKey(projectName)) {
            return true;
        }

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(CONTAINS_PROJECT)) {
            preparedStatement.setString(1, projectName);
            ResultSet result = preparedStatement.executeQuery();

            return result.next();

        } catch (SQLException e) {
            LOGGER.error("Project {} has not been verified", projectName, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }

        return false;
    }

    @Override
    public List<String> getProjectNames(String username) {
        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_PROJECT_NAMES)) {
            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();

            List<String> projectNames = new ArrayList<>();
            while (result.next()) {
                projectNames.add(result.getString("PROJECT_NAME"));
            }

            return projectNames;

        } catch (SQLException e) {
            LOGGER.error("Project names has not been fetched by username {}", username, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return null;
    }


    @Override
    public void deleteProject(String projectName) {
        projects.remove(projectName);

        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PROJECT)) {
            preparedStatement.setString(1, projectName);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("Project {} has not been deleted", projectName, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    private void addRootParticipant(
            Connection connection, String projectName, Participation participant, String ownerUsername) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(ADD_ROOT_PARTICIPANT);

        preparedStatement.setString(1, projectName);
        preparedStatement.setString(2, ownerUsername);
        preparedStatement.setBoolean(3, participant.isActivated());
        preparedStatement.setString(4, participant.getPermission().toString());

        preparedStatement.executeUpdate();
    }

    private void addRootDirectory(Connection connection, String projectName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(ADD_ROOT_DIRECTORY);

        preparedStatement.setString(1, projectName);
        preparedStatement.setString(2, projectName);
        preparedStatement.setString(3, "/" + projectName);

        preparedStatement.executeUpdate();
    }
}
