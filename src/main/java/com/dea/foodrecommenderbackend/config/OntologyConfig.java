package com.dea.foodrecommenderbackend.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import openllet.jena.PelletReasonerFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Getter
@Component
@Configuration
public class OntologyConfig {

    @Value("${ontology.file}")
    private String ontologyFile;

    @Value("${ontology.iri}")
    private String ontologyIri;

    @PostConstruct
    public void init() {
        System.out.println("✅ Ontology File: " + ontologyFile);
        System.out.println("✅ Ontology IRI: " + ontologyIri);
    }

    @Bean
    public OntModel ontModel() {
        OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
        InputStream in = FileManager.get().open(ontologyFile);
        if (in == null) {
            throw new IllegalArgumentException("Ontology file not found: " + ontologyFile);
        }
        model.read(in, ontologyIri);
        return model;
    }
}
