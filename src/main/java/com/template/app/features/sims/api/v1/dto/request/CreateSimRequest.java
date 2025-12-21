package com.template.app.features.sims.api.v1.dto.request;

import com.template.app.features.sims.enums.SimStatusEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSimRequest {
    
    @NotBlank(message = "Phone number must not be blank")
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
    private String simPhoneNumber;

    @NotNull
    private SimStatusEnum simStatus;

    private Integer simSellingPrice;
    private Integer simDealerPrice;
    private Integer simImportPrice;
}
