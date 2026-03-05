package com.app.features.auth.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.features.auth.entity.KeyStoreEntity;

@Repository
public interface KeyStoreRepository extends JpaRepository<KeyStoreEntity, UUID> {
    void upsert(KeyStoreEntity entity);
}
