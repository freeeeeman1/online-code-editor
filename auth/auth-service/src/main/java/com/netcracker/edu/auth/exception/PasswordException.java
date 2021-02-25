package com.netcracker.edu.auth.exception;

import com.netcracker.edu.auth.api.AuthResult;

import  java.util.HashMap;
import java.util.Map;

public class PasswordException extends Exception {
    AuthResult.Codes conditionViolated;

    static Map<AuthResult.Codes, String> exceptionText = new HashMap<>();

    static{
        exceptionText.put(AuthResult.Codes.INCORRECT_PASSWORD_LENGTH, "Password length should be"
                + " between 4 to 16 characters");
        exceptionText.put(AuthResult.Codes.INCORRECT_PASSWORD_SPACE, "Password should not"
                + " contain any space");
        exceptionText.put(AuthResult.Codes.INCORRECT_PASSWORD_DIGITS, "Password should contain"
                + " at least one digit(0-9)");
        exceptionText.put(AuthResult.Codes.INCORRECT_PASSWORD_CHARS, "Password should contain at "
                + "least one special character ( @, #, %, &, !, $, *, / )");
        exceptionText.put(AuthResult.Codes.INCORRECT_PASSWORD_UPPERCASE, "Password should contain at"
                + " least one uppercase letter(A-Z)");
        exceptionText.put(AuthResult.Codes.INCORRECT_PASSWORD_LOWERCASE, "Password should contain at"
                + " least one lowercase letter(a-z)");
    }

    public PasswordException(AuthResult.Codes conditionViolated) {
        super("Invalid Password: ");
        this.conditionViolated = conditionViolated;
    }

    public String getMessage() {
        return exceptionText.get(conditionViolated);
    }

    public AuthResult.Codes getKey(){
        return conditionViolated;
    }
}
