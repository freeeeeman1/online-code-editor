package com.netcracker.edu.project.database;

public class PostgresDescriptor {

    private static final String user = "postgres";
    private static final String password = "root";
    private static final String url = "jdbc:postgresql://localhost:5432/projects";
    private static final String driver = "org.postgresql.Driver";
    private static final int INITIAL_POOL_SIZE = 20;

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }

    public static String getUrl() {
        return url;
    }

    public static String getDriver() {
        return driver;
    }

    public static int getInitialPoolSize() {
        return INITIAL_POOL_SIZE;
    }
}
