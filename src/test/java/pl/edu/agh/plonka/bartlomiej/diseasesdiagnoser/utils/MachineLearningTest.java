package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.PartialStarCreationException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.ontology.OntologyWrapper;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MachineLearningTest {

    private static final Map<String, Entity> PATIENT_CLASS = generateEntity("Patient");
    private static final Map<String, Entity> SYMPTOMS = generateEntities("Symptom", 5);
    private static final Map<String, Entity> TESTS = generateEntities("Test", 5);
    private static final Map<String, Entity> DISEASES = generateEntities("Disease", 5);
    private MachineLearning machineLearning;
    private OntologyWrapper ontologyWrapper;

    private static Map<String, Entity> generateEntities(String baseId, Integer count) {
        Map<String, Entity> entities = new HashMap<>();
        for (int i = 0; i < count; i++) {
            String id = baseId + i;
            entities.put(id, new Entity(id));
        }
        return entities;
    }

    private static Map<String, Entity> generateEntity(String id) {
        Map<String, Entity> entities = new HashMap<>();
        entities.put(id, new Entity(id));

        return entities;
    }

    @Before
    public void setUp() {
        ontologyWrapper = mock(OntologyWrapper.class);
        when(ontologyWrapper.getClasses()).thenReturn(PATIENT_CLASS);
        when(ontologyWrapper.getSymptoms()).thenReturn(SYMPTOMS);
        when(ontologyWrapper.getTests()).thenReturn(TESTS);
        when(ontologyWrapper.getDiseases()).thenReturn(DISEASES);
        machineLearning = new MachineLearning(ontologyWrapper);
    }

    @Test
    public void testSequentialCoveringGenerateUniversalRule() {
        Patient patient = createPatient("Patient", SYMPTOMS.values(), DISEASES.get("Disease1"));

        Collection<Rule> rules = machineLearning.sequentialCovering(Collections.singleton(patient));

        Assert.assertNotNull(rules);
        Assert.assertEquals(1, rules.size());
        Rule rule = rules.iterator().next();
        Assert.assertEquals(1, rule.getBodyAtoms().size());
    }

    @Test(expected = PartialStarCreationException.class)
    public void testSequentialCoveringCreatePartialStarException() {
        Patient patient1 = createPatient("Patient1", SYMPTOMS.values(), DISEASES.get("Disease1"));
        Patient patient2 = createPatient("Patient2", SYMPTOMS.values(), DISEASES.get("Disease2"));

        machineLearning.sequentialCovering(new HashSet<>(Arrays.asList(patient1, patient2)));
    }

    private Patient createPatient(String id, Collection<Entity> symptoms, Entity disease) {
        Patient patient = new Patient(id);
        patient.addSymptoms(symptoms);
        patient.addDisease(disease);

        return patient;
    }
}