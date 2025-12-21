package com.template.app.core.dto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class PaginationResponse<T> {
    private List<T> items;
    private PaginationMeta metaData;

    public static <T> PaginationResponse<T> of(List<T> items, long totalItems, long currentPage, long pageSize) {
        PaginationResponse<T> response = new PaginationResponse<>();

        PaginationMeta meta = new PaginationMeta(
            totalItems,
            currentPage,
            pageSize,
            (long) Math.ceil((double) totalItems / pageSize)
        );

        response.setItems(items);
        response.setMetaData(meta);
        return response;
    }

    public <R> PaginationResponse<R> map(Function<T, R> converter) {
        List<R> newItems = this.items.stream()
                                    .map(converter)
                                    .collect(Collectors.toList());

        PaginationResponse<R> newResponse = new PaginationResponse<>();
        newResponse.setItems(newItems);
        newResponse.setMetaData(this.metaData);
        
        return newResponse;
    }
}
