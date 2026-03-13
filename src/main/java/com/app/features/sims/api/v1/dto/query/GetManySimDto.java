package com.app.features.sims.api.v1.dto.query;

import com.app.base.BaseQuery;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetManySimDto extends BaseQuery {
    
    private SimFilterDto filter;    
}
