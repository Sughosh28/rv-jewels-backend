package com.rv.dto;

import java.io.Serializable;

public class PhoneNumberUpdateRequestDTO implements Serializable {
    String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
