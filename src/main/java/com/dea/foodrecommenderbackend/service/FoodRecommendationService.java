package com.dea.foodrecommenderbackend.service;

import com.dea.foodrecommenderbackend.dto.FoodRecommendationRequest;
import com.dea.foodrecommenderbackend.dto.FoodRecommendationResponse;
import com.dea.foodrecommenderbackend.model.PregnantWoman;
import com.dea.foodrecommenderbackend.util.NutritionCalculator;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FoodRecommendationService {
    private final OntModel ontModel;

    public FoodRecommendationService(OntModel ontModel) {
        this.ontModel = ontModel;
    }

    public FoodRecommendationResponse getFoodRecommendations(FoodRecommendationRequest request) {
        // Gunakan request untuk mengisi PregnantWoman
        PregnantWoman pregnantWoman = new PregnantWoman(request.getTb(), request.getBb(), request.getUsia(),
                request.getUsiaKehamilan(), request.getAlergi(),
                "ya".equalsIgnoreCase(request.getDiabetesGestasional()), request.getFaktorAktivitas(),
                request.getFaktorStres());

        // Hitung Total Energy Expenditure (TEE)
        double tee = NutritionCalculator.calculateTEE(pregnantWoman.getBb(), pregnantWoman.getUsiaKehamilan(),
                pregnantWoman.getFaktorAktivitas(), pregnantWoman.getFaktorStres());

        // Ambil rekomendasi makanan dari ontology
        List<String> recommendedFoods = getRecommendedFoodsFromOntology(pregnantWoman);

        return new FoodRecommendationResponse(tee, recommendedFoods);
    }

    private List<String> getRecommendedFoodsFromOntology(PregnantWoman pregnantWoman) {
        List<String> recommendedFoods = new ArrayList<>();

        String sparqlQuery = "PREFIX food: <http://www.semanticweb.org/dell/ontologies/2025/1/food#> " + "SELECT " +
                "?foodName WHERE { " + "  ?food a food:Menu ; " + "        food:memilikiNama ?foodName . " + "  " +
                "FILTER NOT EXISTS { " + "    ?food food:memilikiLevelNutrisi food:TinggiKarbohidrat . " + "    ?ibu " +
                "food:memilikiDiabetesGestasional \"ya\" " + "  } " + "}";


        // Eksekusi query
        Query query = QueryFactory.create(sparqlQuery);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, ontModel)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Literal foodName = soln.getLiteral("foodName");
                if (foodName != null) {
                    recommendedFoods.add(foodName.getString());
                }
            }
        }

        return recommendedFoods;
    }
}
