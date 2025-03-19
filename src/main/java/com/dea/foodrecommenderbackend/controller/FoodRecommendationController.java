package com.dea.foodrecommenderbackend.controller;

import com.dea.foodrecommenderbackend.dto.FoodRecommendationRequest;
import com.dea.foodrecommenderbackend.dto.FoodRecommendationResponse;
import com.dea.foodrecommenderbackend.service.FoodRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/food-recommendation")
public class FoodRecommendationController {

    @Autowired
    private FoodRecommendationService foodRecommendationService;

    @PostMapping
    public FoodRecommendationResponse getFoodRecommendation(@RequestBody FoodRecommendationRequest request) {
        return foodRecommendationService.generateFoodRecommendation(request);
    }
}
