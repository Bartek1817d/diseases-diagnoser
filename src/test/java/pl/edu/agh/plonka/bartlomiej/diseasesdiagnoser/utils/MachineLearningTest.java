package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.ontology.OntologyWrapper;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MachineLearningTest {

    private MachineLearning machineLearning;
    private OntologyWrapper ontologyWrapper;

    @Before
    public void setUp() {
        ontologyWrapper = mock(OntologyWrapper.class);
        when(ontologyWrapper.getClasses()).thenReturn(generateEntity("Patient"));
        when(ontologyWrapper.getSymptoms()).thenReturn(generateEntities("Symptom", 5));
        when(ontologyWrapper.getTests()).thenReturn(generateEntities("Test", 5));
        when(ontologyWrapper.getDiseases()).thenReturn(generateEntities("Disease", 5));
        machineLearning = new MachineLearning(ontologyWrapper);
    }

    @Test
    public void testSequentialCoveringGenerateUniversalRule() {
        Patient patient = createPatient();

        Collection<Rule> rules = machineLearning.sequentialCovering(Collections.singleton(patient));

        Assert.assertNotNull(rules);
        Assert.assertEquals(1, rules.size());
        Rule rule = rules.iterator().next();
        Assert.assertEquals(1, rule.getBodyAtoms().size());
    }

    private Map<String, Entity> generateEntities(String baseId, Integer count) {
        Map<String, Entity> entities = new HashMap<>();
        for (int i = 0; i < count; i++) {
            String id = baseId + i;
            entities.put(id, new Entity(id));
        }
        return entities;
    }

    private Map<String, Entity> generateEntity(String id) {
        Map<String, Entity> entities = new HashMap<>();
        entities.put(id, new Entity(id));

        return entities;
    }

    private Patient createPatient() {
        Patient patient = new Patient("Bartek");
        patient.addSymptom(new Entity("Symptom1"));
        patient.addDisease(new Entity("Disease1"));

        return patient;
    }
}