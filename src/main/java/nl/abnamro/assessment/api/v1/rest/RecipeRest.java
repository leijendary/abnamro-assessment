package nl.abnamro.assessment.api.v1.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.abnamro.assessment.api.v1.data.RecipeFilterRequest;
import nl.abnamro.assessment.api.v1.data.RecipeRequest;
import nl.abnamro.assessment.api.v1.data.RecipeResponse;
import nl.abnamro.assessment.api.v1.service.RecipeService;
import nl.abnamro.assessment.core.controller.SecuredController;
import nl.abnamro.assessment.core.data.DataResponse;
import nl.abnamro.assessment.core.data.QueryRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recipes")
@Tag(
        name = "Recipe",
        description = "This REST API will allow to to list, create, get, update, and delete specific recipes."
)
public class RecipeRest extends SecuredController {
    private final RecipeService recipeService;

    public RecipeRest(final RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_urn:recipe:page:v1')")
    @Operation(
            summary = "Get the list of recipes available according to the query and filter.",
            description = "Requires the scope of 'urn:recipe:page:v1'"
    )
    public DataResponse<List<RecipeResponse>> page(
            final QueryRequest query,
            @Valid final RecipeFilterRequest filter,
            final Pageable pageable
    ) {
        final var page = recipeService.page(query, filter, pageable);

        return DataResponse.<List<RecipeResponse>>builder()
                .data(page.getContent())
                .meta(page)
                .links(page)
                .build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_urn:recipe:create:v1')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a recipe and return the created object.",
            description = "Requires the scope of 'urn:recipe:create:v1'"
    )
    public DataResponse<RecipeResponse> create(@Valid @RequestBody final RecipeRequest request) {
        final var response = recipeService.create(request);

        return DataResponse.<RecipeResponse>builder()
                .data(response)
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:recipe:get:v1')")
    @Operation(
            summary = "Get a recipe based on the ID",
            description = "Requires the scope of 'urn:recipe:get:v1'"
    )
    public DataResponse<RecipeResponse> get(@PathVariable final UUID id) {
        final var response = recipeService.get(id);

        return DataResponse.<RecipeResponse>builder()
                .data(response)
                .build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:recipe:update:v1')")
    @Operation(
            summary = "Update the recipe's details based on the ID passed and the new details.",
            description = "Requires the scope of 'urn:recipe:update:v1'"
    )
    public DataResponse<RecipeResponse> update(
            @PathVariable final UUID id,
            @Valid @RequestBody final RecipeRequest request
    ) {
        final var response = recipeService.update(id, request);

        return DataResponse.<RecipeResponse>builder()
                .data(response)
                .build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:recipe:delete:v1')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete the recipe based on the ID. WARNING: This action is irreversible!",
            description = "Requires the scope of 'urn:recipe:delete:v1'"
    )
    public void delete(@PathVariable UUID id) {
        recipeService.delete(id);
    }
}
