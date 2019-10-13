package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import org.junit.Test;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singleton;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Category.Predicate.HAS_DISEASE;

public class ComplexComparatorTest {

    private static final Map<String, Entity> PATIENT_CLASS = generateEntity("Patient");
    private static final Map<String, Entity> SYMPTOMS = generateEntities("Symptom", 5);
    private static final Map<String, Entity> TESTS = generateEntities("Test", 5);
    private static final Map<String, Entity> DISEASES = generateEntities("Disease", 5);

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

    @Test
    public void test() {
        Entity symptom1 = SYMPTOMS.get("Symptom1");
        Entity symptom2 = SYMPTOMS.get("Symptom2");
        Entity symptom3 = SYMPTOMS.get("Symptom3");
        Entity disease1 = DISEASES.get("Disease1");
        Entity disease2 = DISEASES.get("Disease2");

        Patient patient1 = new Patient("patient1");
        patient1.addSymptom(symptom1);
        patient1.addDisease(disease1);

        Patient patient2 = new Patient("patient2");
        patient2.addSymptom(symptom2);
        patient2.addDisease(disease2);

        List<Patient> trainingSet = Arrays.asList(patient1, patient2);

        Category category = new Category(disease1, HAS_DISEASE);

        Complex complex1 = new Complex();
        complex1.setSymptomSelector(new NominalSelector<>(singleton(symptom1)));

        Complex complex2 = new Complex();
        complex2.setSymptomSelector(new NominalSelector<>(singleton(symptom3)));

        Star star = new Star();
        star.intersection(Arrays.asList(complex1, complex2));

        ComplexComparator.sortStar(star, category, trainingSet);
        star.leaveFirstElements(1);
        Complex complex = star.get(0);
    }
}