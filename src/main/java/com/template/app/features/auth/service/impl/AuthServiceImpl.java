package com.template.app.features.auth.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.template.app.config.jwt.JwtTokenProvider;
import com.template.app.config.jwt.dto.JwtPayload;
import com.template.app.config.security.crypto.dto.KeyPairDto;
import com.template.app.features.auth.entity.KeyStoreEntity;
import com.template.app.features.auth.entity.ConsumedRefreshTokenEntity;
import com.template.app.features.auth.repository.ConsumedRefreshTokenRepository;
import com.template.app.features.auth.repository.KeyStoreRepository;
import com.template.app.features.auth.service.AuthService;
import com.template.app.features.auth.service.KeyStoreService;
import com.template.app.features.auth.service.schema.AuthCoreMapStruct;
import com.template.app.features.auth.service.schema.command.LoginCmd;
import com.template.app.features.auth.service.schema.result.LoginResult;
import com.template.app.features.user.entity.UserBaseEntity;
import com.template.app.features.user.repository.UserBaseRepository;
import com.template.app.core.exception.ExceptionFactory;

import com.template.app.features.auth.service.schema.result.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserBaseRepository userBaseRepository;
    private final KeyStoreRepository keyStoreRepository;
    private final KeyStoreService keyStoreService;
    private final ConsumedRefreshTokenRepository consumedRefreshTokenRepository;
    private final AuthCoreMapStruct authCoreMapStruct;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResult login(LoginCmd cmd) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(cmd.getUserEmail(), cmd.getUserPassword())
        );

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        String userEmail = userDetails.getUserEmail();

        updateUserLoginInfo(userId, cmd.getIpAddress());
        
        return generateAndSaveTokens(userId, userEmail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResult refreshToken(String refreshToken) {
        ConsumedRefreshTokenEntity usedToken = consumedRefreshTokenRepository.selectOne(
            new LambdaQueryWrapper<ConsumedRefreshTokenEntity>()
                .eq(ConsumedRefreshTokenEntity::getTokenValue, refreshToken)
        );

        if (usedToken != null) {
            log.warn("Refresh Token reuse detected! userId: {}", usedToken.getUserId());

            keyStoreService.deleteKeyStoreByUserId(usedToken.getUserId());

            throw ExceptionFactory.permissionError("Something wrong happened! Please relogin.");
        }

        UUID userId = jwtTokenProvider.getUserIdFromTokenUnverified(refreshToken);
        if (userId == null) throw ExceptionFactory.dataNotFound("userId " + userId + " not found");

        KeyStoreEntity keyStore = keyStoreRepository.selectOne(
            new LambdaQueryWrapper<KeyStoreEntity>().eq(KeyStoreEntity::getUserId, userId)
        );

        if (keyStore == null) throw ExceptionFactory.dataNotFound("User keystore not found. Please login again.");

        if (!refreshToken.equals(keyStore.getRefreshToken())) {
            throw ExceptionFactory.permissionError("Invalid refresh token, please relogin!");
        }

        if (!jwtTokenProvider.validateToken(refreshToken, keyStore.getPublicKey())) {
            throw ExceptionFactory.permissionError("RefreshToken Expired or Invalid Signature");
        }

        ConsumedRefreshTokenEntity history = new ConsumedRefreshTokenEntity();
        history.setTokenHistoryId(UUID.randomUUID());
        history.setKeyStoreId(keyStore.getKeyStoreId());
        history.setUserId(userId);
        history.setTokenValue(refreshToken);
        history.setUsedAt(Instant.now());

        Date expiryDate = jwtTokenProvider.getExpiryDateFromToken(refreshToken, keyStore.getPublicKey());
        history.setExpiryDate(expiryDate.toInstant()); 

        consumedRefreshTokenRepository.insert(history);

        UserBaseEntity user = userBaseRepository.selectById(userId);

        return generateAndSaveTokens(userId, user.getUserEmail());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(UUID keyStoreId, UUID userId) {
        updateUserLogoutInfo(userId);

        keyStoreRepository.deleteById(keyStoreId);
    }

    private LoginResult generateAndSaveTokens(UUID userId, String userEmail) {
        KeyPairDto newKeyPairDto = jwtTokenProvider.generateKeyPair();
        String newPrivateKey = newKeyPairDto.getPrivateKey();
        String newPublicKey = newKeyPairDto.getPublicKey();

        JwtPayload payload = JwtPayload.builder()
            .userEmail(userEmail)
            .build();

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, payload, newPrivateKey);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, newPrivateKey);

        saveKeyStore(userId, newPublicKey, newPrivateKey, newRefreshToken);

        return authCoreMapStruct.toLoginResult(newAccessToken, newRefreshToken, userId, userEmail);
    }

    private void saveKeyStore(UUID userId, String publicKey, String privateKey, String refreshToken) {
        KeyStoreEntity keyStore = new KeyStoreEntity();
        
        keyStore.setKeyStoreId(UUID.randomUUID());
        keyStore.setUserId(userId);
        keyStore.setPublicKey(publicKey);
        keyStore.setPrivateKey(privateKey);
        keyStore.setRefreshToken(refreshToken);

        keyStoreRepository.upsert(keyStore); 
    }

    private void updateUserLoginInfo(UUID userId, String ipAddress) {
        UserBaseEntity userUpdate = new UserBaseEntity();
        userUpdate.setUserId(userId);
        userUpdate.setUserLoginTime(LocalDateTime.now());
        userUpdate.setUserLoginIp(ipAddress);

        userBaseRepository.updateById(userUpdate);
    }

    private void updateUserLogoutInfo(UUID userId) {
        UserBaseEntity userUpdate = new UserBaseEntity();
        userUpdate.setUserId(userId);
        userUpdate.setUserLogoutTime(LocalDateTime.now());

        userBaseRepository.updateById(userUpdate);       
    }
}
