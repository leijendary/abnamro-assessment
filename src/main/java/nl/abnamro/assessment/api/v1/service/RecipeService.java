package nl.abnamro.assessment.api.v1.service;

import nl.abnamro.assessment.api.v1.data.RecipeFilterRequest;
import nl.abnamro.assessment.api.v1.data.RecipeRequest;
import nl.abnamro.assessment.api.v1.data.RecipeResponse;
import nl.abnamro.assessment.api.v1.mapper.RecipeMapper;
import nl.abnamro.assessment.core.data.QueryRequest;
import nl.abnamro.assessment.core.exception.ResourceNotFoundException;
import nl.abnamro.assessment.repository.RecipeRepository;
import nl.abnamro.assessment.specification.RecipeSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class RecipeService {
    private static final String CACHE_NAME = "recipe:v1";
    private static final RecipeMapper MAPPER = RecipeMapper.INSTANCE;
    private static final List<String> SOURCE = Arrays.asList("data", "Recipe", "id");

    private final RecipeRepository recipeRepository;

    public RecipeService(final RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Page<RecipeResponse> page(
            final QueryRequest queryRequest,
            final RecipeFilterRequest recipeFilterRequest,
            final Pageable pageable
    ) {
        final var specification = new RecipeSpecification(queryRequest, recipeFilterRequest);

        return recipeRepository
                .findAll(specification, pageable)
                .map(MAPPER::toResponse);
    }

    @CachePut(value = CACHE_NAME, key = "#result.id")
    @Transactional
    public RecipeResponse create(final RecipeRequest request) {
        final var entity = MAPPER.toEntity(request);

        recipeRepository.save(entity);

        return MAPPER.toResponse(entity);
    }

    @Cacheable(value = CACHE_NAME, key = "#id")
    public RecipeResponse get(final UUID id) {
        final var entity = recipeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SOURCE, id));

        return MAPPER.toResponse(entity);
    }

    @CachePut(value = CACHE_NAME, key = "#result.id")
    @Transactional
    public RecipeResponse update(final UUID id, final RecipeRequest request) {
        final var recipe = recipeRepository
                .findLockedById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SOURCE, id));

        MAPPER.update(request, recipe);

        recipeRepository.save(recipe);

        return MAPPER.toResponse(recipe);
    }

    @CacheEvict(value = CACHE_NAME, key = "#id")
    @Transactional
    public void delete(final UUID id) {
        final var recipe = recipeRepository
                .findLockedById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SOURCE, id));

        recipeRepository.softDelete(recipe);
    }
}
