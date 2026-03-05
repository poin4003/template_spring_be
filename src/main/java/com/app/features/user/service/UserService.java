package com.app.features.user.service;

import com.app.core.dto.PaginationResponse;
import com.app.features.user.service.schema.command.UserCreationCmd;
import com.app.features.user.service.schema.query.UserQuery;
import com.app.features.user.service.schema.result.UserResult;

public interface UserService {

    void checkEmailUnique(String email);

    UserResult createUser(UserCreationCmd cmd);

    PaginationResponse<UserResult> getManyUsers(UserQuery queryInput);
}
