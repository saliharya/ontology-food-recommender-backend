//package com.dea.foodrecommenderbackend;
//
//import org.apache.jena.ontology.*;
//import org.apache.jena.rdf.model.Literal;
//import org.apache.jena.rdf.model.Property;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Set;
//
//@Component
//public class OntologyLoaderTest implements CommandLineRunner {
//
//    private final OntModel ontModel;
//
//    public OntologyLoaderTest(OntModel ontModel) {
//        this.ontModel = ontModel;
//    }
//
//    @Override
//    public void run(String... args) {
//        System.out.println("=== Ontology Loaded Successfully ===");
//
//        // Properti label (rdfs:label) untuk mendapatkan nama deskriptif
//        Property labelProperty = ontModel.getProperty("http://www.w3.org/2000/01/rdf-schema#label");
//        // Properti "memilikiNama" untuk mengambil nama individu dari Data Property
//        Property hasNameProperty = ontModel.getProperty("http://www.semanticweb.org/dell/ontologies/2025/1/food#memilikiNama");
//
//        // Ambil semua kelas, kecuali "Thing"
//        Set<OntClass> classes = ontModel.listClasses().toSet();
//        classes.removeIf(cls -> cls.getLocalName() == null || cls.getLocalName().equals("Thing"));
//
//        System.out.println("Number of Classes: " + classes.size());
//        classes.forEach(cls -> System.out.println("Class: " + cls.getLocalName()));
//
//        // Ambil semua Object Properties
//        Set<ObjectProperty> objectProperties = ontModel.listObjectProperties().toSet();
//        System.out.println("\nNumber of Object Properties: " + objectProperties.size());
//        objectProperties.forEach(prop -> System.out.println("Object Property: " + prop.getLocalName()));
//
//        // Ambil semua Data Properties
//        Set<DatatypeProperty> dataProperties = ontModel.listDatatypeProperties().toSet();
//        System.out.println("\nNumber of Data Properties: " + dataProperties.size());
//        dataProperties.forEach(prop -> System.out.println("Data Property: " + prop.getLocalName()));
//
//        // Tampilkan semua Individuals by Class (maksimal 5 per kelas)
//        System.out.println("\n=== Individuals by Class (Max 5 per Class) ===");
//        for (OntClass cls : classes) {
//            Set<Individual> individuals = ontModel.listIndividuals(cls).toSet();
//            if (!individuals.isEmpty()) {
//                System.out.println("Class: " + cls.getLocalName() + " -> Showing up to 5 Individuals");
//                individuals.stream().limit(5)  // Batasi hanya 5 individu per kelas
//                        .forEach(ind -> {
//                            String indName = ind.getLocalName();
//                            String displayName = ""; // Untuk menyimpan hasil nama
//
//                            // Cek apakah individu memiliki Data Property "memilikiNama"
//                            if (hasNameProperty != null && ind.hasProperty(hasNameProperty)) {
//                                Literal nameLiteral = ind.getPropertyValue(hasNameProperty).asLiteral();
//                                if (nameLiteral != null) {
//                                    displayName = nameLiteral.getString();
//                                }
//                            }
//                            // Jika tidak ada "memilikiNama", fallback ke rdfs:label
//                            else if (labelProperty != null && ind.hasProperty(labelProperty)) {
//                                Literal labelLiteral = ind.getProperty(labelProperty).getLiteral();
//                                if (labelLiteral != null) {
//                                    displayName = labelLiteral.getString();
//                                }
//                            }
//
//                            // Cetak hasilnya
//                            System.out.println("  - " + indName + (displayName.isEmpty() ? "" : " (" + displayName + ")"));
//                        });
//            }
//        }
//    }
//}