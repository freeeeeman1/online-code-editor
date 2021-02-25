package com.netcracker.edu.auth.database;

import com.netcracker.edu.project.user.UserRole;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class DatabaseSchema {

    static final Logger LOGGER = Logger.getLogger(DatabaseSchema.class);

    public static void createUsersTable() {
        String query = "CREATE TABLE IF NOT EXISTS USERS (" +
                "LOGIN VARCHAR(255) PRIMARY KEY," +
                "PASSWORD VARCHAR(255)," +
                "EMAIL VARCHAR(255) NOT NULL," +
                "ACTIVE BOOLEAN," +
                "ROLE VARCHAR(255));";

        try (Connection connection = DriverManager.getConnection(
                DatabaseDescriptor.getUrl(),
                DatabaseDescriptor.getUser(),
                DatabaseDescriptor.getPassword());
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(query);
        } catch (SQLException e) {
            LOGGER.error("Table USERS was not created", e);
        }
    }

    public static void createAdmin() {
        String query = "INSERT INTO USERS(LOGIN, PASSWORD, EMAIL, ROLE, ACTIVE) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(
                DatabaseDescriptor.getUrl(),
                DatabaseDescriptor.getUser(),
                DatabaseDescriptor.getPassword())) {

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, "root");
            statement.setString(2, "root");
            statement.setString(3, "root@root.ru");
            statement.setObject(4, UserRole.ADMIN);
            statement.setBoolean(5, true);

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Admin was not created", e);
        }
    }

    public static void deleteUsersTable() {
        String query = "DROP TABLE USERS";

        try (Connection connection = DriverManager.getConnection(
                DatabaseDescriptor.getUrl(),
                DatabaseDescriptor.getUser(),
                DatabaseDescriptor.getPassword());
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(query);
        } catch (SQLException e) {
            LOGGER.error("Table USERS was not deleted", e);
        }
    }
}
