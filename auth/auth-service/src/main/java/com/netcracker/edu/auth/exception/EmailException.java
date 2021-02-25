package com.netcracker.edu.auth.exception;

import com.netcracker.edu.auth.api.AuthResult;

public class EmailException extends Throwable {

    public EmailException(String email) {
        super("Invalid Email: "+email);
    }

    public AuthResult.Codes getKey(){
        return AuthResult.Codes.MALFORMED_EMAIL;
    }
}
