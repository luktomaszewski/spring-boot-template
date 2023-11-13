package com.lomasz.spring.boot.template.controller;

import com.lomasz.spring.boot.template.model.dto.ErrorDto;
import com.lomasz.spring.boot.template.model.dto.NewTemplateDto;
import com.lomasz.spring.boot.template.model.dto.SearchResult;
import com.lomasz.spring.boot.template.model.dto.TemplateDto;
import com.lomasz.spring.boot.template.service.TemplateService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            })
    public ResponseEntity<SearchResult<TemplateDto>> search(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size,
            @RequestParam(name = "order", required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sortProperty) {
        return ResponseEntity.ok(templateService.search(page, size, direction, sortProperty));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Created. 'Location' header contains URL of the new resource."),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            })
    public ResponseEntity<Void> add(@RequestBody @Valid NewTemplateDto newDto) {
        Long id = templateService.create(newDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/api/templates/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TemplateDto.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "404"),
                    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            })
    public ResponseEntity<TemplateDto> getById(@PathVariable("id") Long id) {
        return templateService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
