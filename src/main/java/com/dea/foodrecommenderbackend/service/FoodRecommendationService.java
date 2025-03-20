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
        String menuClassName = "Menu";
        String memilikiKaloriPropertyName = "memilikiKalori";
        String tidakMengandungPropertyName = "tidakMengandung";
        String sesuaiUntukWaktuMakanPropertyName = "disajikanPada";

        OntClass menuClass = ontModel.getOntClass(ontologyIri + menuClassName);
        if (menuClass == null) {
            return foods;
        }

        Individual waktuMakanIndividual = ontModel.getIndividual(ontologyIri + waktuMakan);
        if (waktuMakanIndividual == null) {
            return foods;
        }

        ontModel.listIndividuals(menuClass).filterKeep(menu -> menu.hasProperty(ontModel.getProperty(ontologyIri + sesuaiUntukWaktuMakanPropertyName), waktuMakanIndividual)).forEachRemaining(menuIndividual -> {
            String memilikiNamaPropertyName = "memilikiNama";
            Property memilikiNamaProperty = ontModel.getProperty(ontologyIri + memilikiNamaPropertyName);

            Literal namaLiteral = menuIndividual.getPropertyValue(memilikiNamaProperty) != null ?
                    menuIndividual.getPropertyValue(memilikiNamaProperty).asLiteral() : null;

            String foodName = (namaLiteral != null) ? namaLiteral.getString() : menuIndividual.getLocalName();

            int calories = 0;
            List<Alergen> alergenList = new ArrayList<>();

            Literal calorieLiteral =
                    menuIndividual.getPropertyValue(ontModel.getProperty(ontologyIri + memilikiKaloriPropertyName)).asLiteral();
            if (calorieLiteral != null) {
                calories = calorieLiteral.getInt();
            }

            ontModel.listObjectsOfProperty(menuIndividual,
                    ontModel.getProperty(ontologyIri + tidakMengandungPropertyName)).forEachRemaining(allergen -> {
                try {
                    Alergen a = Alergen.valueOf(((Resource) allergen).getLocalName());
                    alergenList.add(a);
                } catch (IllegalArgumentException e) {
                    // Ignore unknown allergens
                }
            });

            List<String> waktuMakanList = new ArrayList<>();
            ontModel.listObjectsOfProperty(menuIndividual,
                    ontModel.getProperty(ontologyIri + sesuaiUntukWaktuMakanPropertyName)).forEachRemaining(resource -> waktuMakanList.add(((Resource) resource).getLocalName()));

            String waktuMakanStr = String.join(", ", waktuMakanList);

            foods.add(new Food(foodName, calories, 0, 0, 0, 0, waktuMakanStr, alergenList));
        });

        return foods;
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
