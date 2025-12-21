package com.template.app.features.auth.service.schema.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginCmd {

    private String userEmail;

    private String userPassword;

    private String ipAddress;
}
