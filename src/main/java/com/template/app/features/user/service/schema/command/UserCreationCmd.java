package com.template.app.features.user.service.schema.command;

import lombok.Data;

@Data
public class UserCreationCmd {

    private String email;

    private String password;
}
