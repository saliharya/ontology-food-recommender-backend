package com.dea.foodrecommenderbackend.controller;

import com.dea.foodrecommenderbackend.dto.FoodRecommendationRequest;
import com.dea.foodrecommenderbackend.dto.FoodRecommendationResponse;
import com.dea.foodrecommenderbackend.service.FoodRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class FoodRecommendationController {
    private final FoodRecommendationService service;

    public FoodRecommendationController(FoodRecommendationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FoodRecommendationResponse> getRecommendations(@RequestBody FoodRecommendationRequest request) {
        FoodRecommendationResponse response = service.getFoodRecommendations(request);
        return ResponseEntity.ok(response);
    }
}
