package com.app.features.auth.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.core.security.UserPrincipal;
import com.app.features.rbac.entity.RoleEntity;
import com.app.features.rbac.repository.RoleRepository;
import com.app.features.user.entity.UserBaseEntity;
import com.app.features.user.repository.UserBaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserBaseRepository userBaseRepo; 
    private final RoleRepository roleRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Get user info
        UserBaseEntity user = userBaseRepo.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Not found" + username));

        List<RoleEntity> roles = roleRepo.findByUserId(user.getId()); 
        user.setRoles(roles);

        return modelMapper.map(user, UserPrincipal.class);
    }
}
