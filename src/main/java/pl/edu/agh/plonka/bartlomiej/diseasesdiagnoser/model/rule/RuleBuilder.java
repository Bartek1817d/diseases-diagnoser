package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.*;

public class RuleBuilder {

    private Variable patientVariable;
    private Variable ageVariable;

    private String name;
    private Set<AbstractAtom> headAtoms = new HashSet<>();
    private Set<AbstractAtom> bodyAtoms = new HashSet<>();


    public RuleBuilder() {
        this.patientVariable = new Variable("patient");
        this.ageVariable = new Variable("_age");
    }

    public RuleBuilder(Entity patientClass) {
        this.patientVariable = new Variable("patient", patientClass);
        this.ageVariable = new Variable("_age");
        this.bodyAtoms.add(new ClassDeclarationAtom<>(patientClass, patientVariable));
    }

    public RuleBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public RuleBuilder withSymptom(Entity symptom) {
        bodyAtoms.add(new TwoArgumentsAtom<>(HAS_SYMPTOM_PROPERTY, patientVariable, symptom));
        return this;
    }

    public RuleBuilder withSymptoms(Collection<Entity> symptoms) {
        symptoms.forEach(this::withSymptom);
        return this;
    }

    public RuleBuilder withNegativeTest(Entity negativeTest) {
        bodyAtoms.add(new TwoArgumentsAtom<>(NEGATIVE_TEST_PROPERTY, patientVariable, negativeTest));
        return this;
    }

    public RuleBuilder withNegativeTests(Collection<Entity> negativeTests) {
        negativeTests.forEach(this::withNegativeTest);
        return this;
    }

    public RuleBuilder withDisease(Entity disease) {
        headAtoms.add(new TwoArgumentsAtom<>(HAS_DISEASE_PROPERTY, patientVariable, disease));
        return this;
    }

    public RuleBuilder withDiseases(Collection<Entity> diseases) {
        diseases.forEach(this::withDisease);
        return this;
    }

    public RuleBuilder withAgeGreaterThan(Integer age) {
        bodyAtoms.add(new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable));
        bodyAtoms.add(new TwoArgumentsAtom<>(GREATER_THAN_PROPERTY, SWRLB_PREFIX, ageVariable, age));
        return this;
    }

    public RuleBuilder withAgeGreaterThanOrEqual(Integer age) {
        bodyAtoms.add(new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable));
        bodyAtoms.add(new TwoArgumentsAtom<>(GREATER_THAN_OR_EQUAL_PROPERTY, SWRLB_PREFIX, ageVariable, age));
        return this;
    }

    public RuleBuilder withAgeLessThan(Integer age) {
        bodyAtoms.add(new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable));
        bodyAtoms.add(new TwoArgumentsAtom<>(LESS_THAN_PROPERTY, SWRLB_PREFIX, ageVariable, age));
        return this;
    }

    public RuleBuilder withAgeLessThanOrEqual(Integer age) {
        bodyAtoms.add(new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable));
        bodyAtoms.add(new TwoArgumentsAtom<>(LESS_THAN_OR_EQUAL_PROPERTY, SWRLB_PREFIX, ageVariable, age));
        return this;
    }

    public Rule build() {
        return new Rule(name, bodyAtoms, headAtoms);
    }
}
