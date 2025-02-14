package com.rv.model;

import com.rv.model.abstracts.BaseAddress;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Addresses extends BaseAddress {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private Boolean isDefault;
    @Column(name = "address_count")
    private int addressCount = 0;

    @PrePersist
    @PreUpdate
    private void validateAddressCount() {
        if (addressCount >= 3) {
            throw new IllegalStateException("Maximum limit of 3 addresses reached");
        }
    }
}
