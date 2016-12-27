package com.happ.models;

/**
 * Created by dante on 12/27/16.
 */
public class PasswordResetResponse {

    private String uidb64;
    private String token;
    private String new_password;

    public String getUidb64() {
        return uidb64;
    }

    public void setUidb64(String uidb64) {
        this.uidb64 = uidb64;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
