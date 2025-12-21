package com.template.app.features.ops.service.schema.result;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpsInitResult {
    
    private int totalActive;

    private int successCount;

    private int failedCount;

    private List<String> failedIds;

}
