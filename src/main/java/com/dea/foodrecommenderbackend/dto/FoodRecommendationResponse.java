package com.dea.foodrecommenderbackend.dto;

import com.dea.foodrecommenderbackend.model.Food;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodRecommendationResponse {
    private double amb;
    private double tee;

    private double kaloriSarapan;
    private double kaloriMakanSiang;
    private double kaloriMakanMalam;
    private double kaloriCemilan;

    private List<Food> sarapan;
    private List<Food> makanSiang;
    private List<Food> makanMalam;
    private List<Food> cemilan;
}


