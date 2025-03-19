package com.dea.foodrecommenderbackend.util;

import com.dea.foodrecommenderbackend.model.TEEBreakdown;

public class NutritionCalculator {

    public static double calculateTEE(double weight, double pregnancyAge, double activityFactor, double stressFactor) {
        double trimesterFactor = getTrimesterFactor(pregnancyAge);
        return (weight * 30 + trimesterFactor) * activityFactor * stressFactor;
    }

    private static double getTrimesterFactor(double pregnancyAge) {
        if (pregnancyAge < 13) {
            return 85;
        } else if (pregnancyAge < 27) {
            return 285;
        } else {
            return 475;
        }
    }

    public static TEEBreakdown calculateTEEBreakdown(double weight, double pregnancyAge, double activityFactor,
                                                     double stressFactor) {
        double totalTEE = calculateTEE(weight, pregnancyAge, activityFactor, stressFactor);

        double sarapan = totalTEE * 0.30;
        double makanSiang = totalTEE * 0.35;
        double makanMalam = totalTEE * 0.25;
        double cemilan = totalTEE * 0.10;

        return new TEEBreakdown(totalTEE, sarapan, makanSiang, makanMalam, cemilan);
    }
}
