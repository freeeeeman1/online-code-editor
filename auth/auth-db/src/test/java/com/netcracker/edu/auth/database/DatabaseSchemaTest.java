package com.netcracker.edu.auth.database;

import com.netcracker.edu.auth.pool.ConnectionPool;
import com.netcracker.edu.auth.pool.ConnectionPoolImpl;
import org.h2.jdbc.JdbcSQLSyntaxErrorException;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;

public class DatabaseSchemaTest {
    private static Server server;
    private ConnectionPool connectionPool;

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
    }

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
    public void ShouldCreateUsersTable() throws SQLException {
        Connection connection = connectionPool.getConnection();
        String query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'USERS'";
        Statement statement = connection.createStatement();

        DatabaseSchema.createUsersTable();

        ResultSet rs = statement.executeQuery(query);
        assertTrue(rs.next());
    }

    @Test(expected = JdbcSQLSyntaxErrorException.class)
    public void shouldDeleteUsersTable() throws SQLException {
        Connection connection = connectionPool.getConnection();
        String query = "SELECT * FROM USERS;";
        Statement statement = connection.createStatement();

        DatabaseSchema.deleteUsersTable();

        statement.executeQuery(query);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.stop();
    }
}
