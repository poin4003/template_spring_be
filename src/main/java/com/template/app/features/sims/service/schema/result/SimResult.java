package com.template.app.features.sims.service.schema.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.template.app.features.sims.enums.SimStatusEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimResult {
    private UUID simId;

    private String simPhoneNumber;
    private SimStatusEnum simStatus;
    private Integer simSellingPrice;
    private Integer simDealerPrice;
    private Integer simImportPrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; 
}
