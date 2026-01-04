package org.gso.citations.dto;

import java.net.URI;
import java.util.List;

/**
 * DTO to expose paginable results
 */
public record PageDto<T> (
    int pageSize,
    long totalElements,
    URI next,
    URI first,
    URI last,
    List<T> data) {
}
