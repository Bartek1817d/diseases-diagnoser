package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.*;

public class RuleBuilder {

    private Variable patientVariable;
    private Variable ageVariable;

    private String name;

    private Set<Entity> symptoms = new HashSet<>();
    private Set<Entity> negativeTests = new HashSet<>();
    private Set<Entity> diseases = new HashSet<>();

    private AbstractAtom patientDeclarationAtom;
    private AbstractAtom ageAtom;

    private AbstractAtom ageEqual;
    private AbstractAtom ageGreaterThan;
    private AbstractAtom ageGreaterThanOrEqual;
    private AbstractAtom ageLessThan;
    private AbstractAtom ageLessThanOrEqual;

    public RuleBuilder() {
        this.patientVariable = new Variable("patient");
        this.ageVariable = new Variable("_age");
        this.ageAtom = new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable);
    }

    public RuleBuilder(Entity patientClass) {
        this.patientVariable = new Variable("patient", patientClass);
        this.ageVariable = new Variable("_age");
        this.patientDeclarationAtom = new ClassDeclarationAtom<>(patientClass, patientVariable);
        this.ageAtom = new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable);
    }

    public RuleBuilder(Rule rule) {
        Collection<AbstractAtom> bodyAtoms = rule.getBodyAtoms();
        Collection<AbstractAtom> headAtoms = rule.getHeadAtoms();
        for (AbstractAtom bodyAtom : bodyAtoms) {
            if (bodyAtom instanceof ClassDeclarationAtom) {
                ClassDeclarationAtom classDeclarationAtom = (ClassDeclarationAtom) bodyAtom;
                Object argument = classDeclarationAtom.getArgument();
                if (argument instanceof Variable) {
                    Variable variable = (Variable) argument;
                    if (variable.getName().equals("patient")) {
                        patientDeclarationAtom = bodyAtom;
                        patientVariable = variable;
                    }
                }
            }
            if (bodyAtom instanceof TwoArgumentsAtom) {
                TwoArgumentsAtom twoArgumentsAtom = (TwoArgumentsAtom) bodyAtom;
                String predicate = bodyAtom.predicate;
                switch (predicate) {
                    case AGE_PROPERTY:
                        ageVariable = (Variable) twoArgumentsAtom.getArgument2();
                        ageAtom = twoArgumentsAtom;
                        break;
                    case HAS_SYMPTOM_PROPERTY:
                        symptoms.add((Entity) twoArgumentsAtom.getArgument2());
                        break;
                    case NEGATIVE_TEST_PROPERTY:
                        negativeTests.add((Entity) twoArgumentsAtom.getArgument2());
                        break;
                }
            }
        }
        for (AbstractAtom headAtom : headAtoms) {
            if (headAtom instanceof TwoArgumentsAtom) {
                TwoArgumentsAtom twoArgumentsAtom = (TwoArgumentsAtom) headAtom;
                String predicate = headAtom.predicate;
                switch (predicate) {
                    case HAS_DISEASE_PROPERTY:
                        diseases.add((Entity) twoArgumentsAtom.getArgument2());
                        break;
                }
            }
        }
    }

    public RuleBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public RuleBuilder withSymptom(Entity symptom) {
//        this.symptoms = singleton(new TwoArgumentsAtom<>(HAS_SYMPTOM_PROPERTY, patientVariable, symptom));
        this.symptoms = singleton(symptom);
        return this;
    }

    public RuleBuilder withSymptoms(Collection<Entity> symptoms) {
//        this.symptoms = symptoms
//                .stream()
//                .map(symptom -> new TwoArgumentsAtom<>(HAS_SYMPTOM_PROPERTY, patientVariable, symptom))
//                .collect(toSet());
        this.symptoms = new HashSet<>(symptoms);
        return this;
    }

    public Collection<Entity> getSymptoms() {
        return symptoms;
    }

    public RuleBuilder withNegativeTest(Entity negativeTest) {
//        this.negativeTests = singleton(new TwoArgumentsAtom<>(NEGATIVE_TEST_PROPERTY, patientVariable, negativeTest));
        this.negativeTests = singleton(negativeTest);
        return this;
    }

    public RuleBuilder withNegativeTests(Collection<Entity> negativeTests) {
//        this.negativeTests = negativeTests
//                .stream()
//                .map(negativeTest -> new TwoArgumentsAtom<>(NEGATIVE_TEST_PROPERTY, patientVariable, negativeTest))
//                .collect(toSet());
        this.negativeTests = new HashSet<>(negativeTests);
        return this;
    }

    public Collection<Entity> getNegativeTests() {
        return negativeTests;
    }

    public RuleBuilder withDisease(Entity disease) {
//        this.diseases = singleton(new TwoArgumentsAtom<>(HAS_DISEASE_PROPERTY, patientVariable, disease));
        this.diseases = singleton(disease);
        return this;
    }

    public RuleBuilder withDiseases(Collection<Entity> diseases) {
//        this.diseases = diseases
//                .stream()
//                .map(disease -> new TwoArgumentsAtom<>(HAS_DISEASE_PROPERTY, patientVariable, disease))
//                .collect(toSet());
        this.diseases = new HashSet<>(diseases);
        return this;
    }

    public Collection<Entity> getDiseases() {
        return diseases;
    }

    public RuleBuilder withEqual(NumberProperty property, Integer value) {
//        switch
//        ageEqual = new TwoArgumentsAtom<>(EQUAL_PROPERTY, SWRLB_PREFIX, ageVariable, age);
        return this;
    }

    public RuleBuilder withAgeGreaterThan(Integer age) {
        ageGreaterThan = new TwoArgumentsAtom<>(GREATER_THAN_PROPERTY, SWRLB_PREFIX, ageVariable, age);
        return this;
    }

    public RuleBuilder withAgeGreaterThanOrEqual(Integer age) {
        ageGreaterThanOrEqual = new TwoArgumentsAtom<>(GREATER_THAN_OR_EQUAL_PROPERTY, SWRLB_PREFIX, ageVariable, age);
        return this;
    }

    public RuleBuilder withAgeLessThan(Integer age) {
        ageLessThan = new TwoArgumentsAtom<>(LESS_THAN_PROPERTY, SWRLB_PREFIX, ageVariable, age);
        return this;
    }

    public RuleBuilder withAgeLessThanOrEqual(Integer age) {
        ageLessThanOrEqual = new TwoArgumentsAtom<>(LESS_THAN_OR_EQUAL_PROPERTY, SWRLB_PREFIX, ageVariable, age);
        return this;
    }

    public Rule build() {
        Set<AbstractAtom> headAtoms = new HashSet<>();
        Set<AbstractAtom> bodyAtoms = new HashSet<>();

        if (patientDeclarationAtom != null)
            bodyAtoms.add(patientDeclarationAtom);
        bodyAtoms.addAll(symptoms.stream()
                .map(symptom -> new TwoArgumentsAtom<>(HAS_SYMPTOM_PROPERTY, patientVariable, symptom))
                .collect(toSet()));
        bodyAtoms.addAll(negativeTests.stream()
                .map(negativeTest -> new TwoArgumentsAtom<>(NEGATIVE_TEST_PROPERTY, patientVariable, negativeTest))
                .collect(toSet()));
        headAtoms.addAll(diseases.stream()
                .map(disease -> new TwoArgumentsAtom<>(HAS_DISEASE_PROPERTY, patientVariable, disease))
                .collect(toSet()));
        if (ageEqual != null) {
            bodyAtoms.add(ageEqual);
            bodyAtoms.add(ageAtom);
        }
        if (ageGreaterThan != null) {
            bodyAtoms.add(ageGreaterThan);
            bodyAtoms.add(ageAtom);
        }
        if (ageGreaterThanOrEqual != null) {
            bodyAtoms.add(ageGreaterThanOrEqual);
            bodyAtoms.add(ageAtom);
        }
        if (ageLessThan != null) {
            bodyAtoms.add(ageLessThan);
            bodyAtoms.add(ageAtom);
        }
        if (ageLessThanOrEqual != null) {
            bodyAtoms.add(ageLessThanOrEqual);
            bodyAtoms.add(ageAtom);
        }
        return new Rule(name, bodyAtoms, headAtoms);
    }

    public enum NumberProperty {
        AGE,
        HEIGHT,
        WEIGHT
    }
}
