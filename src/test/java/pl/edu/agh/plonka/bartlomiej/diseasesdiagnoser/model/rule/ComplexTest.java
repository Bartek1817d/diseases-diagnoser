package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ComplexTest {

    private Complex complex1;
    private Complex complex2;
    private Patient patient;

    private Entity symptom1;
    private Entity symptom2;


    @Before
    public void setUp() throws Exception {
        complex1 = new Complex();
        complex2 = new Complex();
        patient = new Patient();
        symptom1 = new Entity("symptom1");
        symptom2 = new Entity("symptom2");
    }

    @Test
    public void testShouldNotContain() {
        EntitiesSelector<Entity> symptomSelector = createNominalSelector(symptom1);
        complex2.setSymptomSelector(symptomSelector);

        assertFalse(complex2.contains(complex1));
    }

    @Test
    public void testShouldContain() {
        EntitiesSelector<Entity> symptomSelector = createNominalSelector(symptom1);
        complex2.setSymptomSelector(symptomSelector);

        assertTrue(complex1.contains(complex2));
    }

    @Test
    public void testPatientIsCovered() {
        EntitiesSelector<Entity> symptomSelector = createNominalSelector(symptom1);
        complex1.setSymptomSelector(symptomSelector);
        patient.addSymptom(symptom1);

        assertTrue(complex1.isPatientCovered(patient));
    }

    @Test
    public void testPatientIsNotCovered() {
        EntitiesSelector<Entity> symptomSelector = createNominalSelector(Arrays.asList(symptom1, symptom2));
        complex1.setSymptomSelector(symptomSelector);
        patient.addSymptom(symptom1);

        assertFalse(complex1.isPatientCovered(patient));
    }

    private <T> EntitiesSelector<T> createNominalSelector(T entity) {
        EntitiesSelector<T> selector = new EntitiesSelector<>();
        selector.add(entity);

        return selector;
    }

    private <T> EntitiesSelector<T> createNominalSelector(Collection<T> entities) {
        EntitiesSelector<T> selector = new EntitiesSelector<>();
        selector.addAll(entities);

        return selector;
    }
}