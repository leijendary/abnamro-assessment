package nl.abnamro.assessment.api.v1.mapper;

import nl.abnamro.assessment.api.v1.data.RecipeRequest;
import nl.abnamro.assessment.api.v1.data.RecipeResponse;
import nl.abnamro.assessment.model.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RecipeMapper {
    RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);

    RecipeResponse toResponse(Recipe recipe);

    Recipe toEntity(RecipeRequest request);

    void update(RecipeRequest request, @MappingTarget Recipe recipe);
}
