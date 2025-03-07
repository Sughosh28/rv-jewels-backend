package com.rv.dto;

import java.io.Serializable;

public record AuthTokenDTO (
    String accessToken,
    String refreshToken,
    String role
) implements Serializable {

}
