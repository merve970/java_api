package com.deneme.java_api.dto;

import lombok.Data;

@Data // Getter ve Setter'ları otomatik oluşturur
public class RoleUpdateRequest {
    private String role;
}