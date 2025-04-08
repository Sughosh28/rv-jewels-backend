package com.rv.dto;

import java.io.Serializable;

public class UserProfileDTO implements Serializable {
    String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
