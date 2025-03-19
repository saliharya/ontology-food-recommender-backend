package com.dea.foodrecommenderbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class FoodRecommendationResponse {
    private double totalEnergyExpenditure; // TEE
    private List<String> recommendedFoods;

    public FoodRecommendationResponse(double tee, List<String> recommendedFoods) {
        this.totalEnergyExpenditure = tee;
        this.recommendedFoods = recommendedFoods;
    }
}
