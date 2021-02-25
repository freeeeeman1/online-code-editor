package com.netcracker.edu.project.repository;

import com.netcracker.edu.auth.pool.ConnectionPool;
import com.netcracker.edu.project.user.project.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.netcracker.edu.project.database.SqlRequests.ADD_FILE;
import static com.netcracker.edu.project.database.SqlRequests.DELETE_FILE;
import static com.netcracker.edu.project.database.SqlRequests.UPDATE_CONTENT_FILE;

public class FileRepositoryImpl implements FileRepository {

    private final ConnectionPool connectionPool;
    static final Logger LOGGER = LoggerFactory.getLogger(FileRepositoryImpl.class);

    public FileRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void addFile(File file, String projectName) {
        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_FILE)) {
            preparedStatement.setString(1, projectName);
            preparedStatement.setString(2, file.getName());
            preparedStatement.setString(3, file.getPath());
            preparedStatement.setBoolean(4, file.isDirectory());
            preparedStatement.setString(5, file.getContent());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("File {} has not been added", file.getName(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public void deleteFile(String path) {
        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FILE)) {
            preparedStatement.setString(1, path);
            preparedStatement.setString(2, path + "%");

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("File {} has not been deleted", path, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public void updateContentFile(String path, String content) {
        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CONTENT_FILE)) {
            preparedStatement.setString(1, content);
            preparedStatement.setString(2, path);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("File {} has not been updated", path, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }
}
