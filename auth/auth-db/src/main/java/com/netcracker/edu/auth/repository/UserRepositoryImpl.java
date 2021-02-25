package com.netcracker.edu.auth.repository;

import com.netcracker.edu.auth.database.DatabaseDescriptor;
import com.netcracker.edu.auth.pool.ConnectionPool;
import com.netcracker.edu.auth.pool.ConnectionPoolImpl;
import com.netcracker.edu.project.user.User;
import com.netcracker.edu.project.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class UserRepositoryImpl implements UserRepository {
    private final ConnectionPool connectionPool;
    private final HashMap<String, User> loginUserMap;
    final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    public UserRepositoryImpl() {
        this.connectionPool = ConnectionPoolImpl.create(
                DatabaseDescriptor.getUrl(),
                DatabaseDescriptor.getUser(),
                DatabaseDescriptor.getPassword(),
                DatabaseDescriptor.getConnectionPoolSize(),
                DatabaseDescriptor.getDriver());
        this.loginUserMap = new HashMap<>();
    }

    @Override
    public void add(User user) {
        Connection connection = connectionPool.getConnection();
        String query = "INSERT INTO USERS(LOGIN, PASSWORD, EMAIL, ROLE, ACTIVE) VALUES (?,?,?,?,?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setObject(4, UserRole.USER);
            statement.setBoolean(5, user.isActivated());

            statement.executeUpdate();
            loginUserMap.put(user.getLogin(), user);
        } catch (SQLException e) {
            logger.error("User {} was not added", user.getLogin(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public void update(User user) {
        Connection connection = connectionPool.getConnection();
        String query = "UPDATE USERS SET(LOGIN, PASSWORD, EMAIL, ROLE, ACTIVE) = (?,?,?,?,?) WHERE LOGIN = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setObject(4, UserRole.USER);
            statement.setBoolean(5, user.isActivated());
            statement.setString(6, user.getLogin());

            statement.executeUpdate();

            loginUserMap.put(user.getLogin(), user);
        } catch (SQLException e) {
            logger.error("User {} was not updated", user.getLogin(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public User getByLogin(String login) {
        Connection connection = connectionPool.getConnection();
        String query = "SELECT * FROM USERS WHERE LOGIN = ?";

        if (loginUserMap.containsKey(login)) {
            return loginUserMap.get(login);
        }

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                User user = new User(result.getString("LOGIN"),
                        result.getString("PASSWORD"),
                        result.getString("EMAIL"));

                if(result.getBoolean("ACTIVE")) {
                    user.activate();
                }

                return user;
            }
        } catch (SQLException e) {
            logger.error("User {} was not fetched from database", login, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }

        return null;
    }

    @Override
    public User getByEmail(String email) {
        Connection connection = connectionPool.getConnection();
        String query = "SELECT * FROM USERS WHERE EMAIL = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setString(1, email);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                User user = new User(result.getString("LOGIN"),
                        result.getString("PASSWORD"),
                        result.getString("EMAIL"));

                if(result.getBoolean("ACTIVE")) {
                    user.activate();
                }

                return user;
            }
        } catch (SQLException e) {
            logger.error("User was not fetched from database by email {}", email, e);
        }

        return null;
    }

    @Override
    public boolean contains(String email) {
        Connection connection = connectionPool.getConnection();
        String query = "SELECT 1 FROM USERS WHERE EMAIL = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setString(1, email);

            ResultSet result = statement.executeQuery();

            return result.next();
        } catch (SQLException e) {
            logger.error("Unsuccessful check for existence {}", email, e);
        }
        return false;
    }

    @Override
    public void updatePassword(User user) {
        Connection connection = connectionPool.getConnection();
        String query = "SELECT * FROM USERS WHERE LOGIN = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query,
                             ResultSet.TYPE_FORWARD_ONLY,
                             ResultSet.CONCUR_UPDATABLE)) {

            statement.setString(1, user.getLogin());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                resultSet.updateString("PASSWORD", user.getPassword());
                resultSet.updateRow();
            }

            loginUserMap.put(user.getLogin(), user);
        } catch (SQLException e) {
            logger.error("User's password {} has not been updated", user.getLogin(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public void remove(User user) {
        Connection connection = connectionPool.getConnection();
        String query = "SELECT * FROM USERS WHERE LOGIN = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query,
                             ResultSet.TYPE_FORWARD_ONLY,
                             ResultSet.CONCUR_UPDATABLE)) {

            statement.setString(1, user.getLogin());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                resultSet.deleteRow();
            }

            loginUserMap.remove(user.getLogin(), user);
        } catch (SQLException e) {
            logger.error("User {} has not been removed", user.getLogin(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public void clear() {
        Connection connection = connectionPool.getConnection();
        String query = "DELETE FROM USERS";

        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate(query);

            loginUserMap.clear();
        } catch (SQLException e) {
            logger.error("Table was not cleared", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }
}
