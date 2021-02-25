package com.netcracker.edu.auth.handlers;

import auxilary.WebSocketClient;
import com.google.gson.Gson;
import com.netcracker.edu.auth.api.AuthResult;
import com.netcracker.edu.auth.server.Server;
import com.netcracker.edu.auth.service.AuthServiceImpl;
import com.netcracker.edu.auth.session.SessionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayDeque;
import java.util.HashMap;

import static com.netcracker.edu.auth.handlers.Requests.SUCCESSFUL_CONFIRM_EMAIL_SIGN_UP;
import static com.netcracker.edu.auth.handlers.Requests.SUCCESSFUL_CONFIRM_EMAIL_RESET;
import static com.netcracker.edu.auth.handlers.Requests.SUCCESSFUL_RESET;
import static com.netcracker.edu.auth.handlers.Requests.SUCCESSFUL_SIGN_IN;
import static com.netcracker.edu.auth.handlers.Requests.SUCCESSFUL_SIGN_UP;
import static com.netcracker.edu.auth.handlers.Requests.SUCCESSFUL_UPDATE_PASSWORD;
import static com.netcracker.edu.auth.handlers.Requests.UNSUCCESSFUL_RESET;
import static com.netcracker.edu.auth.handlers.Requests.UNSUCCESSFUL_SIGN_IN;
import static com.netcracker.edu.auth.handlers.Requests.UNSUCCESSFUL_SIGN_IN_WITH_COOKIE;
import static com.netcracker.edu.auth.handlers.Requests.UNSUCCESSFUL_SIGN_UP;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthHandlerTest {

    private WebSocketClient client;
    private AuthResult authResult;
    private AuthServiceImpl authService;
    private SessionManager sessionManager;
    private Thread serverThread;
    private ArrayDeque<String> responses;

    @Before
    public void initTest() {
        this.responses = new ArrayDeque<>();
        this.authResult = new AuthResult();
        this.authService = mock(AuthServiceImpl.class);
        this.sessionManager = new SessionManager();

        this.serverThread = new Thread(() -> {
            Server server = new Server(authService, sessionManager);
            try {
                server.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        serverThread.start();
    }

    @Test
    public void successfulSignIn() throws InterruptedException {
        this.client = new WebSocketClient(responses, SUCCESSFUL_SIGN_IN);

        String expectedResponse = "You are signed in";
        authResult.setStatus(expectedResponse);
        authResult.setCode(AuthResult.Codes.SUCCESSFUL_LOGIN);
        authResult.setLogin("Max");
        when(authService.signIn(Mockito.any())).thenReturn(authResult);

        client.sendRequest();
        String status = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("status");

        assertEquals(expectedResponse, status);
    }

    @Test
    public void unsuccessfulSignIn() throws InterruptedException {
        this.client = new WebSocketClient(responses, UNSUCCESSFUL_SIGN_IN);

        String expectedResponse = "You are not signed in";
        authResult.setStatus(expectedResponse);
        when(authService.signIn(Mockito.any())).thenReturn(authResult);

        client.sendRequest();
        String status = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("status");

        assertEquals(expectedResponse, status);
    }

    @Test
    public void successfulSignUp() throws InterruptedException {
        this.client = new WebSocketClient(responses, SUCCESSFUL_SIGN_UP);

        String expectedResponse = "You are signed up";
        authResult.setStatus(expectedResponse);
        when(authService.signUp(Mockito.any())).thenReturn(authResult);

        client.sendRequest();
        String status = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("status");

        assertEquals(expectedResponse, status);
    }

    @Test
    public void unsuccessfulSignUp() throws InterruptedException {
        this.client = new WebSocketClient(responses, UNSUCCESSFUL_SIGN_UP);

        String expectedResponse = "You are not signed up";
        authResult.setStatus(expectedResponse);
        when(authService.signUp(Mockito.any())).thenReturn(authResult);

        client.sendRequest();
        String status = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("status");

        assertEquals(expectedResponse, status);
    }

    @Test
    public void successfulReset() throws InterruptedException {
        this.client = new WebSocketClient(responses, SUCCESSFUL_RESET);

        String expectedResponse = "Please check your email";
        authResult.setStatus(expectedResponse);
        when(authService.resetPassword(Mockito.any())).thenReturn(authResult);

        client.sendRequest();
        String status = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("status");

        assertEquals(expectedResponse, status);
    }

    @Test
    public void unsuccessfulReset() throws InterruptedException {
        this.client = new WebSocketClient(responses, UNSUCCESSFUL_RESET);

        String expectedResponse = "You are not registered";
        authResult.setStatus(expectedResponse);
        when(authService.resetPassword(Mockito.any())).thenReturn(authResult);

        client.sendRequest();
        String status = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("status");

        assertEquals(expectedResponse, status);
    }

    @Test
    public void successfulUpdatePassword() throws InterruptedException {
        this.client = new WebSocketClient(responses, SUCCESSFUL_UPDATE_PASSWORD);

        String expectedResponse = "Your password successfully changed";
        authResult.setStatus(expectedResponse);
        when(authService.updatePassword(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(authResult);

        client.sendRequest();
        String status = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("status");

        assertEquals(expectedResponse, status);
    }

    @Test
    public void successfulConfirmEmailRegistration() throws InterruptedException {
        this.client = new WebSocketClient(responses, SUCCESSFUL_CONFIRM_EMAIL_SIGN_UP);

        String expectedResponse = "Your email successfully confirmed";
        authResult.setStatus(expectedResponse);
        when(authService.checkCodeForSignUp(Mockito.any(), Mockito.any())).thenReturn(authResult);

        client.sendRequest();
        String status = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("status");

        assertEquals(expectedResponse, status);
    }

    @Test
    public void successfulConfirmEmailReset() throws InterruptedException {
        this.client = new WebSocketClient(responses, SUCCESSFUL_CONFIRM_EMAIL_RESET);

        String expectedResponse = "Your email successfully confirmed";
        authResult.setStatus(expectedResponse);
        when(authService.checkCodeForReset(Mockito.any(), Mockito.any())).thenReturn(authResult);

        client.sendRequest();
        String status = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("status");

        assertEquals(expectedResponse, status);
    }

    @Test
    public void successfulSignInWithCookie() throws InterruptedException {
        String firstExpectedResponse = "You are signed in";
        this.client = new WebSocketClient(responses, SUCCESSFUL_SIGN_IN);

        authResult.setCode(AuthResult.Codes.SUCCESSFUL_LOGIN);
        authResult.setLogin("Max");
        when(authService.signIn(Mockito.any())).thenReturn(authResult);

        client.sendRequest();
        String cookieSignIn = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("cookie");

        WebSocketClient reloadClient = new WebSocketClient(responses, "{" +
                "\"operation\":" + "\"cookie\"," +
                "\"cookie\":" + "\"" + cookieSignIn + "\"" +
                "}");
        reloadClient.sendRequest();
        String response = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("status");

        assertEquals(firstExpectedResponse, response);
    }

    @Test
    public void unsuccessfulSignInWithCookie() throws InterruptedException {
        this.client = new WebSocketClient(responses, UNSUCCESSFUL_SIGN_IN_WITH_COOKIE);
        String expectedResponse = "You are not signed in";

        client.sendRequest();
        String status = (String) new Gson().fromJson(responses.getLast(), HashMap.class).get("status");

        assertEquals(expectedResponse, status);
    }

    @After
    public void shutdownServer() {
        serverThread.interrupt();
    }
}
