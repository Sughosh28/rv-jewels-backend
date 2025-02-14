package com.rv.model.abstracts;


import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseAddress {
    @Id
    @GeneratedValue
    private UUID id;

    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String landmark;
    private String phoneNumber;
    private String alternatePhoneNumber;
}
