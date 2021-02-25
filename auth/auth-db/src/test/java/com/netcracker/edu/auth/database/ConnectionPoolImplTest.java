package com.netcracker.edu.auth.database;

import com.netcracker.edu.auth.pool.ConnectionPool;
import com.netcracker.edu.auth.pool.ConnectionPoolImpl;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConnectionPoolImplTest {
    private ConnectionPool connectionPool;

    @Before
    public void setUp() {
        connectionPool = ConnectionPoolImpl.create(
                DatabaseDescriptor.getUrl(),
                DatabaseDescriptor.getUser(),
                DatabaseDescriptor.getPassword(),
                DatabaseDescriptor.getConnectionPoolSize(),
                DatabaseDescriptor.getDriver());
    }

    @Test
    public void itShouldReleaseConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(
                DatabaseDescriptor.getUrl(),
                DatabaseDescriptor.getUser(),
                DatabaseDescriptor.getPassword());

        connectionPool.releaseConnection(connection);

        assertTrue(connection.isClosed());
    }

    @Test
    public void itShouldGetMoreThan20Connections() {
        for (int i = 0; i < 20; i++) {
            connectionPool.getConnection();
        }

        Connection actualConnection = connectionPool.getConnection();

        assertNotNull(actualConnection);
    }
}
