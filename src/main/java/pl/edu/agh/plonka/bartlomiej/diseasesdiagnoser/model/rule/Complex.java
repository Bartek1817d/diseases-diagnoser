package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.OntologyWrapper;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;

import java.util.*;

public class Complex implements Comparable<Complex> {
    private NominalSelector<Entity> previousDiseasesSelector;
    private NominalSelector<Entity> symptomSelector;
    private LinearSelector<Integer> ageSelector;
    private NominalSelector<Entity> negativeTestsSelector;

    private Integer evaluation1;
    private Integer evaluation2;
    private Integer evaluation3;
    private Integer evaluation4;

    @SuppressWarnings("unchecked")
    public static Complex conjuction(Complex complex1, Complex complex2) {
        Complex resultComplex = new Complex();
        resultComplex.previousDiseasesSelector = (NominalSelector<Entity>) setSelector(
                complex1.previousDiseasesSelector, complex2.previousDiseasesSelector);
        resultComplex.symptomSelector = (NominalSelector<Entity>) setSelector(complex1.symptomSelector,
                complex2.symptomSelector);
        resultComplex.ageSelector = (LinearSelector<Integer>) setSelector(complex1.ageSelector, complex2.ageSelector);
        resultComplex.negativeTestsSelector = (NominalSelector<Entity>) setSelector(complex1.negativeTestsSelector,
                complex2.negativeTestsSelector);
        return resultComplex;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Selector setSelector(Selector selector1, Selector selector2) {
        if (selector1 != null && selector2 != null)
            return selector1.conjuction(selector2);
        if (selector1 != null)
            return selector1;
        if (selector2 != null)
            return selector2;
        return null;
    }

    public static Collection<Complex> intersection(Collection<Complex> complexes1, Collection<Complex> ccomplexes2) {
        List<Complex> resultComplexes = new LinkedList<Complex>();
        for (Complex complex1 : complexes1)
            for (Complex complex2 : ccomplexes2)
                resultComplexes.add(Complex.conjuction(complex1, complex2));
        return resultComplexes;
    }

    public static void main(String args[]) {
        Collection<Integer> c1 = new LinkedList<Integer>(Arrays.asList(2, 4, 5));
        Collection<Integer> c2 = new LinkedList<Integer>(Arrays.asList(10));
        System.out.println(c1.containsAll(c2));
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
        if (symptomSelector != null
                && ((complex.symptomSelector != null && !symptomSelector.containsAll(complex.symptomSelector))
                || complex.symptomSelector == null))
            return false;
        if (negativeTestsSelector != null && ((complex.negativeTestsSelector != null
                && !negativeTestsSelector.containsAll(complex.negativeTestsSelector))
                || complex.negativeTestsSelector == null))
            return false;
        if (previousDiseasesSelector != null && ((complex.previousDiseasesSelector != null
                && !previousDiseasesSelector.containsAll(complex.previousDiseasesSelector))
                || complex.previousDiseasesSelector == null))
            return false;
        if (ageSelector != null && ((complex.ageSelector != null && !ageSelector.contains(complex.ageSelector))
                || complex.ageSelector == null))
            return false;
        return true;
    }

    public boolean isPatientCovered(Patient patient) {
        if (symptomSelector != null && !symptomSelector.containsAll(patient.getSymptoms()))
            return false;
        if (negativeTestsSelector != null && !negativeTestsSelector.containsAll(patient.getNegativeTests()))
            return false;
        if (previousDiseasesSelector != null
                && !previousDiseasesSelector.containsAll(patient.getPreviousAndCurrentDiseases()))
            return false;
        if (ageSelector != null && !ageSelector.contains(patient.getAge()))
            return false;
        return true;
    }

    public Collection<Rule> generateRules(Entity learnedConcept, OntologyWrapper ontology) {
        Collection<Rule> rules = new ArrayList<Rule>();
        int i = 0;
        for (Entity symptom : symptomSelector != null ? symptomSelector : ontology.getSymptoms().values()) {
            for (Entity previousDisease : previousDiseasesSelector != null ? previousDiseasesSelector
                    : ontology.getDiseases().values()) {
                for (Entity negativeTest : negativeTestsSelector != null ? negativeTestsSelector
                        : ontology.getTests().values()) {
                    Rule rule = new Rule("Generated" + learnedConcept.getID() + "Disease" + ++i);
                    rules.add(rule);
                    Variable patientVariable = new Variable("patient", ontology.getClasses().get("Patient"));
                    TwoArgumentsAtom<Variable, Entity> diseaseAtom = new TwoArgumentsAtom<Variable, Entity>(
                            "hasDisease", patientVariable, learnedConcept);
                    rule.addHeadAtom(diseaseAtom);
                    ClassDeclarationAtom<Variable> patientDeclarationAtom = new ClassDeclarationAtom<Variable>(
                            ontology.getClasses().get("Patient"), patientVariable);
                    TwoArgumentsAtom<Variable, Entity> symptomAtom = new TwoArgumentsAtom<Variable, Entity>(
                            "hasSymptom", patientVariable, symptom);
                    TwoArgumentsAtom<Variable, Entity> prevDiseaseAtom = new TwoArgumentsAtom<Variable, Entity>(
                            "hadOrHasDisease", patientVariable, previousDisease);
                    TwoArgumentsAtom<Variable, Entity> negativeTestAtom = new TwoArgumentsAtom<Variable, Entity>(
                            "negativeTest", patientVariable, negativeTest);
                    rule.addBodyAtom(patientDeclarationAtom);
                    rule.addBodyAtom(symptomAtom);
                    rule.addBodyAtom(prevDiseaseAtom);
                    rule.addBodyAtom(negativeTestAtom);
                    if (ageSelector != null) {
                        Variable ageVariable = new Variable("_age");
                        TwoArgumentsAtom<Variable, Variable> ageAtom = new TwoArgumentsAtom<Variable, Variable>(
                                "negativeTest", patientVariable, ageVariable);
                        rule.addBodyAtom(ageAtom);
                        if (ageSelector.hasLowerBound() && ageSelector.hasUpperBound()
                                && ageSelector.lowerEndpoint().equals(ageSelector.upperEndpoint())) {
                            TwoArgumentsAtom<Variable, Integer> equalAtom = new TwoArgumentsAtom<Variable, Integer>(
                                    "ewual", "swrlb", ageVariable, ageSelector.lowerEndpoint());
                            rule.addBodyAtom(equalAtom);
                        } else {
                            if (ageSelector.hasLowerBound()) {
                                switch (ageSelector.lowerBoundType()) {
                                    case OPEN:
                                        TwoArgumentsAtom<Variable, Integer> greaterThanAtom = new TwoArgumentsAtom<Variable, Integer>(
                                                "greaterThan", "swrlb", ageVariable, ageSelector.lowerEndpoint());
                                        rule.addBodyAtom(greaterThanAtom);
                                        break;
                                    case CLOSED:
                                        TwoArgumentsAtom<Variable, Integer> atLeastAtom = new TwoArgumentsAtom<Variable, Integer>(
                                                "greaterThanOrEqual", "swrlb", ageVariable, ageSelector.lowerEndpoint());
                                        rule.addBodyAtom(atLeastAtom);
                                        break;
                                }
                            }
                            if (ageSelector.hasUpperBound()) {
                                switch (ageSelector.upperBoundType()) {
                                    case OPEN:
                                        TwoArgumentsAtom<Variable, Integer> lessThanAtom = new TwoArgumentsAtom<Variable, Integer>(
                                                "lessThan", "swrlb", ageVariable, ageSelector.upperEndpoint());
                                        rule.addBodyAtom(lessThanAtom);
                                        break;
                                    case CLOSED:
                                        TwoArgumentsAtom<Variable, Integer> atMostAtom = new TwoArgumentsAtom<Variable, Integer>(
                                                "lessThanOrEqual", "swrlb", ageVariable, ageSelector.upperEndpoint());
                                        rule.addBodyAtom(atMostAtom);
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return rules;
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
        return str.toString();
    }

    @Override
    public int compareTo(Complex o) {

        return 0;
    }

}
