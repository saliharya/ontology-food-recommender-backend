package com.dea.foodrecommenderbackend.util;

import com.dea.foodrecommenderbackend.model.TEEBreakdown;

public class NutritionCalculator {

    public static double calculateAMB(double weight, double height, double age) {
        return 655 + (9.6 * weight) + (1.85 * height) - (4.68 * age);
    }

    public static double calculateTEE(double weight, double height, double age, double pregnancyAge,
                                      double activityFactor, double stressFactor) {
        double amb = calculateAMB(weight, height, age);
        double trimesterFactor = getTrimesterFactor(pregnancyAge);
        return (amb + trimesterFactor) * activityFactor * stressFactor;
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

    public static TEEBreakdown calculateTEEBreakdown(double weight, double height, double age, double pregnancyAge,
                                                     double activityFactor, double stressFactor) {
        double amb = calculateAMB(weight, height, age);
        double totalTEE = calculateTEE(weight, height, age, pregnancyAge, activityFactor, stressFactor);

        double sarapan = totalTEE * 0.25;
        double makanSiang = totalTEE * 0.30;
        double makanMalam = totalTEE * 0.25;
        double cemilan = totalTEE * 0.20;

        return new TEEBreakdown(totalTEE, amb, sarapan, makanSiang, makanMalam, cemilan);
    }
}