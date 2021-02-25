package com.netcracker.edu.auth.api;

public class AuthEntry{
    private String userLogin;
    private String userPassword;
    private String userEmail;

    public AuthEntry() {
    }

    public AuthEntry(String userLogin, String userPassword, String userEmail) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public static class Builder {
        private final AuthEntry authEntry;

        public Builder() {
            this.authEntry = new AuthEntry();
        }

        public AuthEntry.Builder setUserLogin(String userLogin){
            authEntry.userLogin = userLogin;
            return this;
        }

        public AuthEntry.Builder setUserPassword(String userPassword){
            authEntry.userPassword = userPassword;
            return this;
        }

        public AuthEntry.Builder setUserEmail(String userEmail){
            authEntry.userEmail = userEmail;
            return this;
        }

        public AuthEntry build(){
            return authEntry;
        }
    }
}
