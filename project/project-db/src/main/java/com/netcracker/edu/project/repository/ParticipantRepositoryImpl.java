package com.netcracker.edu.project.repository;

import com.netcracker.edu.auth.pool.ConnectionPool;
import com.netcracker.edu.project.user.project.Participation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.netcracker.edu.project.database.SqlRequests.ADD_PARTICIPANT;
import static com.netcracker.edu.project.database.SqlRequests.DELETE_PARTICIPANT;
import static com.netcracker.edu.project.database.SqlRequests.UPDATE_PARTICIPANT;

public class ParticipantRepositoryImpl implements ParticipantRepository {

    static final Logger LOGGER = LoggerFactory.getLogger(ParticipantRepositoryImpl.class);
    private final ConnectionPool connectionPool;

    public ParticipantRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void addParticipant(String projectName, String username, Participation participation) {
        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_PARTICIPANT)) {
            preparedStatement.setString(1, projectName);
            preparedStatement.setString(2, username);
            preparedStatement.setBoolean(3, participation.isActivated());
            preparedStatement.setString(4, participation.getPermission().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Participant {} has not been added", username, e);

        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public void deleteParticipant(String projectName, String username) {
        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PARTICIPANT)) {
            preparedStatement.setString(1, projectName);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Participant {} has not been deleted", username, e);

        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public void updateParticipant(String projectName, String username, Participation participation) {
        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PARTICIPANT)) {
            preparedStatement.setBoolean(1, participation.isActivated());
            preparedStatement.setString(2, participation.getPermission().toString());
            preparedStatement.setString(3, projectName);
            preparedStatement.setString(4, username);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Participant {} has not been updated", username, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }
}
