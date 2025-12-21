package com.template.app.features.auth.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.template.app.features.auth.entity.KeyStoreEntity;
import com.template.app.features.auth.repository.KeyStoreRepository;
import com.template.app.features.auth.service.KeyStoreService;
import com.template.app.features.auth.service.schema.AuthCoreMapStruct;
import com.template.app.features.auth.service.schema.result.KeyStoreResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeyStoreServiceImpl implements KeyStoreService {

    private final KeyStoreRepository keyStoreRepository;
    private final AuthCoreMapStruct authCoreMapStruct;

    @Override
    public KeyStoreResult getKeyStoreByUserId(UUID userId) {
        KeyStoreEntity keyStore = keyStoreRepository.selectOne(
            new LambdaQueryWrapper<KeyStoreEntity>()
                .eq(KeyStoreEntity::getUserId, userId)
        );

        return authCoreMapStruct.toKeyStoreResult(keyStore);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteKeyStoreByUserId(UUID userId) {
        keyStoreRepository.delete(
            new LambdaQueryWrapper<KeyStoreEntity>().eq(KeyStoreEntity::getUserId, userId)
        );
    } 
}
