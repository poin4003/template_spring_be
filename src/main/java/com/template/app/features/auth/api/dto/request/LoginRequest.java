package com.template.app.features.auth.api.dto.request;

import lombok.Data;

@Data
public class LoginRequest {

    private String userEmail; 

    private String userPassword;
}
