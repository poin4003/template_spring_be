package com.template.app.features.sims.api.v1.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.template.app.features.sims.enums.SimStatusEnum;

import lombok.Data;

@Data
public class SimResponse {
    private UUID simId;

    private String simPhoneNumber;
    private SimStatusEnum simStatus;
    private Integer simSellingPrice;
    private Integer simDealerPrice;
    private Integer simImportPrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
