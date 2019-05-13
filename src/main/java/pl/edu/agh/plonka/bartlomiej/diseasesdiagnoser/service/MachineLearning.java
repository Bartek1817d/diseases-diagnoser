package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.PartialStarCreationException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.ontology.OntologyWrapper;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.*;

public class MachineLearning {

    // 0 for restrictive, 1 for general
    private static final float epsilon = 0.5f;
    private static final float decisionVotesPercent = 0.5f;
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private OntologyWrapper ontology;

    public MachineLearning(OntologyWrapper ontology) {
        this.ontology = ontology;
    }

    public Collection<Rule> sequentialCovering(Set<Patient> trainingSet) throws PartialStarCreationException {
        Collection<Rule> rules = new HashSet<>();
        Set<Patient> uncoveredSet = new HashSet<>(trainingSet);
        int ruleIdx = 1;
        while (!uncoveredSet.isEmpty()) {
            Complex complex = findComplex(trainingSet, uncoveredSet);
            Concepts concepts = categories(complex, trainingSet, uncoveredSet);
            removeCoveredExamples(uncoveredSet, complex);
            Rule rule = complex.generateRule(GENERATED_RULE_PREFIX + ruleIdx++, concepts, ontology);
            rules.add(rule);
        }
        return rules;
    }

    private Complex findComplex(Set<Patient> trainingSet, Set<Patient> uncoveredSet) throws PartialStarCreationException {
        LOG.debug("findComplex");
        Star star = new Star();
        Patient positiveSeed = positiveSeed(trainingSet, uncoveredSet);
        Patient negativeSeed = negativeSeed(trainingSet, star, positiveSeed);
        while (positiveSeed != null && negativeSeed != null) {
            Collection<Complex> partialStar = partialStar(positiveSeed, negativeSeed);
            if (partialStar.isEmpty()) {
                LOG.debug("Partial star is empty");
                throw new PartialStarCreationException(positiveSeed, negativeSeed);
            }
            star.intersection(partialStar);
            star.deleteNarrowComplexes();
            star.sort(new ComplexComparator(trainingSet, uncoveredSet, positiveSeed));
            star.leaveFirstElements(5);
            negativeSeed = negativeSeed(trainingSet, star, positiveSeed);
        }
        return star.get(0);
    }

    private Concepts categories(Complex complex, Collection<Patient> trainingSet, Collection<Patient> uncoveredSet) {
        LOG.debug("categories");

        Concepts concepts = new Concepts();
        int patientsCovered = 0;
        HashMap<Entity, Integer> diseasesVoteBox = new HashMap<>();
        HashMap<Entity, Integer> testsVoteBox = new HashMap<>();
        HashMap<Entity, Integer> treatmentsVoteBox = new HashMap<>();

        for (Patient trainingSeed : trainingSet) {
            if (complex.isPatientCovered(trainingSeed)) {
                patientsCovered++;
                trainingSeed.getDiseases().forEach(e -> addVote(diseasesVoteBox, e));
                trainingSeed.getTests().forEach(e -> addVote(testsVoteBox, e));
                trainingSeed.getTreatments().forEach(e -> addVote(treatmentsVoteBox, e));
            }
        }

        concepts.diseases = countVotes(diseasesVoteBox, patientsCovered);
        concepts.tests = countVotes(testsVoteBox, patientsCovered);
        concepts.treatments = countVotes(treatmentsVoteBox, patientsCovered);

        return concepts;
    }


    private Patient positiveSeed(Set<Patient> trainingSet, Set<Patient> uncoveredSet) {
        LOG.debug("positiveSeed");
        if (uncoveredSet.isEmpty())
            return null;
        Set<Patient> coveredSet = Sets.difference(trainingSet, uncoveredSet);
        for (Patient uncovered : uncoveredSet) {
            calculateDistance(uncovered, coveredSet);
        }
        return Collections.max(uncoveredSet);
    }

    private Patient negativeSeed(Collection<Patient> trainingSet, Star star, Patient positiveSeed) {
        LOG.debug("negativeSeed");
        List<Patient> negativeSeeds = new ArrayList<>();
        for (Patient patient : trainingSet) {
            if (star.isPatientCovered(patient) && (!positiveSeed.getDiseases().containsAll(patient.getDiseases()) ||
                    !positiveSeed.getTests().containsAll(patient.getTests()) || !positiveSeed.getTreatments().containsAll(patient.getTreatments()))) {
                negativeSeeds.add(patient);
            }
        }
        if (negativeSeeds.isEmpty())
            return null;
        Set<Patient> positiveSeedSingleton = Collections.singleton(positiveSeed);
        for (Patient negativeSeed : negativeSeeds)
            calculateDistance(negativeSeed, positiveSeedSingleton);
        return Collections.min(negativeSeeds);
    }

