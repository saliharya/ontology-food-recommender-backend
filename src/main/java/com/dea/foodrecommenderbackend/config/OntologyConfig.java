package com.dea.foodrecommenderbackend.config;

import openllet.jena.PelletReasonerFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class OntologyConfig {

    @Value("${ontology.file}")
    private String ontologyFile;

    @Value("${ontology.iri}")
    private String ontologyIri;

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