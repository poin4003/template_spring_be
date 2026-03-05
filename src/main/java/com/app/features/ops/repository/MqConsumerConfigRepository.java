package com.app.features.ops.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.features.ops.entity.MqConsumerConfigEntity;

@Repository
public interface MqConsumerConfigRepository extends JpaRepository<MqConsumerConfigEntity, UUID> {

}
