package com.netcracker.edu.project.client;

public class Requests {

    public static final String GET_USER_CONTENT = "{" +
            "\"operation\": \"get_user_content\", " +
            "\"username\": \"Vadim\" " +
            "}";

    public static final String GET_PROJECT_CONTENT = "{" +
            "\"operation\": \"get_project_content\", " +
            "\"project_name\": \"NetCracker\" " +
            "}";

    public static final String GET_PROJECT_CONTENT_WRONG = "{" +
            "\"operation\": \"get_project_content\", " +
            "\"project_name\": \"qweqwe\" " +
            "}";

    public static final String CREATE_PROJECT= "{" +
            "\"operation\": \"create_project\", " +
            "\"username\": \"Vadim\", " +
            "\"project_name\": \"NetCracker\", " +
            "\"visibility\": \"PUBLIC\" " +
            "}";

    public static final String DELETE_PROJECT = "{" +
            "\"operation\": \"delete_project\", " +
            "\"username\": \"Vadim\", " +
            "\"project_name\": \"NetCracker\" " +
            "}";

    public static final String ADD_DIRECTORY = "{" +
            "\"operation\": \"add_directory\", " +
            "\"username\": \"Vadim\", " +
            "\"project_name\": \"NetCracker\", " +
            "\"file_name\": \"file\", " +
            "\"path\": \"path\" " +
            "}";

    public static final String ADD_FILE = "{" +
            "\"operation\": \"add_file\", " +
            "\"username\": \"Vadim\", " +
            "\"project_name\": \"NetCracker\", " +
            "\"file_name\": \"file\", " +
            "\"path\": \"path\" " +
            "}";

    public static final String READ_FILE = "{" +
            "\"operation\": \"read_file\", " +
            "\"username\": \"Vadim\", " +
            "\"project_name\": \"NetCracker\", " +
            "\"path\": \"path\" " +
            "}";

    public static final String UPDATE_FILE = "{" +
            "\"operation\": \"update_file\", " +
            "\"username\": \"Vadim\", " +
            "\"project_name\": \"NetCracker\", " +
            "\"path\": \"path|qweqwe|qweqwe|w\", " +
            "\"content\": \"content\" " +
            "}";

    public static final String DELETE_FILE = "{" +
            "\"operation\": \"delete_file\", " +
            "\"path\": \"path\", " +
            "\"project_name\": \"NetCracker\" " +
            "}";

    public static final String ADD_PARTICIPANT = "{" +
            "\"operation\": \"add_participant\", " +
            "\"username\": \"Vadim\", " +
            "\"project_name\": \"NetCracker\", " +
            "\"creator\": \"Vadim\", " +
            "\"permission\": \"CREATOR\" " +
            "}";

    public static final String DELETE_PARTICIPANT = "{" +
            "\"operation\": \"delete_participant\", " +
            "\"username\": \"Vadim\", " +
            "\"project_name\": \"NetCracker\", " +
            "\"creator\": \"Vadim\" " +
            "}";

    public static final String REQUEST_PARTICIPATION = "{" +
            "\"operation\": \"request_participation\", " +
            "\"username\": \"Vadim\", " +
            "\"project_name\": \"NetCracker\", " +
            "\"permission\": \"CREATOR\" " +
            "}";

    public static final String GET_PARTICIPATION_REQUEST = "{" +
            "\"operation\": \"get_participation_request\", " +
            "\"username\": \"Ayaz\", " +
            "\"project_name\": \"NetCracker\" " +
            "}";

    public static final String STATUS_SUCCESSFUL = "{" +
            '"' + "status" + '"' + ':' + '"' + "successful" + '"' +
            '}';

    public static final String UNKNOWN_OPERATION = "{" +
            '"' + "operation" + '"' + ':' + '"' + "qweqweqwe" + '"' +
            '}';

   public static  final String STATUS_UNKNOWN_OPERATION = "{" +
                '"' + "status" + '"' + ':' + '"' + "unknown_operation" + '"' +
                '}';

}
