package com.netcracker.edu.project.database;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.netcracker.edu.project.database.SqlRequests.CREATE_FILES_TABLE;
import static com.netcracker.edu.project.database.SqlRequests.CREATE_PARTICIPANTS_TABLE;
import static com.netcracker.edu.project.database.SqlRequests.CREATE_PROJECTS_TABLE;
import static com.netcracker.edu.project.database.SqlRequests.DROP_FILES_TABLE;
import static com.netcracker.edu.project.database.SqlRequests.DROP_PARTICIPANTS_TABLE;
import static com.netcracker.edu.project.database.SqlRequests.DROP_PROJECTS_TABLE;

public class PostgresInitializer {

    static final Logger LOGGER = Logger.getLogger(PostgresInitializer.class);

    public static void createProjectsTable() {
        try (Connection connection = DriverManager.getConnection(
                PostgresDescriptor.getUrl(),
                PostgresDescriptor.getUser(),
                PostgresDescriptor.getPassword());
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(CREATE_PROJECTS_TABLE);
        } catch (SQLException e) {
            LOGGER.error("Table PROJECTS was not created", e);
        }
    }

    public static void createFilesTable() {
        try (Connection connection = DriverManager.getConnection(
                PostgresDescriptor.getUrl(),
                PostgresDescriptor.getUser(),
                PostgresDescriptor.getPassword());
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(CREATE_FILES_TABLE);
        } catch (SQLException e) {
            LOGGER.error("Table FILES was not created", e);
        }
    }

    public static void createParticipantsTable() {
        try (Connection connection = DriverManager.getConnection(
                PostgresDescriptor.getUrl(),
                PostgresDescriptor.getUser(),
                PostgresDescriptor.getPassword());
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(CREATE_PARTICIPANTS_TABLE);
        } catch (SQLException e) {
            LOGGER.error("Table PARTICIPANTS was not created", e);
        }
    }

    public static void dropProjectsTable() {
        try (Connection connection = DriverManager.getConnection(
                PostgresDescriptor.getUrl(),
                PostgresDescriptor.getUser(),
                PostgresDescriptor.getPassword());
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(DROP_PROJECTS_TABLE);
        } catch (SQLException e) {
            LOGGER.error("Table PROJECTS was not dropped", e);
        }
    }

    public static void dropFilesTable() {
        try (Connection connection = DriverManager.getConnection(
                PostgresDescriptor.getUrl(),
                PostgresDescriptor.getUser(),
                PostgresDescriptor.getPassword());
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(DROP_FILES_TABLE);
        } catch (SQLException e) {
            LOGGER.error("Table FILES was not dropped", e);
        }
    }

    public static void dropParticipantsTable() {
        try (Connection connection = DriverManager.getConnection(
                PostgresDescriptor.getUrl(),
                PostgresDescriptor.getUser(),
                PostgresDescriptor.getPassword());
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(DROP_PARTICIPANTS_TABLE);
        } catch (SQLException e) {
            LOGGER.error("Table PARTICIPANTS was not dropped", e);
        }
    }
}
