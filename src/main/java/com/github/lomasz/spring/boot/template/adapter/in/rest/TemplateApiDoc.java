package com.github.lomasz.spring.boot.template.adapter.in.rest;

import com.github.lomasz.spring.boot.template.application.domain.model.SearchResult;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

interface TemplateApiDoc {

    @Operation(summary = "Search", description = "Search based on given criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL to retrieve object in 'Location' header"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    ResponseEntity<SearchResult<TemplateResponse>> search(
            @Parameter(description = "Page number of the search results") int page,
            @Parameter(description = "Number of items per page") int size,
            @Parameter(description = "Direction of sorting") SortDirection sortDirection,
            @Parameter(description = "Property to sort by") String sortProperty);

    @Operation(summary = "Add", description = "Add new object")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created. 'Location' header contains URL of the new resource."),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    ResponseEntity<Void> add(CreateTemplateRequest request);

    @Operation(summary = "Get by ID", description = "Retrieve a specific object by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Template.class))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    ResponseEntity<TemplateResponse> getById(@Parameter(description = "ID of the object to retrieve") Long id);
}
