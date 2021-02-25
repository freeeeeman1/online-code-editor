package com.netcracker.edu.auth.service;

import com.netcracker.edu.auth.api.AuthEntry;
import com.netcracker.edu.auth.api.AuthResult;
import com.netcracker.edu.auth.exception.UserException;

public interface AuthService {

    /**
     *
     * @param authEntry
     * @return AuthResult
     * @throws UserException when login already exist
     */
    AuthResult signUp(AuthEntry authEntry) throws UserException;

    /**
     *
     * @param registrationCard
     * @return AuthResult
     */
    AuthResult signIn(AuthEntry registrationCard);

    AuthResult checkCodeForSignUp(String username, String verificationKey);

    /**
     *
     * @param verificationCode
     * @param email
     * @return
     */
    AuthResult checkCodeForReset(String verificationCode, String email);

    /**
     *
     * @param email
     */
    AuthResult resetPassword(String email);

    /**
     *
     * @param password
     */
    AuthResult updatePassword(String email, String password, String verificationKey);
}

