package com.dea.foodrecommenderbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TEEBreakdown {
    private double totalTEE;
    private double sarapan;
    private double makanSiang;
    private double makanMalam;
    private double cemilan;
}
