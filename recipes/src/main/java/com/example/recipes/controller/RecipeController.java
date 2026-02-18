package com.example.recipes.controller;

import com.example.recipes.entity.Recipe;
import com.example.recipes.repository.RecipeRepository;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*")
public class RecipeController {

    private final RecipeRepository repository;

    public RecipeController(RecipeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Map<String, Object> getAllRecipes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        List<Recipe> allRecipes = repository.findAll();
        List<Recipe> sortedRecipes = allRecipes.stream()
                .sorted((r1, r2) -> {
                    Float rating1 = r1.getRating() != null ? r1.getRating() : 0f;
                    Float rating2 = r2.getRating() != null ? r2.getRating() : 0f;
                    return rating2.compareTo(rating1);
                })
                .collect(Collectors.toList());

        int start = (page - 1) * limit;
        int end = Math.min(start + limit, sortedRecipes.size());
        List<Recipe> paginatedRecipes = sortedRecipes.subList(start, end);

        return Map.of(
                "page", page,
                "limit", limit,
                "total", sortedRecipes.size(),
                "data", paginatedRecipes
        );
    }

    @GetMapping("/search")
    public Map<String, Object> searchRecipes(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) String total_time,
            @RequestParam(required = false) String calories) {

        List<Recipe> results = repository.findAll();

        if (title != null && !title.isEmpty()) {
            results = results.stream()
                    .filter(r -> r.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (cuisine != null && !cuisine.isEmpty()) {
            results = results.stream()
                    .filter(r -> r.getCuisine().toLowerCase().contains(cuisine.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (rating != null && !rating.isEmpty()) {
            results = filterByNumericCondition(results, rating, r -> r.getRating());
        }

        if (total_time != null && !total_time.isEmpty()) {
            results = filterByNumericConditionInt(results, total_time, r -> r.getTotal_time());
        }

        if (calories != null && !calories.isEmpty()) {
            results = results.stream()
                    .filter(r -> matchesCalorieFilter(r, calories))
                    .collect(Collectors.toList());
        }

        results = results.stream()
                .sorted((r1, r2) -> {
                    Float rating1 = r1.getRating() != null ? r1.getRating() : 0f;
                    Float rating2 = r2.getRating() != null ? r2.getRating() : 0f;
                    return rating2.compareTo(rating1);
                })
                .collect(Collectors.toList());

        return Map.of("data", results);
    }

    private List<Recipe> filterByNumericCondition(List<Recipe> recipes, String condition, 
                                                   java.util.function.Function<Recipe, Float> valueExtractor) {
        try {
            if (condition.startsWith(">=")) {
                float value = Float.parseFloat(condition.substring(2));
                return recipes.stream()
                        .filter(r -> {
                            Float val = valueExtractor.apply(r);
                            return val != null && val >= value;
                        })
                        .collect(Collectors.toList());
            } else if (condition.startsWith("<=")) {
                float value = Float.parseFloat(condition.substring(2));
                return recipes.stream()
                        .filter(r -> {
                            Float val = valueExtractor.apply(r);
                            return val != null && val <= value;
                        })
                        .collect(Collectors.toList());
            } else if (condition.startsWith(">")) {
                float value = Float.parseFloat(condition.substring(1));
                return recipes.stream()
                        .filter(r -> {
                            Float val = valueExtractor.apply(r);
                            return val != null && val > value;
                        })
                        .collect(Collectors.toList());
            } else if (condition.startsWith("<")) {
                float value = Float.parseFloat(condition.substring(1));
                return recipes.stream()
                        .filter(r -> {
                            Float val = valueExtractor.apply(r);
                            return val != null && val < value;
                        })
                        .collect(Collectors.toList());
            } else if (condition.startsWith("=")) {
                float value = Float.parseFloat(condition.substring(1));
                return recipes.stream()
                        .filter(r -> {
                            Float val = valueExtractor.apply(r);
                            return val != null && val.equals(value);
                        })
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
        }
        return recipes;
    }

    private List<Recipe> filterByNumericConditionInt(List<Recipe> recipes, String condition, 
                                                      java.util.function.Function<Recipe, Integer> valueExtractor) {
        try {
            if (condition.startsWith(">=")) {
                int value = Integer.parseInt(condition.substring(2));
                return recipes.stream()
                        .filter(r -> {
                            Integer val = valueExtractor.apply(r);
                            return val != null && val >= value;
                        })
                        .collect(Collectors.toList());
            } else if (condition.startsWith("<=")) {
                int value = Integer.parseInt(condition.substring(2));
                return recipes.stream()
                        .filter(r -> {
                            Integer val = valueExtractor.apply(r);
                            return val != null && val <= value;
                        })
                        .collect(Collectors.toList());
            } else if (condition.startsWith(">")) {
                int value = Integer.parseInt(condition.substring(1));
                return recipes.stream()
                        .filter(r -> {
                            Integer val = valueExtractor.apply(r);
                            return val != null && val > value;
                        })
                        .collect(Collectors.toList());
            } else if (condition.startsWith("<")) {
                int value = Integer.parseInt(condition.substring(1));
                return recipes.stream()
                        .filter(r -> {
                            Integer val = valueExtractor.apply(r);
                            return val != null && val < value;
                        })
                        .collect(Collectors.toList());
            } else if (condition.startsWith("=")) {
                int value = Integer.parseInt(condition.substring(1));
                return recipes.stream()
                        .filter(r -> {
                            Integer val = valueExtractor.apply(r);
                            return val != null && val.equals(value);
                        })
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
        }
        return recipes;
    }

    private boolean matchesCalorieFilter(Recipe recipe, String calorieCondition) {
        try {
            if (recipe.getNutrients() instanceof Map) {
                Map<String, Object> nutrients = (Map<String, Object>) recipe.getNutrients();
                String caloriesStr = (String) nutrients.get("calories");
                if (caloriesStr != null) {
                    String numericPart = caloriesStr.replaceAll("[^0-9.]", "");
                    float calories = Float.parseFloat(numericPart);

                    if (calorieCondition.startsWith(">=")) {
                        return calories >= Float.parseFloat(calorieCondition.substring(2));
                    } else if (calorieCondition.startsWith("<=")) {
                        return calories <= Float.parseFloat(calorieCondition.substring(2));
                    } else if (calorieCondition.startsWith(">")) {
                        return calories > Float.parseFloat(calorieCondition.substring(1));
                    } else if (calorieCondition.startsWith("<")) {
                        return calories < Float.parseFloat(calorieCondition.substring(1));
                    } else if (calorieCondition.startsWith("=")) {
                        return calories == Float.parseFloat(calorieCondition.substring(1));
                    }
                }
            }
        } catch (Exception e) {
        }
        return true;
    }
}


