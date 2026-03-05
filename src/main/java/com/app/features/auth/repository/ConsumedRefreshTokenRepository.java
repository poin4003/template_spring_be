package com.app.features.auth.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.features.auth.entity.ConsumedRefreshTokenEntity;

@Repository
public interface ConsumedRefreshTokenRepository extends JpaRepository<ConsumedRefreshTokenEntity, UUID> {
    boolean existsByTokenValue(String tokenValue);
}
