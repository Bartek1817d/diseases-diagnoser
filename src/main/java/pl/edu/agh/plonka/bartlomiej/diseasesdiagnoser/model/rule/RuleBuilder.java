package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import com.google.common.collect.Range;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Range.*;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.*;

public class RuleBuilder {

    private Variable patientVariable;
    private Variable ageVariable;
    private Variable heightVariable;
    private Variable weightVariable;

    private String name;

    private Set<Entity> symptoms = new HashSet<>();
    private Set<Entity> negativeTests = new HashSet<>();
    private Set<Entity> previousDiseases = new HashSet<>();
    private Set<Entity> diseases = new HashSet<>();
    private Set<Entity> treatments = new HashSet<>();
    private Set<Entity> tests = new HashSet<>();


    private AbstractAtom patientDeclarationAtom;
    private AbstractAtom ageAtom;
    private AbstractAtom heightAtom;
    private AbstractAtom weightAtom;


    private Range<Integer> ageRange = Range.all();
    private Range<Integer> heightRange = Range.all();
    private Range<Integer> weightRange = Range.all();


    public RuleBuilder() {
        this.patientVariable = new Variable("patient");
        this.ageVariable = new Variable("_age");
        this.heightVariable = new Variable("_height");
        this.weightVariable = new Variable("_weight");
        this.ageAtom = new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable);
        this.heightAtom = new TwoArgumentsAtom<>(HEIGHT_PROPERTY, patientVariable, heightVariable);
        this.weightAtom = new TwoArgumentsAtom<>(WEIGHT_PROPERTY, patientVariable, weightVariable);

    }

    public RuleBuilder(Entity patientClass) {
        this.patientVariable = new Variable("patient", patientClass);
        this.ageVariable = new Variable("_age");
        this.heightVariable = new Variable("_height");
        this.weightVariable = new Variable("_weight");
        this.patientDeclarationAtom = new ClassDeclarationAtom<>(patientClass, patientVariable);
        this.ageAtom = new TwoArgumentsAtom<>(AGE_PROPERTY, patientVariable, ageVariable);
        this.heightAtom = new TwoArgumentsAtom<>(HEIGHT_PROPERTY, patientVariable, heightVariable);
        this.weightAtom = new TwoArgumentsAtom<>(WEIGHT_PROPERTY, patientVariable, weightVariable);
    }

    public RuleBuilder(Rule rule) {
        for (AbstractAtom declarationAtom : rule.getDeclarationAtoms()) {
            if (declarationAtom instanceof ClassDeclarationAtom) {
                ClassDeclarationAtom classDeclarationAtom = (ClassDeclarationAtom) declarationAtom;
                Object argument = classDeclarationAtom.getArgument();
                if (argument instanceof Variable) {
                    Variable variable = (Variable) argument;
                    if (variable.getName().equals("patient")) {
                        patientDeclarationAtom = declarationAtom;
                        patientVariable = variable;
                    }
                }
            }
            if (declarationAtom instanceof TwoArgumentsAtom) {
                TwoArgumentsAtom twoArgumentsAtom = (TwoArgumentsAtom) declarationAtom;
                String predicate = declarationAtom.predicate;
                switch (predicate) {
                    case AGE_PROPERTY:
                        ageVariable = (Variable) twoArgumentsAtom.getArgument2();
                        ageAtom = twoArgumentsAtom;
                        ageRange = alignRange(ageVariable, rule.getBodyAtoms(), ageRange);
                        break;
                    case HEIGHT_PROPERTY:
                        heightVariable = (Variable) twoArgumentsAtom.getArgument2();
                        heightAtom = twoArgumentsAtom;
                        heightRange = alignRange(heightVariable, rule.getBodyAtoms(), ageRange);
                        break;
                    case WEIGHT_PROPERTY:
                        weightVariable = (Variable) twoArgumentsAtom.getArgument2();
                        weightAtom = twoArgumentsAtom;
                        weightRange = alignRange(weightVariable, rule.getBodyAtoms(), ageRange);
                        break;
                }
            }
        }
        for (AbstractAtom bodyAtom : rule.getBodyAtoms()) {
            if (bodyAtom instanceof TwoArgumentsAtom) {
                TwoArgumentsAtom twoArgumentsAtom = (TwoArgumentsAtom) bodyAtom;
                String predicate = bodyAtom.predicate;
                switch (predicate) {
                    case HAS_SYMPTOM_PROPERTY:
                        symptoms.add((Entity) twoArgumentsAtom.getArgument2());
                        break;
                    case NEGATIVE_TEST_PROPERTY:
                        negativeTests.add((Entity) twoArgumentsAtom.getArgument2());
                        break;
                    case PREVOIUS_DISEASE_PROPERTY:
                        previousDiseases.add((Entity) twoArgumentsAtom.getArgument2());
                        break;
                }
            }
        }
        for (AbstractAtom headAtom : rule.getHeadAtoms()) {
            if (headAtom instanceof TwoArgumentsAtom) {
                TwoArgumentsAtom twoArgumentsAtom = (TwoArgumentsAtom) headAtom;
                String predicate = headAtom.predicate;
                switch (predicate) {
                    case HAS_DISEASE_PROPERTY:
                        diseases.add((Entity) twoArgumentsAtom.getArgument2());
                        break;
                    case SHOULD_BE_TREATED_WITH_PROPERTY:
                        treatments.add((Entity) twoArgumentsAtom.getArgument2());
                        break;
                    case SHOULD_MAKE_TEST_PROPERTY:
                        tests.add((Entity) twoArgumentsAtom.getArgument2());
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
        this.symptoms = singleton(symptom);
        return this;
    }

    public RuleBuilder withSymptoms(Collection<Entity> symptoms) {
        this.symptoms = new HashSet<>(symptoms);
        return this;
    }

    public Collection<Entity> getSymptoms() {
        return symptoms;
    }

    public RuleBuilder withNegativeTest(Entity negativeTest) {
        this.negativeTests = singleton(negativeTest);
        return this;
    }

    public RuleBuilder withNegativeTests(Collection<Entity> negativeTests) {
        this.negativeTests = new HashSet<>(negativeTests);
        return this;
    }

    public Collection<Entity> getNegativeTests() {
        return negativeTests;
    }

    public RuleBuilder withDisease(Entity disease) {
        this.diseases = singleton(disease);
        return this;
    }

    public RuleBuilder withDiseases(Collection<Entity> diseases) {
        this.diseases = new HashSet<>(diseases);
        return this;
    }

    public RuleBuilder withPreviousDiseases(Collection<Entity> previousDiseases) {
        this.previousDiseases = new HashSet<>(previousDiseases);
        return this;
    }

    public Collection<Entity> getDiseases() {
        return diseases;
    }

    public RuleBuilder withAge(Range<Integer> ageRange) {
        this.ageRange = this.ageRange.intersection(ageRange);
        return this;
    }

    public RuleBuilder withHeight(Range<Integer> heightRange) {
        this.heightRange = this.heightRange.intersection(heightRange);
        return this;
    }

    public RuleBuilder withWeight(Range<Integer> weightRange) {
        this.weightRange = this.weightRange.intersection(weightRange);
        return this;
    }

    public RuleBuilder withTreatments(Collection<Entity> treatments) {
        this.treatments = new HashSet<>(treatments);
        return this;
    }

    public RuleBuilder withTests(Collection<Entity> tests) {
        this.tests = new HashSet<>(tests);
        return this;
    }

    public Rule build() {
        Set<AbstractAtom> declarationAtoms = new HashSet<>();
        Set<AbstractAtom> headAtoms = new HashSet<>();
        Set<AbstractAtom> bodyAtoms = new HashSet<>();

        if (patientDeclarationAtom != null)
            declarationAtoms.add(patientDeclarationAtom);

        bodyAtoms.addAll(symptoms.stream()
                .map(symptom -> new TwoArgumentsAtom<>(HAS_SYMPTOM_PROPERTY, patientVariable, symptom))
                .collect(toSet()));
        bodyAtoms.addAll(negativeTests.stream()
                .map(negativeTest -> new TwoArgumentsAtom<>(NEGATIVE_TEST_PROPERTY, patientVariable, negativeTest))
                .collect(toSet()));
        bodyAtoms.addAll(previousDiseases.stream()
                .map(previousDisease -> new TwoArgumentsAtom<>(PREVOIUS_DISEASE_PROPERTY, patientVariable, previousDisease))
                .collect(toSet()));


        headAtoms.addAll(diseases.stream()
                .map(disease -> new TwoArgumentsAtom<>(HAS_DISEASE_PROPERTY, patientVariable, disease))
                .collect(toSet()));
        headAtoms.addAll(treatments.stream()
                .map(treatment -> new TwoArgumentsAtom<>(SHOULD_BE_TREATED_WITH_PROPERTY, patientVariable, treatment))
                .collect(toSet()));
        headAtoms.addAll(tests.stream()
                .map(test -> new TwoArgumentsAtom<>(SHOULD_MAKE_TEST_PROPERTY, patientVariable, test))
                .collect(toSet()));

        if (!isRangeUniversal(ageRange)) {
            declarationAtoms.add(ageAtom);
            bodyAtoms.addAll(getLinearAtoms(ageRange, ageVariable));
        }
        if (!isRangeUniversal(heightRange)) {
            declarationAtoms.add(heightAtom);
            bodyAtoms.addAll(getLinearAtoms(heightRange, heightVariable));
        }
        if (!isRangeUniversal(weightRange)) {
            declarationAtoms.add(weightAtom);
            bodyAtoms.addAll(getLinearAtoms(weightRange, weightVariable));
        }

        return new Rule(name, declarationAtoms, bodyAtoms, headAtoms);
    }

    private Collection<AbstractAtom> getLinearAtoms(Range<Integer> range, Variable variable) {
        Collection<AbstractAtom> ageAtoms = new ArrayList<>();
        if (range.hasLowerBound()) {
            switch (range.lowerBoundType()) {
                case OPEN:
                    ageAtoms.add(new TwoArgumentsAtom<>(GREATER_THAN_PROPERTY, SWRLB_PREFIX, variable, range.lowerEndpoint()));
                    break;
                case CLOSED:
                    ageAtoms.add(new TwoArgumentsAtom<>(GREATER_THAN_OR_EQUAL_PROPERTY, SWRLB_PREFIX, variable, range.lowerEndpoint()));
                    break;
            }
        }
        if (range.hasUpperBound()) {
            switch (range.upperBoundType()) {
                case OPEN:
                    ageAtoms.add(new TwoArgumentsAtom<>(LESS_THAN_PROPERTY, SWRLB_PREFIX, variable, range.upperEndpoint()));
                    break;
                case CLOSED:
                    ageAtoms.add(new TwoArgumentsAtom<>(LESS_THAN_OR_EQUAL_PROPERTY, SWRLB_PREFIX, variable, range.upperEndpoint()));
                    break;
            }
        }

        return ageAtoms;
    }

    private Range<Integer> alignRange(Variable variable, Collection<AbstractAtom> atoms, Range<Integer> range) {
        for (AbstractAtom atom : atoms) {
            if (atom instanceof TwoArgumentsAtom) {
                TwoArgumentsAtom twoArgumentsAtom = (TwoArgumentsAtom) atom;
                if (twoArgumentsAtom.getArgument1().equals(variable)) {
                    String predicate = twoArgumentsAtom.getPredicate();
                    int value = (int) twoArgumentsAtom.getArgument2();
                    switch (predicate) {
                        case LESS_THAN_PROPERTY:
                            range = range.intersection(lessThan(value));
                            break;
                        case LESS_THAN_OR_EQUAL_PROPERTY:
                            range = range.intersection(atMost(value));
                            break;
                        case GREATER_THAN_PROPERTY:
                            range = range.intersection(greaterThan(value));
                            break;
                        case GREATER_THAN_OR_EQUAL_PROPERTY:
                            range = range.intersection(atLeast(value));
                            break;
                        case EQUAL_PROPERTY:
                            range = range.intersection(Range.singleton(value));
                            break;
                    }
                }
            }
        }
        return range;
    }

    private boolean isRangeUniversal(Range range) {
        return !range.hasLowerBound() && !range.hasUpperBound();
    }
}
