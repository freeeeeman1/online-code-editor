package com.netcracker.edu.auth.pool;

import java.sql.Connection;

public interface ConnectionPool {

    Connection getConnection();

    /**
     * Release used connection for further usage
     *
     * @param connection
     */
    void releaseConnection(Connection connection);
}