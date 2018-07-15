package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ComplexTest {

    private Complex complex1;
    private Complex complex2;

    @Before
    public void setUp() throws Exception {
        complex1 = new Complex();
        complex2 = new Complex();
    }

    @Test
    public void textShouldContain() {
        NominalSelector<Entity> symptomSelector = new NominalSelector<>();
        symptomSelector.add(new Entity("symptom"));
        complex2.setSymptomSelector(symptomSelector);

        assertTrue(complex2.contains(complex1));
    }

    @Test
    public void textShouldNotContain() {
        NominalSelector<Entity> symptomSelector = new NominalSelector<>();
        symptomSelector.add(new Entity("symptom"));
        complex2.setSymptomSelector(symptomSelector);

        assertFalse(complex1.contains(complex2));
    }
}