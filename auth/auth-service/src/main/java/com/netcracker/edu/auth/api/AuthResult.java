package com.netcracker.edu.auth.api;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthResult {
    private String login;
    private String status;
    private Codes code;

    public AuthResult(){
    }

    public AuthResult(String login, String status, Codes code) {
        this.login = login;
        this.status = status;
        this.code = code;
    }

    public AuthResult(String login, Codes code) {
        this.login = login;
        this.code = code;
        this.status = null;
    }



    public void setLogin(String login) {
        this.login = login;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCode(Codes code) {
        this.code = code;
    }

    public String getLogin() {
        return login;
    }

    public String getStatus() {
        return status;
    }

    public Codes getCode() {
        return code;
    }

    public boolean isSuccessful(){
        String EMAIL_REGEX = "^SUCCESSFUL.*";

        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(code.toString());
        return matcher.matches();
    }


    @Override
    public String toString() {
        return "Result the method {" +
                "login = " + login +
                ", status = " + status +
                ", code = " + code +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthResult that = (AuthResult) o;
        return login.equals(that.login) &&
                code == that.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, code);
    }

    public enum Codes {
        SUCCESSFUL_LOGIN,
        SUCCESSFUL_LOGIN_WITH_COOKIE,
        SUCCESSFUL_REGISTRATION,
        SUCCESSFUL_PASSWORD,
        SUCCESSFUL_ACTIVATION,
        SUCCESSFUL_UPDATE,
        SUCCESSFUL_CODE,
        SUCCESSFUL_CONFIRM_EMAIL,
        SUCCESSFUL_EMAIL_SENDING,
        WRONG_UPDATE,
        WRONG_PASSWORD,
        WRONG_ACTIVATION,
        WRONG_CODE,
        WRONG_SIGN_IN,
        INCORRECT_PASSWORD_LENGTH,
        INCORRECT_PASSWORD_SPACE,
        INCORRECT_PASSWORD_DIGITS,
        INCORRECT_PASSWORD_CHARS,
        INCORRECT_PASSWORD_UPPERCASE,
        INCORRECT_PASSWORD_LOWERCASE,
        MALFORMED_EMAIL,
        UNCONFIRMED_EMAIL,
        LOGIN_EXISTS,
        ERROR_EMAIL_SENDING,
        USERNAME_ALREADY_EXISTS,
        EMAIL_ALREADY_EXISTS;
    }
}

