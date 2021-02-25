package com.netcracker.edu.project.database;

public class SqlRequests {

    public static final String CREATE_PROJECTS_TABLE =
            "CREATE TABLE IF NOT EXISTS PROJECTS (" +
                    "ID SERIAL PRIMARY KEY," +
                    "PROJECT_NAME VARCHAR(255) UNIQUE NOT NULL," +
                    "OWNER_USERNAME VARCHAR(255) NOT NULL," +
                    "VISIBILITY VARCHAR(255) NOT NULL);";

    public static final String CREATE_FILES_TABLE =
            "CREATE TABLE IF NOT EXISTS FILES (" +
                    "ID SERIAL PRIMARY KEY," +
                    "PROJECT_NAME VARCHAR(255) REFERENCES PROJECTS (PROJECT_NAME) ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FILENAME VARCHAR(255) NOT NULL," +
                    "PATH VARCHAR(255) UNIQUE NOT NULL," +
                    "IS_DIRECTORY VARCHAR(255) NOT NULL," +
                    "CONTENT TEXT);";

    public static final String CREATE_PARTICIPANTS_TABLE =
            "CREATE TABLE IF NOT EXISTS PARTICIPANTS (" +
                    "ID SERIAL PRIMARY KEY," +
                    "PROJECT_NAME VARCHAR(255) REFERENCES PROJECTS (PROJECT_NAME) ON DELETE CASCADE ON UPDATE CASCADE," +
                    "USERNAME VARCHAR(255) NOT NULL," +
                    "ACTIVATED BOOLEAN NOT NULL," +
                    "PERMISSION VARCHAR(20) NOT NULL)";

    public static final String DROP_PROJECTS_TABLE = "DROP TABLE IF EXISTS PROJECTS CASCADE";

    public static final String DROP_FILES_TABLE = "DROP TABLE IF EXISTS FILES";

    public static final String DROP_PARTICIPANTS_TABLE = "DROP TABLE IF EXISTS PARTICIPANTS";

    public static final String ADD_PROJECT = "INSERT INTO PROJECTS(PROJECT_NAME, OWNER_USERNAME, VISIBILITY) VALUES (?, ?, ?)";

    public static final String GET_PROJECT = "SELECT * FROM PROJECTS, FILES, PARTICIPANTS" +
            "WHERE PROJECTS.PROJECT_NAME = FILES.PROJECT_NAME" +
            "AND FILES.PROJECT_NAME = PARTICIPANTS.PROJECT_NAME" +
            "AND PARTICIPANTS.PROJECT_NAME = ?";

    public static final String CONTAINS_PROJECT = "SELECT ID FROM PROJECTS WHERE PROJECT_NAME = ?;";

    public static final String GET_PROJECT_NAMES = "(SELECT DISTINCT PROJECT_NAME FROM PARTICIPANTS" +
            "WHERE PARTICIPANTS.USERNAME = ? AND PARTICIPANTS.ACTIVATED = TRUE)";

    public static final String DELETE_PROJECT = "DELETE FROM PROJECTS WHERE PROJECT_NAME = ?";

    public static final String ADD_ROOT_PARTICIPANT = "INSERT INTO PARTICIPANTS(ID, PROJECT_NAME, USERNAME, ACTIVATED, PERMISSION) " +
            "VALUES (DEFAULT, ?, ?, ?, ?)";

    public static final String ADD_ROOT_DIRECTORY = "INSERT INTO FILES(ID, PROJECT_NAME, FILENAME, PATH, IS_DIRECTORY, CONTENT) " +
            "VALUES (DEFAULT , ?, ?, ?, true, null)";

    public static final String ADD_FILE = "INSERT INTO FILES(ID, PROJECT_NAME, FILENAME, PATH, IS_DIRECTORY, CONTENT) " +
            "VALUES (DEFAULT , ?, ?, ?, ?, ?)";

    public static final String DELETE_FILE = "DELETE FROM FILES WHERE PATH LIKE ? OR PATH LIKE ?";

    public static final String UPDATE_CONTENT_FILE = "UPDATE FILES SET CONTENT = ? WHERE PATH = ?";

    public static final String ADD_PARTICIPANT = "INSERT INTO PARTICIPANTS(ID, PROJECT_NAME, USERNAME, ACTIVATED, PERMISSION)" +
            " VALUES (DEFAULT, ?, ?, ?, ?)";

    public static final String DELETE_PARTICIPANT = "DELETE FROM PARTICIPANTS VALUES WHERE PROJECT_NAME = ? AND  USERNAME = ?";

    public static final String UPDATE_PARTICIPANT = "UPDATE PARTICIPANTS SET ACTIVATED = ?, PERMISSION = ?" +
            "WHERE PROJECT_NAME = ? AND USERNAME = ?";
}
