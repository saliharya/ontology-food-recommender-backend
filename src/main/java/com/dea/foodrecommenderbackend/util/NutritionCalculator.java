package com.dea.foodrecommenderbackend.util;

public class NutritionCalculator {

    public static double calculateTEE(double weight, double pregnancyAge, double activityFactor, double stressFactor) {
        double trimesterFactor = getTrimesterFactor(pregnancyAge);
        return (weight * 30 + trimesterFactor) * activityFactor * stressFactor;
    }

    private static double getTrimesterFactor(double pregnancyAge) {
        if (pregnancyAge < 13) {
            return 85;  // Trimester 1
        } else if (pregnancyAge < 27) {
            return 285; // Trimester 2
        } else {
            return 475; // Trimester 3
        }
    }
}
