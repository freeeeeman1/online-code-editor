package com.netcracker.edu.auth.service;

import com.netcracker.edu.auth.api.AuthEntry;
import com.netcracker.edu.auth.api.AuthResult;
import com.netcracker.edu.auth.exception.EmailException;
import com.netcracker.edu.auth.exception.PasswordException;
import com.netcracker.edu.auth.mail.EmailService;
import com.netcracker.edu.project.user.User;
import com.netcracker.edu.auth.repository.UserRepository;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.netcracker.edu.auth.service.PasswordValidator.validatePassword;

public class AuthServiceImpl implements AuthService {

    private final Map<String, User> activationKeys;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.activationKeys = new HashMap<>();
        this.emailService = new EmailService();
    }

    @Override
    public AuthResult signUp(AuthEntry authEntry) {
        String userEmail = authEntry.getUserEmail();
        String userPassword = authEntry.getUserPassword();
        String userLogin = authEntry.getUserLogin();

        User user = userRepository.getByLogin(userLogin);

        if (userRepository.contains(userEmail)) {
            return new AuthResult(userLogin, null, AuthResult.Codes.EMAIL_ALREADY_EXISTS);
        }

        if (user == null) {
            try {
                validatePassword(userPassword);
                validateEmail(userEmail);

                user = new User(userLogin, userPassword, userEmail);

                userRepository.add(user);

                String activationKey = generateActivationKey();
                activationKeys.put(activationKey, user);

                emailService.send("Confirm your email. Enter the activation code " + activationKey, userEmail);

                return new AuthResult(userLogin, "User is registered", AuthResult.Codes.SUCCESSFUL_REGISTRATION);

            } catch (PasswordException e) {
                return new AuthResult(userLogin, e.getMessage(), e.getKey());

            } catch (EmailException e) {
                return new AuthResult(userLogin, e.getMessage(), e.getKey());

            } catch (MessagingException e) {
                return new AuthResult(userLogin, e.getMessage(), AuthResult.Codes.ERROR_EMAIL_SENDING);
            }

        } else {
            return new AuthResult(userLogin, null, AuthResult.Codes.USERNAME_ALREADY_EXISTS);
        }
    }

    @Override
    public AuthResult signIn(AuthEntry authEntry) {
        String userLogin = authEntry.getUserLogin();
        String userPassword = authEntry.getUserPassword();

        User user = userRepository.getByLogin(userLogin);

        if (user != null) {
            if (user.isActivated()) {
                if (userPassword.equals(user.getPassword())) {
                    return new AuthResult(userLogin, "Sign In successfully", AuthResult.Codes.SUCCESSFUL_LOGIN);
                } else {
                    return new AuthResult(userLogin, "Password isn`t correct", AuthResult.Codes.WRONG_PASSWORD);
                }
            } else {
                return new AuthResult(userLogin, "Email wasn`t confirmed", AuthResult.Codes.UNCONFIRMED_EMAIL);
            }
        } else {
            return new AuthResult(userLogin, "Login doesn`t exist", AuthResult.Codes.WRONG_SIGN_IN);
        }
    }

    @Override
    public AuthResult resetPassword(String email) {
        String key = generateActivationKey();
        User user = userRepository.getByEmail(email);

        activationKeys.put(key, user);
        try {
            emailService.send("Enter code " + key, email);

            return new AuthResult(user.getLogin(), "Code was sent on your email", AuthResult.Codes.SUCCESSFUL_EMAIL_SENDING);
        } catch (MessagingException e) {
            return new AuthResult(null, "Email does not exist", AuthResult.Codes.ERROR_EMAIL_SENDING);
        }
    }

    @Override
    public AuthResult updatePassword(String email, String password, String verificationKey) {

        if (!activationKeys.containsKey(verificationKey)) {
            return new AuthResult(email, "Password can not be updated", AuthResult.Codes.UNCONFIRMED_EMAIL);
        }

        User user = userRepository.getByEmail(email);
        String userLogin = user.getLogin();

        try {
            validatePassword(password);

            user.changePassword(password);
            userRepository.updatePassword(user);
            return new AuthResult(userLogin, "Password is updated", AuthResult.Codes.SUCCESSFUL_UPDATE);

        } catch (PasswordException e) {
            return new AuthResult(userLogin, e.getMessage(), e.getKey());
        }
    }

    @Override
    public AuthResult checkCodeForSignUp(String username, String verificationKey) {
        User user = userRepository.getByLogin(username);

        if (user.isActivated()) {
            return new AuthResult(user.getLogin(), "User was already confirmed", AuthResult.Codes.WRONG_ACTIVATION);

        } else if (activationKeys.containsKey(verificationKey)) {

            user.activate();
            userRepository.update(user);

            activationKeys.remove(verificationKey);
            return new AuthResult(user.getLogin(), "User is confirmed", AuthResult.Codes.SUCCESSFUL_CODE);
        } else {
            return new AuthResult(user.getLogin(), "User is not confirmed", AuthResult.Codes.WRONG_CODE);
        }
    }

    @Override
    public AuthResult checkCodeForReset(String email, String verificationKey) {
        User user = userRepository.getByEmail(email);

        if (activationKeys.containsKey(verificationKey)) {
            waitForPasswordReset(verificationKey);

            return new AuthResult(user.getLogin(), "User successful confirmed email", AuthResult.Codes.SUCCESSFUL_CONFIRM_EMAIL);
        } else {
            return new AuthResult(user.getLogin(), "User is not confirmed", AuthResult.Codes.WRONG_CODE);
        }
    }

    private void waitForPasswordReset(String verificationKey) {
        Executors.newSingleThreadScheduledExecutor().schedule(() ->
                activationKeys.remove(verificationKey), 180, TimeUnit.SECONDS);
    }

    private String generateActivationKey() {
        String key = randomString();

        while (activationKeys.containsKey(key)) {
            key = randomString();
        }

        return key;
    }

    private void validateEmail(String email) throws EmailException {
        String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new EmailException(email);
        }
    }

    private String randomString() {
        char LEFT_LIMIT = '0';
        char RIGHT_LIMIT = 'z';
        int TARGET_LENGTH = 6;

        Random random = new Random();

        return random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
                .filter(i -> (i <= '9' || i >= 'A') && (i <= 'Z' || i >= 'a'))
                .limit(TARGET_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
