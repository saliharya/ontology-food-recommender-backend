package com.dea.foodrecommenderbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PregnantWoman {
    private double tb; // tb
    private double bb; // bb
    private double usia; // usia
    private double usiaKehamilan; // usia kehamilan
    private List<String> alergi;
    private String diabetesGestasional; // "ya" atau "tidak"
    private double faktorAktivitas;
    private double faktorStres;

    public PregnantWoman(double tb, double bb, double usia, double usiaKehamilan, List<String> alergi, boolean b,
                         double faktorAktivitas, double faktorStres) {
        this.tb = tb;
        this.bb = bb;
        this.usia = usia;
        this.usiaKehamilan = usiaKehamilan;
        this.alergi = alergi;
        this.diabetesGestasional = "tidak";
        this.faktorAktivitas = faktorAktivitas;
        this.faktorStres = faktorStres;
    }
}
