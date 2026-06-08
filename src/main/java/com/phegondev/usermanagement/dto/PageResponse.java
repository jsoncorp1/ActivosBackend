package com.phegondev.usermanagement.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private Integer totalElements;
    private Integer totalPages;
    private Integer currentPage;
    private Boolean hasNext;
    private Boolean hasPrevious;
}
