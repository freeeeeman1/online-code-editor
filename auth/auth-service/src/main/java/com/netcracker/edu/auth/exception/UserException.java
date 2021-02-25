package com.netcracker.edu.auth.exception;

import com.netcracker.edu.auth.api.AuthResult;

public class UserException extends Exception {

    public UserException(String userLogin) {
        super("Username already exist " + userLogin);
    }

    public AuthResult.Codes getKey(){
        return AuthResult.Codes.LOGIN_EXISTS;
    }
}
