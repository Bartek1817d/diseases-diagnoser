package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.ontology.OntologyWrapper;

import java.util.*;
import java.util.stream.Collectors;

import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.*;

public class Complex implements Comparable<Complex> {

    private static final Logger LOG = LoggerFactory.getLogger(Complex.class);

    private NominalSelector<Entity> previousDiseasesSelector;
    private NominalSelector<Entity> symptomSelector;
    private NominalSelector<Entity> negativeTestsSelector;
    private LinearSelector<Integer> ageSelector;
    private LinearSelector<Integer> heightSelector;
    private LinearSelector<Integer> weightSelector;

    private Integer evaluation1;
    private Integer evaluation2;
    private Integer evaluation3;
    private Integer evaluation4;

    @SuppressWarnings("unchecked")
    public static Complex conjunction(Complex complex1, Complex complex2) {
        Complex resultComplex = new Complex();
        resultComplex.previousDiseasesSelector = (NominalSelector<Entity>) setSelector(
                complex1.previousDiseasesSelector, complex2.previousDiseasesSelector);
        resultComplex.symptomSelector = (NominalSelector<Entity>) setSelector(complex1.symptomSelector,
                complex2.symptomSelector);
        resultComplex.negativeTestsSelector = (NominalSelector<Entity>) setSelector(complex1.negativeTestsSelector,
                complex2.negativeTestsSelector);
        resultComplex.ageSelector = (LinearSelector<Integer>) setSelector(complex1.ageSelector, complex2.ageSelector);
        resultComplex.heightSelector = (LinearSelector<Integer>) setSelector(complex1.heightSelector, complex2.heightSelector);
        resultComplex.weightSelector = (LinearSelector<Integer>) setSelector(complex1.weightSelector, complex2.weightSelector);

        return resultComplex;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Selector setSelector(Selector selector1, Selector selector2) {
        if (selector1 != null && selector2 != null)
            return selector1.conjunction(selector2);
        if (selector1 != null)
            return selector1;
        if (selector2 != null)
            return selector2;
        return null;
    }

    public static Collection<Complex> intersection(Collection<Complex> complexes1, Collection<Complex> complexes2) {
        List<Complex> resultComplexes = new LinkedList<>();
        for (Complex complex1 : complexes1)
            for (Complex complex2 : complexes2)
                resultComplexes.add(Complex.conjunction(complex1, complex2));
        return resultComplexes;
    }

    public NominalSelector<Entity> getPreviousDiseasesSelector() {
        return previousDiseasesSelector;
    }

    public void setPreviousDiseasesSelector(NominalSelector<Entity> previousDiseasesSelector) {
        this.previousDiseasesSelector = previousDiseasesSelector;
    }

    public NominalSelector<Entity> getSymptomSelector() {
        return symptomSelector;
    }

    public void setSymptomSelector(NominalSelector<Entity> symptomSelector) {
        this.symptomSelector = symptomSelector;
    }

    public LinearSelector<Integer> getAgeSelector() {
        return ageSelector;
    }

    public void setAgeSelector(LinearSelector<Integer> ageSelector) {
        this.ageSelector = ageSelector;
    }

    public NominalSelector<Entity> getNegativeTestsSelector() {
        return negativeTestsSelector;
    }

    public void setNegativeTestsSelector(NominalSelector<Entity> negativeTestsSelector) {
        this.negativeTestsSelector = negativeTestsSelector;
    }

    public LinearSelector<Integer> getHeightSelector() {
        return heightSelector;
    }

    public void setHeightSelector(LinearSelector<Integer> heightSelector) {
        this.heightSelector = heightSelector;
    }

    public LinearSelector<Integer> getWeightSelector() {
        return weightSelector;
    }

    public void setWeightSelector(LinearSelector<Integer> weightSelector) {
        this.weightSelector = weightSelector;
    }

    public Integer getEvaluation1() {
        return evaluation1;
    }

    public void setEvaluation1(Integer evaluation1) {
        this.evaluation1 = evaluation1;
    }

    public Integer getEvaluation2() {
        return evaluation2;
    }

    public void setEvaluation2(Integer evaluation2) {
        this.evaluation2 = evaluation2;
    }

    public Integer getEvaluation3() {
        return evaluation3;
    }

    public void setEvaluation3(Integer evaluation3) {
        this.evaluation3 = evaluation3;
    }

    public Integer getEvaluation4() {
        return evaluation4;
    }

    public void setEvaluation4(Integer evaluation4) {
        this.evaluation4 = evaluation4;
    }

    public boolean contains(Complex complex) {
        if (!contains(symptomSelector, complex.symptomSelector))
            return false;
        if (!contains(negativeTestsSelector, complex.negativeTestsSelector))
            return false;
        if (!contains(previousDiseasesSelector, complex.previousDiseasesSelector))
            return false;
        if (!contains(ageSelector, complex.ageSelector))
            return false;
        if (!contains(heightSelector, complex.heightSelector))
            return false;
        if (!contains(weightSelector, complex.weightSelector))
            return false;
        return true;
    }

    private boolean contains(Selector selector1, Selector selector2) {
        if (selector1 != null) {
            return selector1.contains(selector2);
        } else {
            return true;
        }
    }

    public boolean isPatientCovered(Patient patient) {
        if (!covers(symptomSelector, patient.getSymptoms())) {
            return false;
        }
        if (!covers(negativeTestsSelector, patient.getNegativeTests())) {
            return false;
        }
        if (!covers(previousDiseasesSelector, patient.getPreviousAndCurrentDiseases())) {
            return false;
        }
        if (!covers(ageSelector, patient.getAge())) {
            return false;
        }
        if (!covers(heightSelector, patient.getHeight())) {
            return false;
        }
        if (!covers(weightSelector, patient.getWeight())) {
            return false;
        }
        return true;
    }

    private boolean covers(Selector<Entity> selector, Collection<Entity> entities) {
        return selector == null || selector.covers(entities);
    }

    private boolean covers(Selector<Integer> selector, Integer entity) {
        return selector == null || selector.covers(entity);
    }

    public Rule generateRule(String ruleName, Concepts concepts, OntologyWrapper ontology) {
        Rule rule = new Rule(ruleName);
        Variable patientVariable = new Variable("patient", ontology.getClasses().get(PATIENT_CLASS));
        Variable ageVariable = new Variable("_age");
        Variable heightVariable = new Variable("_height");
        Variable weightVariable = new Variable("_weight");

        rule.addBodyAtom(new ClassDeclarationAtom<>(ontology.getClasses().get(PATIENT_CLASS), patientVariable));

        rule.addBodyAtoms(createLinearAtoms(ageVariable, patientVariable, AGE_PROPERTY, ageSelector));
        rule.addBodyAtoms(createLinearAtoms(heightVariable, patientVariable, HEIGHT_PROPERTY, heightSelector));
        rule.addBodyAtoms(createLinearAtoms(weightVariable, patientVariable, WEIGHT_PROPERTY, weightSelector));
        rule.addBodyAtoms(createEntityAtoms(patientVariable, HAS_SYMPTOM_PROPERTY, symptomSelector));
        rule.addBodyAtoms(createEntityAtoms(patientVariable, PREVIOUS_DISEASE_PROPERTY, previousDiseasesSelector));
        rule.addBodyAtoms(createEntityAtoms(patientVariable, NEGATIVE_TEST_PROPERTY, negativeTestsSelector));

        rule.addHeadAtoms(createEntityAtoms(patientVariable, HAS_DISEASE_PROPERTY, concepts.diseases));
        rule.addHeadAtoms(createEntityAtoms(patientVariable, SHOULD_MAKE_TEST_PROPERTY, concepts.tests));
        rule.addHeadAtoms(createEntityAtoms(patientVariable, SHOULD_BE_TREATED_WITH_PROPERTY, concepts.treatments));

        return rule;
    }

    private static Collection<AbstractAtom> createEntityAtoms(Variable variable, String predicate, Collection<Entity> entities) {
        if (entities == null)
            return Collections.emptyList();
        else
            return entities.stream().map(e -> new TwoArgumentsAtom<>(predicate, variable, e)).collect(Collectors.toList());
    }

    private static Collection<AbstractAtom> createLinearAtoms(Variable linearVariable, Variable patientVariable, String propertyName,
                                                       LinearSelector<Integer> linearSelector) {
        if (linearSelector == null)
            return Collections.emptyList();

        Collection<AbstractAtom> atoms = new ArrayList<>();
        atoms.add(new TwoArgumentsAtom<>(propertyName, patientVariable, linearVariable));
        atoms.addAll(createLinearAtoms(linearVariable, linearSelector));

        return atoms;
    }

    private static Collection<AbstractAtom> createLinearAtoms(Variable linearVariable, LinearSelector<Integer> linearSelector) {
        ArrayList<AbstractAtom> atoms = new ArrayList<>();
        if (linearSelector.hasLowerBound() && linearSelector.hasUpperBound()
                && linearSelector.lowerEndpoint().equals(linearSelector.upperEndpoint())) {
            TwoArgumentsAtom<Variable, Integer> equalAtom = new TwoArgumentsAtom<>(
                    EQUAL_PROPERTY, SWRLB_PREFIX, linearVariable, linearSelector.lowerEndpoint());
            atoms.add(equalAtom);
        } else {
            if (linearSelector.hasLowerBound()) {
                switch (linearSelector.lowerBoundType()) {
                    case OPEN:
                        TwoArgumentsAtom<Variable, Integer> greaterThanAtom = new TwoArgumentsAtom<>(
                                GREATER_THAN_PROPERTY, SWRLB_PREFIX, linearVariable, linearSelector.lowerEndpoint());
                        atoms.add(greaterThanAtom);
                        break;
                    case CLOSED:
                        TwoArgumentsAtom<Variable, Integer> atLeastAtom = new TwoArgumentsAtom<>(
                                GREATER_THAN_OR_EQUAL_PROPERTY, SWRLB_PREFIX, linearVariable, linearSelector.lowerEndpoint());
                        atoms.add(atLeastAtom);
                        break;
                }
            }
            if (linearSelector.hasUpperBound()) {
                switch (linearSelector.upperBoundType()) {
                    case OPEN:
                        TwoArgumentsAtom<Variable, Integer> lessThanAtom = new TwoArgumentsAtom<>(
                                LESS_THAN_PROPERTY, SWRLB_PREFIX, linearVariable, linearSelector.upperEndpoint());
                        atoms.add(lessThanAtom);
                        break;
                    case CLOSED:
                        TwoArgumentsAtom<Variable, Integer> atMostAtom = new TwoArgumentsAtom<>(
                                LESS_THAN_OR_EQUAL_PROPERTY, SWRLB_PREFIX, linearVariable, linearSelector.upperEndpoint());
                        atoms.add(atMostAtom);
                        break;
                }
            }
        }
        return atoms;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Symptoms: \n\t");
        str.append(symptomSelector);
        str.append("\nPrevious or current diseases: \n\t");
        str.append(previousDiseasesSelector);
        str.append("\nNegative tests: \n\t");
        str.append(negativeTestsSelector);
        str.append("\nAge: ");
        str.append(ageSelector);
        str.append("\nHeight: ");
        str.append(heightSelector);
        str.append("\nWeight: ");
        str.append(weightSelector);
        return str.toString();
    }

    @Override
    public int compareTo(Complex o) {

        return 0;
    }

}
