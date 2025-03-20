package com.dea.foodrecommenderbackend.service;

import com.dea.foodrecommenderbackend.config.OntologyConfig;
import com.dea.foodrecommenderbackend.dto.FoodRecommendationRequest;
import com.dea.foodrecommenderbackend.dto.FoodRecommendationResponse;
import com.dea.foodrecommenderbackend.model.Alergen;
import com.dea.foodrecommenderbackend.model.Food;
import com.dea.foodrecommenderbackend.model.TEEBreakdown;
import com.dea.foodrecommenderbackend.util.NutritionCalculator;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FoodRecommendationService {

    private final OntModel ontModel;
    private final String ontologyIri;

    @Autowired
    public FoodRecommendationService(OntModel ontModel, OntologyConfig ontologyConfig) {
        this.ontModel = ontModel;
        this.ontologyIri = ontologyConfig.getOntologyIri();
    }

    public TEEBreakdown getTEECalculation(FoodRecommendationRequest request) {
        return NutritionCalculator.calculateTEEBreakdown(request.getBeratBadan(), request.getTinggiBadan(),
                request.getUsia(), request.getUsiaKehamilan(), request.getFaktorAktivitas(), request.getFaktorStres());
    }


    public FoodRecommendationResponse generateFoodRecommendation(FoodRecommendationRequest request) {
        TEEBreakdown teeBreakdown = getTEECalculation(request);
        List<String> alergi = request.getAlergi();

        return new FoodRecommendationResponse(teeBreakdown.getTotalTEE(), teeBreakdown.getAmb(),
                teeBreakdown.getSarapan(), teeBreakdown.getMakanSiang(), teeBreakdown.getMakanMalam(),
                teeBreakdown.getCemilan(), getFoodsForCalories(teeBreakdown.getSarapan(), alergi, "Sarapan"),
                getFoodsForCalories(teeBreakdown.getMakanSiang(), alergi, "MakanSiang"),
                getFoodsForCalories(teeBreakdown.getMakanMalam(), alergi, "MakanMalam"),
                getFoodsForCalories(teeBreakdown.getCemilan(), alergi, "Cemilan"));
    }

    private List<Food> getFoodsForCalories(double calorieTarget, List<String> alergi, String waktuMakan) {
        List<Food> allFoods = getFoodsFromOntology(waktuMakan);
        Set<Alergen> alergiEnums = alergi.stream().map(Alergen::valueOf).collect(Collectors.toSet());

        List<Food> filteredFoods = allFoods.stream().filter(food -> !isFoodIncompatibleWithAllergies(food,
                alergiEnums)).sorted((f1, f2) -> Double.compare(f2.getKalori(), f1.getKalori())).toList();

        return selectFoodsForCalories(filteredFoods, calorieTarget);
    }

    private List<Food> selectFoodsForCalories(List<Food> foods, double calorieTarget) {
        double[] totalCalories = {0};
        return foods.stream().takeWhile(food -> (totalCalories[0] += food.getKalori()) <= calorieTarget + 10).collect(Collectors.toList());
    }

    private List<Food> getFoodsFromOntology(String waktuMakan) {
        List<Food> foods = new ArrayList<>();

        // Definisi nama properti dalam ontologi
        String menuClassName = "Menu";
        String memilikiKaloriPropertyName = "memilikiKalori";
        String memilikiProteinPropertyName = "memilikiProtein";
        String memilikiKarbohidratPropertyName = "memilikiKarbohidrat";
        String memilikiSeratPropertyName = "memilikiSerat";
        String memilikiZatBesiPropertyName = "memilikiZatBesi";
        String tidakMengandungPropertyName = "tidakMengandung";
        String sesuaiUntukWaktuMakanPropertyName = "disajikanPada";
        String memilikiNamaPropertyName = "memilikiNama";

        OntClass menuClass = ontModel.getOntClass(ontologyIri + menuClassName);
        if (menuClass == null) {
            System.out.println("Menu class not found in ontology: " + ontologyIri + menuClassName);
            return foods;
        }

        Individual waktuMakanIndividual = ontModel.getIndividual(ontologyIri + waktuMakan);
        if (waktuMakanIndividual == null) {
            return foods;
        }

        // Loop semua makanan yang sesuai dengan waktu makan
        ontModel.listIndividuals(menuClass).filterKeep(menu -> menu.hasProperty(ontModel.getProperty(ontologyIri + sesuaiUntukWaktuMakanPropertyName), waktuMakanIndividual)).forEachRemaining(menuIndividual -> {
            Property memilikiNamaProperty = ontModel.getProperty(ontologyIri + memilikiNamaPropertyName);
            Literal namaLiteral = (menuIndividual.getPropertyValue(memilikiNamaProperty) != null) ?
                    menuIndividual.getPropertyValue(memilikiNamaProperty).asLiteral() : null;
            String foodName = (namaLiteral != null) ? namaLiteral.getString() : menuIndividual.getLocalName();

            // Mengambil nilai properti makanan dengan pengecekan null
            double calories = getLiteralValue(menuIndividual, memilikiKaloriPropertyName, 0);
            double protein = getLiteralValue(menuIndividual, memilikiProteinPropertyName, 0.0);
            double carbs = getLiteralValue(menuIndividual, memilikiKarbohidratPropertyName, 0.0);
            double fiber = getLiteralValue(menuIndividual, memilikiSeratPropertyName, 0.0);
            double iron = getLiteralValue(menuIndividual, memilikiZatBesiPropertyName, 0.0);

            // Mengambil daftar alergen dari ontologi
            List<Alergen> alergenList = new ArrayList<>();
            ontModel.listObjectsOfProperty(menuIndividual,
                    ontModel.getProperty(ontologyIri + tidakMengandungPropertyName)).forEachRemaining(allergen -> {
                if (allergen.isResource()) {
                    String allergenName = ((Resource) allergen).getLocalName();
                    try {
                        alergenList.add(Alergen.valueOf(allergenName));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Unknown allergen: " + allergenName);
                    }
                }
            });

            foods.add(new Food(foodName, calories, protein, carbs, fiber, iron, alergenList));
        });

        return foods;
    }

    /**
     * Helper function untuk mendapatkan nilai dari properti ontologi dengan pengecekan null.
     */
    private double getLiteralValue(Individual individual, String propertyName, double defaultValue) {
        Property property = ontModel.getProperty(ontologyIri + propertyName);
        if (property == null)
            return defaultValue;

        Literal literal = individual.getPropertyValue(property) != null ?
                individual.getPropertyValue(property).asLiteral() : null;
        return (literal != null) ? literal.getDouble() : defaultValue;
    }


    private boolean isFoodIncompatibleWithAllergies(Food food, Set<Alergen> allergies) {
        String foodUri = ontologyIri + food.getNama();
        Individual foodIndividual = ontModel.getIndividual(foodUri);

        if (foodIndividual == null) {
            return false;
        }

        for (Alergen allergen : allergies) {
            String allergenUri = ontologyIri + allergen.name();
            Resource allergenResource = ontModel.getResource(allergenUri);

            if (allergenResource == null) {
                continue;
            }

            Property tidakMengandungProperty = ontModel.getProperty(ontologyIri + "#tidakMengandung");
            return foodIndividual.hasProperty(tidakMengandungProperty, allergenResource);
        }
        return false;
    }
}
