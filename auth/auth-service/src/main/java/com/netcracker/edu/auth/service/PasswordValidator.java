package com.netcracker.edu.auth.service;

import com.netcracker.edu.auth.api.AuthResult;
import com.netcracker.edu.auth.exception.PasswordException;

public class PasswordValidator {

    static void validatePassword(String password) throws PasswordException {
        if ((password.length() < 4) || (password.length() > 16)) {
            throw new PasswordException(AuthResult.Codes.INCORRECT_PASSWORD_LENGTH);
        }

        if ((password.contains(" "))) {
            throw new PasswordException(AuthResult.Codes.INCORRECT_PASSWORD_SPACE);
        }

        if (!isDigitExists(password)) {
            throw new PasswordException(AuthResult.Codes.INCORRECT_PASSWORD_DIGITS);
        }

        if (!isCapitalCharsExists(password)) {
            throw new PasswordException(AuthResult.Codes.INCORRECT_PASSWORD_UPPERCASE);
        }

        if (!isSmallCharsExists(password)) {
            throw new PasswordException(AuthResult.Codes.INCORRECT_PASSWORD_LOWERCASE);
        }

    }

    private static boolean isDigitExists(String password){
        for (char y = '0'; y <= '9'; y++) {
            if (password.contains(String.valueOf(y))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCapitalCharsExists(String password){
        for (char y = 'A'; y <= 'Z'; y++) {
            if (password.contains(String.valueOf(y))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSmallCharsExists(String password){
        for (char y = 'a'; y <= 'z'; y++) {
            if (password.contains(String.valueOf(y))) {
                return true;
            }
        }
        return false;
    }
}
