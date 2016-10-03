package com.happ.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dante on 10/3/16.
 */
public class ChangePwData {
    @SerializedName("old_password")
    private String oldPassword;

    @SerializedName("new_password")
    private String newPssword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPssword() {
        return newPssword;
    }

    public void setNewPssword(String newPssword) {
        this.newPssword = newPssword;
    }
}
