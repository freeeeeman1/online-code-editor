package com.netcracker.edu.auth.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPoolImpl implements ConnectionPool {

    private final String url;
    private final String username;
    private final String password;

    private final List<Connection> connectionPool;
    private final List<Connection> usedConnections;

    static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPoolImpl.class);

    private ConnectionPoolImpl(String url, String username, String password, List<Connection> connectionPool) {
        this.connectionPool = connectionPool;
        this.usedConnections = new ArrayList<>();
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private static Connection createConnection(String url, String user, String password) {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            LOGGER.error("Connection to {} was not released for {}", url, user, e);
        }

        return null;
    }

    public static ConnectionPoolImpl create(
            String url, String username, String password, int poolSize, String driver){

        List<Connection> connectionPool = new ArrayList<>(poolSize);
        setUpDriver(driver);

        for (int i = 0; i < poolSize; i++) {
            connectionPool.add(createConnection(url, username, password));
        }

        return new ConnectionPoolImpl(url, username, password, connectionPool);
    }

    public static void setUpDriver(String driver) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Class not found", e);
        }
    }

    @Override
    public void releaseConnection(Connection connection) {
        if (usedConnections.contains(connection)) {

            connectionPool.add(connection);
            usedConnections.remove(connection);

            return;
        }

        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Connection was not released", e);
        }
    }

    @Override
    public Connection getConnection() {
        if (connectionPool.isEmpty()) {

            return createConnection(url, username, password);
        }

        Connection connection = connectionPool.remove(connectionPool.size() - 1);

        usedConnections.add(connection);

        return connection;
    }
}
