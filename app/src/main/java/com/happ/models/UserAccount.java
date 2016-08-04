package com.happ.models;

import io.realm.RealmList;

/**
 * Created by dante on 8/1/16.
 */
public class UserAccount {

    private RealmList<User> users;

    public RealmList<User> getUsers() {
        return users;
    }

    public void setUsers(RealmList<User> users) {
        this.users = users;
    }
}
