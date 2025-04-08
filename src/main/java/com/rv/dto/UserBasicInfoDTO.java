package com.rv.dto;

import java.io.Serializable;

public class UserBasicInfoDTO implements Serializable {

    private String name;
    private String email;
    private Long id;

    public UserBasicInfoDTO(String name, String email, Long id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
