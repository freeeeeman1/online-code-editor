package com.netcracker.edu.auth.session;

import io.netty.handler.codec.http.cookie.DefaultCookie;

import java.security.SecureRandom;
import java.util.Base64;

public class Cookie {
    private String cookieId;
    private String username;

    public Cookie() {
    }

    public String getUsername() {
        return username;
    }

    public String getCookie() {
        return cookieId;
    }

    public static class Builder {

        private final Cookie cookie;

        public Builder() {
            this.cookie = new Cookie();
        }

        public Cookie.Builder setUsername(String username) {
            cookie.username = username;
            return this;
        }

        private String generateValue() {
            SecureRandom secureRandom = new SecureRandom(); //threadsafe

            byte[] randomBytes = new byte[24];
            secureRandom.nextBytes(randomBytes);
            return Base64.getEncoder().encodeToString(randomBytes);
        }

        public Cookie build() {
            DefaultCookie defaultCookie = new DefaultCookie(cookie.username, generateValue());
            defaultCookie.setMaxAge(24 * 60 * 60);
            defaultCookie.setPath("/");
            cookie.cookieId = defaultCookie.toString();
            return cookie;
        }
    }
}
