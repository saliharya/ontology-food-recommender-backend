package com.dea.foodrecommenderbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Food {
    private String nama;
    private double kalori;
    private double protein;
    private double karbohidrat;
    private double serat;
    private double zatBesi;
    private String sesuaiUntukWaktuMakan;
    private List<Alergen> tidakMengandung;
}
