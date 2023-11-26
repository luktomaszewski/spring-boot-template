package com.github.lomasz.spring.boot.template.controller;

import com.github.lomasz.spring.boot.template.model.dto.ErrorDto;
import com.github.lomasz.spring.boot.template.model.dto.NewTemplateDto;
import com.github.lomasz.spring.boot.template.model.dto.SearchResult;
import com.github.lomasz.spring.boot.template.model.dto.TemplateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

public interface TemplateApiDoc {

    @Operation(summary = "Search", description = "Search based on given criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    ResponseEntity<SearchResult<TemplateDto>> search(
            @Parameter(description = "Page number of the search results") int page,
            @Parameter(description = "Number of items per page") int size,
            @Parameter(description = "Direction of sorting") Sort.Direction direction,
            @Parameter(description = "Property to sort by") String sortProperty);

    @Operation(summary = "Add", description = "Add new object")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created. 'Location' header contains URL of the new resource."),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    ResponseEntity<Void> add(NewTemplateDto newDto);

    @Operation(summary = "Get by ID", description = "Retrieve a specific object by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TemplateDto.class))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    ResponseEntity<TemplateDto> getById(@Parameter(description = "ID of the object to retrieve") Long id);
}
