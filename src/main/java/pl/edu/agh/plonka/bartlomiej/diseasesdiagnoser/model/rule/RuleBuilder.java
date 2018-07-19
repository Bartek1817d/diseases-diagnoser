package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;

import java.util.Collection;

import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.*;

public class RuleBuilder {

    private Rule rule;
    private Variable patientVariable;
    private Variable ageVariable;

    public RuleBuilder() {
        this.rule = new Rule();
        this.patientVariable = new Variable("patient");
        this.ageVariable = new Variable("_age");
    }

    public RuleBuilder(Entity patientClass) {
        this.rule = new Rule();
        this.patientVariable = new Variable("patient", patientClass);
        this.ageVariable = new Variable("_age");
        this.rule.addBodyAtom(new ClassDeclarationAtom<>(patientClass, patientVariable));
    }

    public RuleBuilder withSymptom(Entity symptom) {
        rule.addBodyAtom(new TwoArgumentsAtom<>(HAS_SYMPTOM_PROPERTY, patientVariable, symptom));
        return this;
    }

    public RuleBuilder withSymptoms(Collection<Entity> symptoms) {
        symptoms.forEach(this::withSymptom);
        return this;
    }

    public RuleBuilder withNegativeTest(Entity negativeTest) {
        rule.addBodyAtom(new TwoArgumentsAtom<>(NEGATIVE_TEST_PROPERTY, patientVariable, negativeTest));
        return this;
    }

    public RuleBuilder withNegativeTests(Collection<Entity> negativeTests) {
        negativeTests.forEach(this::withNegativeTest);
        return this;
    }

    public RuleBuilder withDisease(Entity disease) {
        rule.addHeadAtom(new TwoArgumentsAtom<>(HAS_DISEASE_PROPERTY, patientVariable, disease));
        return this;
    }

    public RuleBuilder withDiseases(Collection<Entity> diseases) {
        diseases.forEach(this::withDisease);
        return this;
    }

    public RuleBuilder withAgeGreaterThan(Integer age) {
        rule.addBodyAtom(new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable));
        rule.addBodyAtom(new TwoArgumentsAtom<>(GREATER_THAN_PROPERTY, SWRLB_PREFIX, ageVariable, age));
        return this;
    }

    public RuleBuilder withAgeGreaterThanOrEqual(Integer age) {
        rule.addBodyAtom(new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable));
        rule.addBodyAtom(new TwoArgumentsAtom<>(GREATER_THAN_OR_EQUAL_PROPERTY, SWRLB_PREFIX, ageVariable, age));
        return this;
    }

    public RuleBuilder withAgeLessThan(Integer age) {
        rule.addBodyAtom(new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable));
        rule.addBodyAtom(new TwoArgumentsAtom<>(LESS_THAN_PROPERTY, SWRLB_PREFIX, ageVariable, age));
        return this;
    }

    public RuleBuilder withAgeLessThanOrEqual(Integer age) {
        rule.addBodyAtom(new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable));
        rule.addBodyAtom(new TwoArgumentsAtom<>(LESS_THAN_OR_EQUAL_PROPERTY, SWRLB_PREFIX, ageVariable, age));
        return this;
    }

    public Rule build() {
        return rule;
    }
}
