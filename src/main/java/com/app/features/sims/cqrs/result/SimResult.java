package com.app.features.sims.cqrs.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.app.features.sims.enums.SimStatusEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimResult {
    private UUID id;

    private String phoneNumber;
    private SimStatusEnum status;
    private Integer sellingPrice;
    private Integer sealerPrice;
    private Integer importPrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String note;
    private String description;
}
