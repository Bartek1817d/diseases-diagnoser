package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;

import static com.google.common.collect.Range.greaterThan;
import static com.google.common.collect.Range.lessThan;

public class RuleBuilderTest {

    private final Entity patientClass = new Entity("Patient");
    private final Entity symptom = new Entity("Symptom");
    private final Entity disease = new Entity("Disease");

    private RuleBuilder ruleBuilder;

    @Before
    public void setUp() {
        ruleBuilder = new RuleBuilder(patientClass);
    }

    @Test
    public void test() {
        Rule rule = ruleBuilder
                .withSymptom(symptom)
                .withDisease(disease)
                .withAge(greaterThan(10))
                .withAge(lessThan(20))
                .build();

        System.out.println(rule);

        ruleBuilder = new RuleBuilder(rule);

        System.out.println(ruleBuilder.build());
    }
}