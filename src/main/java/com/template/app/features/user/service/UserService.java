package com.template.app.features.user.service;

import com.template.app.core.dto.PaginationResponse;
import com.template.app.features.user.service.schema.command.UserCreationCmd;
import com.template.app.features.user.service.schema.query.UserQuery;
import com.template.app.features.user.service.schema.result.UserResult;

public interface UserService {

    void checkEmailUnique(String email);

    UserResult createUser(UserCreationCmd cmd);

    PaginationResponse<UserResult> getManyUsers(UserQuery queryInput);
}