    private void calculateDistance(Patient patient, Collection<Patient> otherPatients) {
        LOG.debug("calculateDistance");
        if (otherPatients.isEmpty()) {
            LOG.debug("No other patients. Set patient distance to 0");
            patient.setEvaluation(0);
            return;
        }
        int symptomDiff = 0;
        int negTestDiff = 0;
        int disDiff = 0;
        int ageDiff = 0;
        int heightDiff = 0;
        int weightDiff = 0;
        for (Patient otherPatient : otherPatients) {
            symptomDiff += Sets.symmetricDifference(new HashSet<>(patient.getSymptoms()), new HashSet<>(otherPatient.getSymptoms())).size();
            negTestDiff += Sets.symmetricDifference(new HashSet<>(patient.getNegativeTests()), new HashSet<>(otherPatient.getNegativeTests())).size();
            disDiff += Sets.symmetricDifference(new HashSet<>(patient.getPreviousAndCurrentDiseases()),
                    new HashSet<>(otherPatient.getPreviousAndCurrentDiseases())).size();
            if (patient.getAge() >= 0 && otherPatient.getAge() >= 0)
                ageDiff += Math.abs(patient.getAge() - otherPatient.getAge());
            if (patient.getHeight() >= 0 && otherPatient.getHeight() >= 0)
                heightDiff += Math.abs(patient.getHeight() - otherPatient.getHeight());
            if (patient.getWeight() >= 0 && otherPatient.getWeight() >= 0)
                weightDiff += Math.abs(patient.getWeight() - otherPatient.getWeight());
        }
        float symptomEv = (float) symptomDiff / (otherPatients.size() * ontology.getSymptoms().size());
        float negTestEv = (float) negTestDiff / (otherPatients.size() * ontology.getTests().size());
        float disEv = (float) disDiff / (otherPatients.size() * ontology.getDiseases().size());
        float ageEv = (float) ageDiff / (otherPatients.size() * (PATIENT_MAX_AGE - PATIENT_MIN_AGE));
        float heightEv = (float) heightDiff / (otherPatients.size() * (PATIENT_MAX_HEIGHT - PATIENT_MIN_HEIGHT));
        float weightEv = (float) weightDiff / (otherPatients.size() * (PATIENT_MAX_WEIGHT - PATIENT_MIN_WEIGHT));

        patient.setEvaluation(symptomEv + negTestEv + disEv + ageEv + heightEv + weightEv);
    }

    private Collection<Complex> partialStar(Patient positivePatient, Patient negativePatient) {
        Collection<Complex> resultComplexes = new ArrayList<>();
        resultComplexes.addAll(createComplexes(positivePatient.getSymptoms(), negativePatient.getSymptoms(), Complex::setSymptomSelector));
        resultComplexes.addAll(createComplexes(positivePatient.getNegativeTests(), negativePatient.getNegativeTests(), Complex::setNegativeTestsSelector));
        resultComplexes.addAll(createComplexes(positivePatient.getPreviousAndCurrentDiseases(), negativePatient.getPreviousAndCurrentDiseases(), Complex::setPreviousDiseasesSelector));

        Complex ageComplex = createLinearComplex(positivePatient.getAge(), negativePatient.getAge(), Complex::setAgeSelector);
        Complex heightComplex = createLinearComplex(positivePatient.getHeight(), negativePatient.getHeight(), Complex::setHeightSelector);
        Complex weightComplex = createLinearComplex(positivePatient.getWeight(), negativePatient.getWeight(), Complex::setWeightSelector);

        if (ageComplex != null) resultComplexes.add(ageComplex);
        if (heightComplex != null) resultComplexes.add(heightComplex);
        if (weightComplex != null) resultComplexes.add(weightComplex);

        return resultComplexes;
    }

    private void removeCoveredExamples(Collection<Patient> trainingSet, Complex complex) {
        LOG.debug("removeCoveredExamples");
        trainingSet.removeIf(complex::isPatientCovered);
    }

    private Collection<Complex> createComplexes(Collection<Entity> positiveEntities, Collection<Entity> negativeEntities,
                                                BiConsumer<Complex, NominalSelector<Entity>> complexSetter) {
        ArrayList<Complex> complexes = new ArrayList<>();
        if (!positiveEntities.isEmpty()) {
            for (Entity entity : positiveEntities) {
                if (!negativeEntities.contains(entity)) {
                    Complex complex = createComplex(entity, complexSetter);
                    complexes.add(complex);
                }
            }
        }
        return complexes;
    }

    private Complex createComplex(Entity entity, BiConsumer<Complex, NominalSelector<Entity>> complexSetter) {
        NominalSelector<Entity> selector = new NominalSelector<>();
        selector.add(entity);
        Complex complex = new Complex();
        complexSetter.accept(complex, selector);
        return complex;
    }

    private Complex createLinearComplex(int posVal, int negVal, BiConsumer<Complex, LinearSelector<Integer>> complexSetter) {
        LinearSelector<Integer> selector = createLinearSelector(posVal, negVal);
        if (selector != null) {
            Complex complex = new Complex();
            complexSetter.accept(complex, selector);
            return complex;
        }
        return null;
    }

    private LinearSelector createLinearSelector(int posValue, int negValue) {
        if (posValue >= 0 && negValue >= 0 && posValue != negValue) {
            int midValue = Math.round(posValue + (negValue - posValue) * epsilon);
            if (negValue < posValue) {
                if (midValue == negValue)
                    return LinearSelector.greaterThanSelector(midValue);
                else
                    return LinearSelector.atLeastSelector(midValue);
            } else {
                if (midValue == negValue)
                    return LinearSelector.lessThanSelector(midValue);
                else
                    return LinearSelector.atMostSelector(midValue);
            }
        }
        return null;
    }

    private void addVote(Map<Entity, Integer> voteBox, Entity entity) {
        if (voteBox.containsKey(entity))
            voteBox.put(entity, voteBox.get(entity) + 1);
        else
            voteBox.put(entity, 1);
    }

    private Collection<Entity> countVotes(Map<Entity, Integer> voteBox, int allVotesNumber) {
        return voteBox.entrySet()
                .stream()
                .filter(e -> (float) e.getValue() / allVotesNumber >= decisionVotesPercent)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

}
