package com.app.features.ops.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.features.ops.entity.CronJobConfigEntity;
import com.app.features.ops.enums.OpsStatusEnum;

@Repository
public interface CronJobConfigRepository extends JpaRepository<CronJobConfigEntity, UUID> {

    @Query("SELECT c FROM CronJobConfigEntity c JOIN c.opsConfigEntity o WHERE o.status = :status")
    List<CronJobConfigEntity> findAllActiveJobs(@Param("status") OpsStatusEnum status);
}
