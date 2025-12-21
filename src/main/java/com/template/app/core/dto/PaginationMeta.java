package com.template.app.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationMeta {
    private long totalItems;
    private long currentPage;
    private long pageSize;
    private long totalPages;
}
