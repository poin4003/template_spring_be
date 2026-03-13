package com.app.features.ops.service.schema;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpsInitSummary {
    
    private int totalActive;

    private int successCount;

    private int failedCount;

    private List<String> failedIds;

}
