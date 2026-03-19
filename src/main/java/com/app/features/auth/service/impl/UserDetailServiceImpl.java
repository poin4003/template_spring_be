package com.app.features.auth.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Get user info
        UserBaseEntity user = userBaseRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found" + username));

        // 2. Get roles
        List<RoleEntity> roles = roleRepo.findByUserId(user.getId());

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getKey()))
                .collect(Collectors.toList());

        return UserPrincipal.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .status(user.getStatus())
                .delFlag(user.getDelFlag())
                .authorities(authorities)
                .build();
    }
}
