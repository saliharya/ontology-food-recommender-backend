package com.dea.foodrecommenderbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class FoodRecommendationRequest {
    private double tb; // tb
    private double bb; // bb
    private double usia; // usia
    private double usiaKehamilan; // usia kehamilan
    private List<String> alergi;
    private String diabetesGestasional; // "ya" atau "tidak"
    private double faktorAktivitas;
    private double faktorStres;
}
