package com.dea.foodrecommenderbackend.service;

import com.dea.foodrecommenderbackend.dto.FoodRecommendationRequest;
import com.dea.foodrecommenderbackend.dto.FoodRecommendationResponse;
import com.dea.foodrecommenderbackend.model.Alergen;
import com.dea.foodrecommenderbackend.model.Food;
import com.dea.foodrecommenderbackend.model.TEEBreakdown;
import com.dea.foodrecommenderbackend.util.NutritionCalculator;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FoodRecommendationService {

    private static final List<Food> ALL_FOODS = List.of(new Food("Oatmeal", 150, 5, 27, 4, 1.5, List.of(Alergen.Susu,
            Alergen.Seafood, Alergen.Telur)), new Food("Telur Rebus", 78, 6, 1, 0, 0.6, List.of(Alergen.Kacang,
            Alergen.Susu, Alergen.Seafood)), new Food("Nasi Merah", 215, 5, 45, 3, 0.8, List.of(Alergen.Kacang,
            Alergen.Susu, Alergen.Seafood, Alergen.Telur)), new Food("Ayam Panggang", 165, 31, 0, 0, 1.3,
            List.of(Alergen.Kacang, Alergen.Susu, Alergen.Seafood, Alergen.Telur)), new Food("Salmon Panggang", 280,
            25, 0, 0, 1.2, List.of(Alergen.Kacang, Alergen.Susu, Alergen.Telur)), new Food("Kentang Rebus", 160, 4,
            37, 3, 1.1, List.of(Alergen.Kacang, Alergen.Susu, Alergen.Seafood, Alergen.Telur)), new Food("Yogurt " +
            "Greek", 100, 10, 7, 0, 0.3, List.of(Alergen.Kacang, Alergen.Seafood, Alergen.Telur)),
            new Food("Kacang " + "Almond", 170, 6, 6, 4, 1.2, List.of(Alergen.Susu, Alergen.Seafood, Alergen.Telur)));

    public TEEBreakdown getTEECalculation(FoodRecommendationRequest request) {
        return NutritionCalculator.calculateTEEBreakdown(request.getBeratBadan(), request.getUsiaKehamilan(),
                request.getFaktorAktivitas(), request.getFaktorStres());
    }

    public FoodRecommendationResponse generateFoodRecommendation(FoodRecommendationRequest request) {
        TEEBreakdown teeBreakdown = getTEECalculation(request);
        List<String> alergi = request.getAlergi();

        return new FoodRecommendationResponse(getFoodsForCalories(teeBreakdown.getSarapan(), alergi),
                getFoodsForCalories(teeBreakdown.getMakanSiang(), alergi),
                getFoodsForCalories(teeBreakdown.getMakanMalam(), alergi),
                getFoodsForCalories(teeBreakdown.getCemilan(), alergi));
    }

    private List<Food> getFoodsForCalories(double calorieTarget, List<String> alergi) {
        Set<Alergen> alergiEnums = alergi.stream().map(Alergen::valueOf).collect(Collectors.toSet());

        List<Food> filteredFoods =
                ALL_FOODS.stream().filter(food -> new HashSet<>(food.getTidakMengandung()).containsAll(alergiEnums)).sorted((f1, f2) -> Double.compare(f2.getKalori(), f1.getKalori())).toList();

        return selectFoodsForCalories(filteredFoods, calorieTarget);
    }

    private List<Food> selectFoodsForCalories(List<Food> foods, double calorieTarget) {
        double[] totalCalories = {0};

        return foods.stream().takeWhile(food -> (totalCalories[0] += food.getKalori()) <= calorieTarget + 10).collect(Collectors.toList());
    }
}
