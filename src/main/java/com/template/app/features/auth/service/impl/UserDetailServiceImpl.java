package com.template.app.features.auth.service.impl;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.template.app.features.auth.service.schema.AuthCoreMapStruct;
import com.template.app.features.rbac.entity.RoleEntity;
import com.template.app.features.rbac.repository.RoleRepository;
import com.template.app.features.user.entity.UserBaseEntity;
import com.template.app.features.user.repository.UserBaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserBaseRepository userBaseRepo; 
    private final RoleRepository roleRepo;
    private final AuthCoreMapStruct authCoreMapStruct;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Get user info
        LambdaQueryWrapper<UserBaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserBaseEntity::getUserEmail, username);        

        UserBaseEntity user = userBaseRepo.selectOne(queryWrapper);
        if (user == null) {
            throw new UsernameNotFoundException("Not found" + username);
        }

        List<RoleEntity> roles = roleRepo.selectRolesByUserId(user.getUserId());
       
        user.setRoles(roles);

        return authCoreMapStruct.toUserPrincipal(user);
    }
}
