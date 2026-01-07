package com.template.app.features.user.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.template.app.core.dto.PaginationResponse;
import com.template.app.core.exception.ExceptionFactory;
import com.template.app.features.user.entity.UserBaseEntity;
import com.template.app.features.user.enums.UserStatusEnum;
import com.template.app.features.user.repository.UserBaseRepository;
import com.template.app.features.user.service.UserService;
import com.template.app.features.user.service.schema.UserCoreMapStruct;
import com.template.app.features.user.service.schema.command.UserCreationCmd;
import com.template.app.features.user.service.schema.query.UserQuery;
import com.template.app.features.user.service.schema.result.UserResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserBaseRepository userBaseRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCoreMapStruct userCoreMapStruct;

    @Override
    public void checkEmailUnique(String email) {
        LambdaQueryWrapper<UserBaseEntity> checkEmail = new LambdaQueryWrapper<>();
        checkEmail.eq(UserBaseEntity::getUserEmail, email);
        if (userBaseRepository.exists(checkEmail)) {
            throw ExceptionFactory.dataAlreadyExists("Email " + email);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResult createUser(UserCreationCmd cmd) {
        checkEmailUnique(cmd.getEmail()); 
        
        UUID userId = UUID.randomUUID();
        
        UserBaseEntity userBase = new UserBaseEntity();
        userBase.setUserId(userId);
        userBase.setUserEmail(cmd.getEmail());
        userBase.setUserPassword(passwordEncoder.encode(cmd.getPassword()));
        userBase.setUserStatus(UserStatusEnum.ACTIVE);
        userBase.setDelFlag("0");

        userBaseRepository.insert(userBase);

        return userCoreMapStruct.toUserResult(userBase);
    }

    @Override 
    public PaginationResponse<UserResult> getManyUsers(UserQuery queryInput) {
        IPage<UserBaseEntity> pageObject = new Page<>(queryInput.getCurrentPage(), queryInput.getPageSize());

        IPage<UserBaseEntity> entityPage = userBaseRepository.selectPage(pageObject, null);

        List<UserResult> responseList = entityPage.getRecords().stream()
                    .map(userCoreMapStruct::toUserResult)
                    .collect(Collectors.toList());
        
        return PaginationResponse.of(
            responseList,
            entityPage.getTotal(),
            entityPage.getCurrent(),
            entityPage.getSize()
        );
    }
}
