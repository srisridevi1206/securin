package com.example.recipes.repository;

import com.example.recipes.entity.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface RecipeRepository 
        extends MongoRepository<Recipe, String> {
    
    List<Recipe> findByTitleContainingIgnoreCase(String title);
    List<Recipe> findByCuisineContainingIgnoreCase(String cuisine);
    List<Recipe> findByRatingGreaterThanEqual(Float rating);
    List<Recipe> findByTotalTimeLessThanEqual(Integer totalTime);
}

