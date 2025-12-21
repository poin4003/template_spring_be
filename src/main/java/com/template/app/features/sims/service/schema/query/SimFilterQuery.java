package com.template.app.features.sims.service.schema.query;

import com.template.app.base.BaseQuery;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class SimFilterQuery extends BaseQuery {
    private String phoneNumber;
    
    private Integer status;

    private Long fromDate;
    
    private Long toDate;
}
