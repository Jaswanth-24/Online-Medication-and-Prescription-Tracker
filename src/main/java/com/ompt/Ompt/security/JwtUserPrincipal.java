package com.ompt.Ompt.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtUserPrincipal {

    private final Long userId;
    private final String email;
    private final String role;
}
