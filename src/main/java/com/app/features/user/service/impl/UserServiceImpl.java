package com.app.features.user.service.impl;

import org.springframework.stereotype.Service;

import com.app.core.exception.ExceptionFactory;
import com.app.features.user.repository.UserBaseRepository;
import com.app.features.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserBaseRepository userBaseRepo;

    @Override
    public void checkEmailUnique(String email) {
        if (!userBaseRepo.existsByEmail(email)) {
            throw ExceptionFactory.alreadyExists("Email " + email);
        }
    }
}
