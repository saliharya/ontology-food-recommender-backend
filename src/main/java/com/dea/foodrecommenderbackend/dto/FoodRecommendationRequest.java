package com.dea.foodrecommenderbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodRecommendationRequest {
    private double tinggiBadan;
    private double beratBadan;
    private double usia;
    private double usiaKehamilan;
    private List<String> alergi;
    private String diabetesGestasional;
    private double faktorAktivitas;
    private double faktorStres;
}