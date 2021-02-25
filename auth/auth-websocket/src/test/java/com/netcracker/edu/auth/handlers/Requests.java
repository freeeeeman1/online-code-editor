package com.netcracker.edu.auth.handlers;

public class Requests {

    public final static String SUCCESSFUL_SIGN_IN = "{" +
            "\"operation\": \"login\", " +
            "\"login\": \"login\", " +
            "\"password\": \"password\" " +
            "}";

    public final static String UNSUCCESSFUL_SIGN_IN = "{" +
            "\"operation\": \"login\", " +
            "\"login\": \"login111\"," +
            "\"password\": \"password\"" +
            "}";

    public final static String SUCCESSFUL_SIGN_UP = "{" +
            "\"operation\": \"registration\"," +
            "\"login\": \"login\"," +
            "\"password\": \"password\"," +
            "\"email\": \"email\"" +
            "}";

    public final static String UNSUCCESSFUL_SIGN_UP = "{" +
            "\"operation\": \"registration\"," +
            "\"login\": \"a\"," +
            "\"password\": \"password\"," +
            "\"email\": \"email\"" +
            "}";

    public final static String SUCCESSFUL_RESET = "{" +
            "\"operation\": \"reset-password\"," +
            "\"email\": \"email\"" +
            "}";

    public final static String UNSUCCESSFUL_RESET = "{" +
            "\"operation\": \"reset-password\"," +
            "\"email\": \"wrong email\"" +
            "}";

    public final static String SUCCESSFUL_UPDATE_PASSWORD = "{" +
            "\"operation\": \"update-password\"," +
            "\"password\": \"password\"," +
            "\"email\": \"email\"" +
            "}";

    public final static String SUCCESSFUL_CONFIRM_EMAIL_SIGN_UP = "{" +
            "\"operation\": \"confirm-email-registration\"," +
            "\"code\": \"NJKF343\"," +
            "\"username\": \"Anna\"" +
            "}";

    public final static String SUCCESSFUL_CONFIRM_EMAIL_RESET = "{" +
            "\"operation\": \"confirm-email-reset\"," +
            "\"code\": \"NJKF343\"," +
            "\"username\": \"Anna\"" +
            "}";

    public final static String UNSUCCESSFUL_SIGN_IN_WITH_COOKIE = "{" +
            "\"operation\":" + "\"cookie\"," +
            "\"cookie\":" + "\"max:21 path\"" +
            "}";
}
