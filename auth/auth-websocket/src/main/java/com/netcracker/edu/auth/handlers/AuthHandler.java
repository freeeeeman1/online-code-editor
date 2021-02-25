package com.netcracker.edu.auth.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.netcracker.edu.auth.api.AuthEntry;
import com.netcracker.edu.auth.api.AuthResult;
import com.netcracker.edu.auth.service.AuthServiceImpl;
import com.netcracker.edu.auth.session.SessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AuthHandler extends ChannelInboundHandlerAdapter {

    public final static String OPERATION = "operation";
    public final static String SIGN_IN = "login";
    public final static String SIGN_UP = "registration";
    public final static String RESET_PASSWORD = "reset-password";
    public final static String UPDATE_PASSWORD = "update-password";
    public final static String CONFIRM_EMAIL_REGISTRATION = "confirm-email-registration";
    public final static String CONFIRM_EMAIL_RESET = "confirm-email-reset";
    public final static String SIGN_IN_WITH_COOKIE = "cookie";
    public final static String LOGOUT = "logout";

    public final static String LOGIN = "login";
    public final static String PASSWORD = "password";
    public final static String EMAIL = "email";
    public final static String CODE = "code";
    public final static String COOKIE = "cookie";

    private final Logger logger = Logger.getLogger(AuthHandler.class);
    private final SessionManager sessionManager;
    private final AuthServiceImpl authService;

    public AuthHandler(SessionManager sessionManager, AuthServiceImpl authService) {
        this.sessionManager = sessionManager;
        this.authService = authService;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        if (!(message instanceof CloseWebSocketFrame)) {

            Map<String, String> request = parseRequest(message);
            AuthResult result = processRequest(request);

            context.writeAndFlush(new TextWebSocketFrame(createJsonResponse(result)));

            if (result.getCode() == AuthResult.Codes.SUCCESSFUL_LOGIN
                    || result.getCode() == AuthResult.Codes.SUCCESSFUL_LOGIN_WITH_COOKIE) {
                context.pipeline().remove(this);
            }
        } else {
            context.close();
        }
    }

    private Map<String, String> parseRequest(Object message) {
        ByteBuf buf = ((WebSocketFrame) message).content();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.duplicate().readBytes(bytes);

        return new Gson().fromJson(new String(bytes), HashMap.class);
    }

    private AuthResult processRequest(Map<String, String> request) {

        String operation = request.get(OPERATION);
        switch (operation) {
            case SIGN_IN:
                return signIn(request);
            case SIGN_IN_WITH_COOKIE:
                return signInWithCookie(request);
            case LOGOUT:
                return logout(request);
            case SIGN_UP:
                return signUp(request);
            case CONFIRM_EMAIL_REGISTRATION:
                return authService.checkCodeForSignUp(request.get(LOGIN), request.get(CODE));
            case CONFIRM_EMAIL_RESET:
                return authService.checkCodeForReset(request.get(EMAIL), request.get(CODE));
            case RESET_PASSWORD:
                return authService.resetPassword(request.get(EMAIL));
            case UPDATE_PASSWORD:
                return authService.updatePassword(request.get(EMAIL), request.get(PASSWORD), request.get(CODE));
            default:
                logger.error("Unknown Operation {" + operation + "} sent by {" + request.get(LOGIN) + "}");

                AuthResult authResult = new AuthResult();
                authResult.setStatus("Unknown Operation");
                return authResult;
        }
    }

    private AuthResult signIn(Map<String, String> request) {
        AuthEntry authEntry = new AuthEntry.Builder()
                .setUserLogin(request.get(LOGIN))
                .setUserPassword(request.get(PASSWORD))
                .build();
        return authService.signIn(authEntry);
    }

    private AuthResult signInWithCookie(Map<String, String> request) {
        AuthResult authResult = new AuthResult();
        if (sessionManager.isSessionExist(request.get(COOKIE))) {
            String username = sessionManager.getSession(request.get(COOKIE)).getUsername();
            authResult.setLogin(username);
            authResult.setCode(AuthResult.Codes.SUCCESSFUL_LOGIN_WITH_COOKIE);
            authResult.setStatus("You are signed in");
        } else {
            authResult.setStatus("You are not signed in");
            authResult.setCode(AuthResult.Codes.WRONG_SIGN_IN);
        }
        return authResult;
    }

    private AuthResult logout(Map<String, String> request) {
        sessionManager.removeSession(request.get(COOKIE));
        AuthResult authResult = new AuthResult();
        authResult.setStatus("Removed");
        return authResult;
    }

    private AuthResult signUp(Map<String, String> request) {
        AuthEntry authEntry = new AuthEntry.Builder()
                .setUserEmail(request.get(EMAIL))
                .setUserLogin(request.get(LOGIN))
                .setUserPassword(request.get(PASSWORD))
                .build();
        return authService.signUp(authEntry);
    }

    private String createJsonResponse(AuthResult result) {
        JsonElement response = new Gson().toJsonTree(result);

        if (result.getCode() == AuthResult.Codes.SUCCESSFUL_LOGIN) {
            String cookie = createCookie(result);
            response.getAsJsonObject().addProperty(COOKIE, cookie);
        }
        return response.toString();
    }

    private String createCookie(AuthResult authResult) {
        return sessionManager.createSession(authResult.getLogin());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause);
        ctx.close();
    }
}
