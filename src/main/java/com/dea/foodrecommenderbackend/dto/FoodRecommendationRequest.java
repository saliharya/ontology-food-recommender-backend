package com.dea.foodrecommenderbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodRecommendationRequest {
    private double tinggiBadan; // tb
    private double beratBadan; // bb
    private double usia; // usia
    private double usiaKehamilan; // usia kehamilan
    private List<String> alergi;
    private String diabetesGestasional; // "ya" atau "tidak"
    private double faktorAktivitas;
    private double faktorStres;
}