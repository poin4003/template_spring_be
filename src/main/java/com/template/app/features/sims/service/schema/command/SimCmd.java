package com.template.app.features.sims.service.schema.command;

import com.template.app.features.sims.enums.SimStatusEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimCmd {
    private String simPhoneNumber;

    private Integer simImportPrice;

    private Integer simSellingPrice;

    private Integer simDealerPrice;

    private SimStatusEnum simStatus; 
}
