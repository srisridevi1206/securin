package com.example.recipes;

import com.example.recipes.entity.Recipe;
import com.example.recipes.repository.RecipeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DataLoader implements CommandLineRunner {

    private final RecipeRepository repository;

    public DataLoader(RecipeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        InputStream input = getClass()
                .getResourceAsStream("/recipes.json");

        JsonNode root = mapper.readTree(input);

        for (JsonNode node : root) {

            Recipe recipe = new Recipe();

            recipe.setContinent(node.get("Contient").asText());
            recipe.setCountry_state(node.get("Country_State").asText());
            recipe.setUrl(node.get("URL").asText());
            recipe.setCuisine(node.get("cuisine").asText());
            recipe.setTitle(node.get("title").asText());

            if (node.has("rating") && !node.get("rating").isNull()) {
                try {
                    double ratingValue = node.get("rating").asDouble();
                    if (!Double.isNaN(ratingValue)) {
                        recipe.setRating((float) ratingValue);
                    }
                } catch (Exception e) {
                }
            }

            if (node.has("prep_time") && !node.get("prep_time").isNull()) {
                try {
                    int prepTime = node.get("prep_time").asInt();
                    recipe.setPrep_time(prepTime);
                } catch (Exception e) {
                }
            }

            if (node.has("cook_time") && !node.get("cook_time").isNull()) {
                try {
                    int cookTime = node.get("cook_time").asInt();
                    recipe.setCook_time(cookTime);
                } catch (Exception e) {
                }
            }

            if (node.has("total_time") && !node.get("total_time").isNull()) {
                try {
                    int totalTime = node.get("total_time").asInt();
                    recipe.setTotal_time(totalTime);
                } catch (Exception e) {
                 
                }
            }

            recipe.setDescription(node.get("description").asText());

            recipe.setNutrients(mapper.treeToValue(node.get("nutrients"), Object.class));
            recipe.setIngredients(mapper.treeToValue(node.get("ingredients"), Object.class));
            recipe.setInstructions(mapper.treeToValue(node.get("instructions"), Object.class));

            recipe.setServes(node.get("serves").asText());

            repository.save(recipe);
        }

        System.out.println("Data inserted successfully to MongoDB");
    }
}
