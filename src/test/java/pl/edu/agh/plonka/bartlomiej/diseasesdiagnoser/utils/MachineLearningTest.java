package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.PartialStarCreationException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.ontology.OntologyWrapper;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.MachineLearning;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
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

        Collection<Rule> rules = machineLearning.sequentialCovering(singleton(patient));

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

    @Test
    public void testSequentialCoveringCreateDifferentRules() {
        Patient patient1 = createPatient("Patient1", singleton(SYMPTOMS.get("Symptom1")), DISEASES.get("Disease1"));
        Patient patient2 = createPatient("Patient2", singleton(SYMPTOMS.get("Symptom2")), DISEASES.get("Disease2"));
        Patient patient3 = createPatient("Patient3", Arrays.asList(SYMPTOMS.get("Symptom2"), SYMPTOMS.get("Symptom3")), DISEASES.get("Disease2"));

        Collection<Rule> rules = machineLearning.sequentialCovering(new HashSet<>(Arrays.asList(patient1, patient2, patient3)));
        ArrayList<Rule> ruleList = new ArrayList<>(rules);

        Assert.assertEquals(2, ruleList.size());
    }

    @Test
    public void testSequentialCoveringCreateRulesBasedOnAge() {
        Patient patient1 = createPatient("Patient1", 10, emptyList(), DISEASES.get("Disease1"));
        Patient patient2 = createPatient("Patient2", 20, emptyList(), DISEASES.get("Disease2"));

        Collection<Rule> rules = machineLearning.sequentialCovering(new HashSet<>(Arrays.asList(patient1, patient2)));
        ArrayList<Rule> ruleList = new ArrayList<>(rules);

        Assert.assertEquals(2, ruleList.size());
    }

    private Patient createPatient(String id, Collection<Entity> symptoms, Entity disease) {
        return createPatient(id, null, symptoms, disease);
    }

    private Patient createPatient(String id, Integer age, Collection<Entity> symptoms, Entity disease) {
        Patient patient = new Patient(id);
        if (age != null)
            patient.setAge(age);
        patient.addSymptoms(symptoms);
        patient.addDisease(disease);

        return patient;
    }
}