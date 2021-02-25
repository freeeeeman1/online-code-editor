package com.netcracker.edu.auth.database;

public class DatabaseDescriptor {
    private static final String user = "sa";
    private static final String password = "123";
    private static final String url = "jdbc:h2:~/test";
    private static final String driver = "org.h2.Driver";
    private static final int connectionPoolSize = 20;

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }

    public static String getUrl() {
        return url;
    }

    public static int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public static String getDriver() {
        return driver;
    }
}
