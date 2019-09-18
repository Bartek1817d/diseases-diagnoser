package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service;

import org.junit.BeforeClass;
import org.junit.Test;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.ontology.OntologyWrapper;

import java.util.HashSet;

public class MachineLearningTest {

    private static OntologyWrapper ontology;
    private static MachineLearning machineLearning;

    @BeforeClass
    public static void setUp() throws Exception {
        ontology = new OntologyWrapper(MachineLearningTest.class
                .getClassLoader().getResourceAsStream("human_diseases.owl"));
        machineLearning = new MachineLearning(ontology);
    }

    @Test
    public void sequentialCovering() throws Exception {
        machineLearning.sequentialCovering(new HashSet<>(ontology.getPatients()));
    }
}