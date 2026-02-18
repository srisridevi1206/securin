package com.example.recipes.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document(collection = "recipes")
@Data
public class Recipe {

    @Id
    private String id;

    private String continent;
    private String country_state;
    private String url;
    private String cuisine;
    private String title;
    private Float rating;
    private Integer prep_time;
    private Integer cook_time;
    private Integer total_time;

    private String description;
    private Object nutrients;
    private Object ingredients;
    private Object instructions;
    private String serves;
}



