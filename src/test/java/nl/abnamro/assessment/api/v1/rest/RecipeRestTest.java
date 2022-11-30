package nl.abnamro.assessment.api.v1.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.abnamro.assessment.ApplicationTests;
import nl.abnamro.assessment.api.v1.data.IngredientRequest;
import nl.abnamro.assessment.api.v1.data.InstructionRequest;
import nl.abnamro.assessment.api.v1.data.RecipeRequest;
import nl.abnamro.assessment.api.v1.data.RecipeResponse;
import nl.abnamro.assessment.core.data.DataResponse;
import nl.abnamro.assessment.model.Recipe;
import nl.abnamro.assessment.model.RecipeIngredient;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RecipeRestTest extends ApplicationTests {
    private static final String URL = "/api/v1/recipes";
    private static final String INSTRUCTION_PREFIX = "Create unit tests that actually test the APIs - ";
    private static final int LIST_SIZE = 21;
    private static final int LIST_MEMBER_SIZE = 9;
    private static final int LIST_META_SIZE = 3;
    private static final int DETAIL_MEMBER_SIZE = 9;
    private static final int DETAIL_META_SIZE = 2;
    private static final int DETAIL_LINKS_SIZE = 1;
    private static final int META_PAGE_SIZE = 5;
    private static final int INSTRUCTION_SIZE = 5;
    private static final int INSTRUCTION_MEMBER_SIZE = 2;
    private static final int INGREDIENT_MEMBER_SIZE = 4;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Recipe page should return 200")
    public void whenPageValidRequest_ShouldReturn200() throws Exception {
        final var suffix = createSuffix();
        final var responses = new ArrayList<RecipeResponse>();

        for (var i = 0; i < LIST_SIZE; i++) {
            final var request = createRequest(suffix);
            final var response = postRequest(request);

            assertResponse(request, response);

            responses.add(response);
        }

        Assertions.assertEquals(LIST_SIZE, responses.size());

        final var page = 0;
        final var requestUrl = UriComponentsBuilder.fromUriString(URL)
                .queryParam("page", page)
                .queryParam("size", LIST_SIZE)
                .queryParam("sort", "createdAt,desc")
                .build()
                .toUriString();
        final var builder = get(requestUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(bearerHeaders);
        final var content = mockMvc
                .perform(builder)
                .andExpectAll(
                        isOk(),
                        isJson(),
                        jsonPath("$.data").isArray(),
                        jsonPath("$.data.length()").value(LIST_SIZE),
                        jsonPath("$.data[0].length()").value(LIST_MEMBER_SIZE),
                        jsonPath("$.data[0].instructions.length()").value(INSTRUCTION_SIZE),
                        jsonPath("$.data[0].instructions[0].length()").value(INSTRUCTION_MEMBER_SIZE),
                        jsonPath("$.data[0].ingredients.length()").value(INSTRUCTION_SIZE),
                        jsonPath("$.data[0].ingredients[0].length()").value(INGREDIENT_MEMBER_SIZE),
                        jsonPath("$.meta").isMap(),
                        jsonPath("$.meta.length()").value(LIST_META_SIZE),
                        jsonPath("$.meta.status").value(HttpStatus.OK.value()),
                        jsonPath("$.meta.page").isMap(),
                        jsonPath("$.meta.page.length()").value(META_PAGE_SIZE),
                        jsonPath("$.meta.page.size").value(LIST_SIZE),
                        jsonPath("$.meta.page.number").value(page),
                        jsonPath("$.links").isMap()
                )
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        final var data = mapper.readValue(content, DataResponse.class);
        final var list = mapper.convertValue(data.getData(), List.class);

        // Validate each item is in the list
        for (final var item : list) {
            final var recipe = mapper.convertValue(item, RecipeResponse.class);
            final var exists = exists(recipe, responses);

            Assertions.assertTrue(exists, "%s does not exist in the responses".formatted(recipe.title()));
        }

        Assertions.assertTrue(
                list.size() <= LIST_SIZE,
                "Returned page size is greater than the list size"
        );
    }

    @Test
    @DisplayName("Recipe create should return 201")
    public void whenCreateValidRequest_ShouldReturn201() throws Exception {
        final var suffix = createSuffix();
        final var request = createRequest(suffix);
        final var response = postRequest(request);

        assertResponse(request, response);
    }

    @Test
    @DisplayName("Recipe get should return 200")
    public void whenGetValidRequest_ShouldReturn200() throws Exception {
        final var suffix = createSuffix();
        final var request = createRequest(suffix);
        final var response = postRequest(request);

        assertResponse(request, response);

        final var url = "%s/%s".formatted(URL, response.id());
        final var builder = get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(bearerHeaders);
        final var matchers = detailMatcher(request, HttpStatus.OK);
        final var content = mockMvc
                .perform(builder)
                .andExpectAll(matchers)
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        final var fromGet = convertContent(content);

        assertResponse(request, fromGet);
    }

    @Test
    @DisplayName("Recipe update should return 200")
    public void whenUpdateValidRequest_ShouldReturn200() throws Exception {
        final var initialSuffix = createSuffix();
        final var initialRequest = createRequest(initialSuffix);
        final var initialResponse = postRequest(initialRequest);

        assertResponse(initialRequest, initialResponse);

        final var updatedSuffix = createSuffix();
        final var updatedRequest = createRequest(updatedSuffix);
        final var updatedJson = mapper.writeValueAsString(updatedRequest);
        final var url = "%s/%s".formatted(URL, initialResponse.id());
        final var builder = put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(bearerHeaders)
                .content(updatedJson);
        final var matchers = detailMatcher(updatedRequest, HttpStatus.OK);
        final var content = mockMvc
                .perform(builder)
                .andExpectAll(matchers)
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        final var updatedResponse = convertContent(content);

        Assertions.assertEquals(initialResponse.id(), updatedResponse.id());

        assertResponse(updatedRequest, updatedResponse);
    }

    @Test
    @DisplayName("Recipe delete should return 204 and then 404")
    public void whenDeleteValidRequest_ShouldReturn204And404() throws Exception {
        final var initialSuffix = createSuffix();
        final var initialRequest = createRequest(initialSuffix);
        final var initialResponse = postRequest(initialRequest);

        assertResponse(initialRequest, initialResponse);

        final var url = "%s/%s".formatted(URL, initialResponse.id());
        final var deleteBuilder = delete(url).headers(bearerHeaders);

        mockMvc
                .perform(deleteBuilder)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());


        final var getBuilder = get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(bearerHeaders);

        mockMvc
                .perform(getBuilder)
                .andExpect(status().isNotFound());
    }

    private RecipeResponse postRequest(final RecipeRequest request) throws Exception {
        final var json = mapper.writeValueAsString(request);
        final var builder = post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(bearerHeaders)
                .content(json);
        final var matchers = detailMatcher(request, HttpStatus.CREATED);
        final var content = mockMvc
                .perform(builder)
                .andExpectAll(matchers)
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        return convertContent(content);
    }

    private RecipeResponse convertContent(final String content) throws JsonProcessingException {
        final var data = mapper.readValue(content, DataResponse.class);

        return mapper.convertValue(data.getData(), RecipeResponse.class);
    }

    private ResultMatcher[] detailMatcher(final RecipeRequest request, final HttpStatus status) {
        return new ResultMatcher[]{
                status().is(status.value()),
                isJson(),
                jsonPath("$.data").isNotEmpty(),
                jsonPath("$.data.length()").value(DETAIL_MEMBER_SIZE),
                jsonPath("$.data.instructions.length()").value(request.instructions().size()),
                jsonPath("$.data.instructions[0].length()").value(INSTRUCTION_MEMBER_SIZE),
                jsonPath("$.data.ingredients.length()").value(request.ingredients().size()),
                jsonPath("$.data.ingredients[0].length()").value(INGREDIENT_MEMBER_SIZE),
                jsonPath("$.meta").isMap(),
                jsonPath("$.meta.length()").value(DETAIL_META_SIZE),
                jsonPath("$.meta.status").value(status.value()),
                jsonPath("$.links").isMap(),
                jsonPath("$.links.length()").value(DETAIL_LINKS_SIZE)
        };
    }

    private boolean exists(final RecipeResponse recipe, final List<RecipeResponse> responses) {
        return responses
                .stream()
                .anyMatch(response -> {
                    final var titleMatches = response.title().equals(recipe.title());
                    final var ingredientsMatches = response
                            .ingredients()
                            .stream()
                            .allMatch(ingredient -> recipe
                                    .ingredients()
                                    .stream()
                                    .anyMatch(i -> i.name().equals(ingredient.name()))
                            );

                    return titleMatches && ingredientsMatches;
                });
    }

    private void assertResponse(final RecipeRequest request, final RecipeResponse response) {
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        Assertions.assertNotNull(response.createdAt());

        assertUser(response.createdBy());

        Assertions.assertEquals(0, response.version());
        Assertions.assertEquals(request.title(), response.title());
        Assertions.assertEquals(request.instructions().size(), response.instructions().size());
        Assertions.assertEquals(request.dishType(), response.dishType());
        Assertions.assertEquals(request.ingredients().size(), response.ingredients().size());
        Assertions.assertEquals(request.servingSize(), response.servingSize());
    }

    private RecipeRequest createRequest(final String suffix) {
        final var rand = faker.random().nextInt(10000);
        final var dishType = faker.random().nextBoolean()
                ? Recipe.DishType.VEGETARIAN
                : Recipe.DishType.NON_VEGETARIAN;
        final var title = "%s - %s - %s".formatted(faker.food().dish(), rand, suffix);
        final var instructions = new ArrayList<InstructionRequest>();
        final var ingredients = new ArrayList<IngredientRequest>();

        for (var i = 0; i < INSTRUCTION_SIZE; i++) {
            final var instruction = createInstruction(i, suffix);

            instructions.add(instruction);

            final var ingredient = createIngredient(i);

            ingredients.add(ingredient);
        }

        final var servingSize = faker.random().nextInt(1, 100);

        return new RecipeRequest(title, instructions, dishType.getValue(), ingredients, servingSize);
    }

    private InstructionRequest createInstruction(final int index, final String suffix) {
        final var detail = "%s%s - %s".formatted(INSTRUCTION_PREFIX, index + 1, suffix);

        return new InstructionRequest(detail, index + 1);
    }

    private IngredientRequest createIngredient(final int index) {
        final var number = faker.random().nextInt(100);
        // Avoid duplicate names
        final var name = "%s - %s".formatted(faker.food().ingredient(), number);
        final var value = faker.food().measurement();
        final var unit = RecipeIngredient.Unit.VALUE.getName();

        return new IngredientRequest(name, value, unit, index + 1);
    }

    private String createSuffix() {
        return RandomStringUtils.randomAlphabetic(20);
    }
}
