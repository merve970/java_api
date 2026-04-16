package com.deneme.java_api.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String surname;
    private String password;
    private String role;
}