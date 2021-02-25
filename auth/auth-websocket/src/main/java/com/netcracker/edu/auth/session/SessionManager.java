package com.netcracker.edu.auth.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    Map<String, Session> sessionMap;

    public SessionManager() {
        this.sessionMap = new HashMap<>();
    }

    public String createSession(String username) {
        String cookie = new Cookie
                .Builder()
                .setUsername(username)
                .build()
                .getCookie();

        Session session = new Session(username);
        sessionMap.put(cookie, session);

        return cookie;
    }

    public Session getSession(String cookie) {
        return sessionMap.get(cookie);
    }

    public boolean isSessionExist(String cookieId) {
        return sessionMap.containsKey(cookieId);
    }

    public void removeSession(String cookieId) {
        sessionMap.remove(cookieId);
    }
}
