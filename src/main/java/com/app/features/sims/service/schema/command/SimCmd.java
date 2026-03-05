package com.app.features.sims.service.schema.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.app.core.annotation.RegisterMqType;
import com.app.features.sims.enums.SimStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterMqType("SimCmd")
public class SimCmd {
    
    private String simPhoneNumber;

    private Integer simImportPrice;

    private Integer simSellingPrice;

    private Integer simDealerPrice;

    private SimStatusEnum simStatus; 
}
