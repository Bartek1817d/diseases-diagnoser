package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.ontology;

import org.semanticweb.owlapi.model.*;

class OntologyProperties {

     final OWLDataProperty firstNameProperty;
     final OWLDataProperty lastNameProperty;
     final OWLDataProperty ageProperty;
     final OWLDataProperty heightProperty;
     final OWLDataProperty weightProperty;

     final OWLObjectProperty symptomProperty;
     final OWLObjectProperty diseaseProperty;
     final OWLObjectProperty testProperty;
     final OWLObjectProperty negativeTestProperty;
     final OWLObjectProperty treatmentProperty;
     final OWLObjectProperty causeProperty;
     final OWLObjectProperty previousOrCurrentDiseaseProperty;

     final OWLClass diseaseClass;
     final OWLClass symptomClass;
     final OWLClass causeClass;
     final OWLClass treatmentClass;
     final OWLClass testingClass;
     final OWLClass patientClass;

     OntologyProperties(OWLDataFactory factory, PrefixManager prefixManager) {
         firstNameProperty = factory.getOWLDataProperty("firstName", prefixManager);
         lastNameProperty = factory.getOWLDataProperty("lastName", prefixManager);
         ageProperty = factory.getOWLDataProperty("age", prefixManager);
         heightProperty = factory.getOWLDataProperty("height", prefixManager);
         weightProperty = factory.getOWLDataProperty("weight", prefixManager);
         symptomProperty = factory.getOWLObjectProperty("hasSymptom", prefixManager);
         diseaseProperty = factory.getOWLObjectProperty("hasDisease", prefixManager);
         testProperty = factory.getOWLObjectProperty("shouldMakeTest", prefixManager);
         negativeTestProperty = factory.getOWLObjectProperty("negativeTest", prefixManager);
         treatmentProperty = factory.getOWLObjectProperty("shouldBeTreatedWith", prefixManager);
         causeProperty = factory.getOWLObjectProperty("causeOfDisease", prefixManager);
         previousOrCurrentDiseaseProperty = factory.getOWLObjectProperty("hadOrHasDisease", prefixManager);

         diseaseClass = factory.getOWLClass("Disease", prefixManager);
         symptomClass = factory.getOWLClass("Symptom", prefixManager);
         causeClass = factory.getOWLClass("Cause", prefixManager);
         treatmentClass = factory.getOWLClass("Treatment", prefixManager);
         testingClass = factory.getOWLClass("Testing", prefixManager);
         patientClass = factory.getOWLClass("Patient", prefixManager);
     }
}
