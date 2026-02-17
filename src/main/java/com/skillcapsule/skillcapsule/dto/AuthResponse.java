package com.skillcapsule.skillcapsule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String id;     // ⬅️ CHANGE Long → String
    private String name;
    private String email;
}
