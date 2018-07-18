package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils;

import com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
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

public class MachineLearning {

    // 0 for restrictive, 1 for general
    private static final float epsilon = 0.5f;
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private OntologyWrapper ontology;
    private Random random = new Random();

    public MachineLearning(OntologyWrapper ontology) {
        this.ontology = ontology;
    }

    public static void main(String args[]) throws OWLOntologyCreationException {
        OntologyWrapper ontology = new OntologyWrapper(MachineLearning.class.getResourceAsStream("/human_diseases.owl"));
        MachineLearning machineLearning = new MachineLearning(ontology);
        Collection<Rule> rules = machineLearning.sequentialCovering(new HashSet<>(ontology.generatePatientsFromRules()));
    }

    public Collection<Rule> sequentialCovering(Set<Patient> trainingSet) {
        Collection<Rule> rules = new HashSet<>();
        Set<Patient> uncoveredSet = new HashSet<>(trainingSet);
        while (!uncoveredSet.isEmpty()) {
            Complex complex = findComplex(trainingSet, uncoveredSet);
            Collection<Entity> category = category(complex, trainingSet, uncoveredSet);
            removeCoveredExamples(uncoveredSet, complex);
            Collection<String> ruleNames = rules.stream().map(Rule::getName).collect(Collectors.toList());
            Rule rule = complex.generateRule(category, ontology, ruleNames);
            rules.add(rule);
        }
        return rules;
    }

    private Complex findComplex(Set<Patient> trainingSet, Set<Patient> uncoveredSet) {
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
            Collections.sort(star, new ComplexComparator(trainingSet, uncoveredSet, positiveSeed));
            star.leaveFirstElements(5);
            negativeSeed = negativeSeed(trainingSet, star, positiveSeed);
        }
        return star.get(0);
    }

    private Collection<Entity> category(Complex complex, Collection<Patient> trainingSet, Collection<Patient> uncoveredSet) {
        System.out.println("category");
        Map<HashSet<Entity>, Integer> voteBox = new HashMap<>();
        for (Patient trainingSeed : trainingSet) {
            if (complex.isPatientCovered(trainingSeed)) {
                HashSet<Entity> decision = new HashSet<>(trainingSeed.getDiseases());
                if (voteBox.containsKey(decision))
                    voteBox.put(decision, voteBox.get(decision) + 1);
                else
                    voteBox.put(decision, 1);
            }
        }
        return Collections.max(voteBox.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    private Patient positiveSeed(Set<Patient> trainingSet, Set<Patient> uncoveredSet) {
        System.out.println("positiveSeed");
        if (uncoveredSet.isEmpty())
            return null;
        Set<Patient> coveredSet = Sets.difference(trainingSet, uncoveredSet);
        for (Patient uncovered : uncoveredSet) {
            calculateDistance(uncovered, coveredSet);
        }
        return Collections.max(uncoveredSet);
    }

    private Patient negativeSeed(Collection<Patient> trainingSet, Star star, Patient positiveSeed) {
        System.out.println("negativeSeed");
        List<Patient> negativeSeeds = new ArrayList<>();
        for (Patient patient : trainingSet)
            if (!positiveSeed.getDiseases().containsAll(patient.getDiseases()) && star.isPatientCovered(patient))
                negativeSeeds.add(patient);
        if (negativeSeeds.isEmpty())
            return null;
        Set<Patient> positiveSeedSingleton = Collections.singleton(positiveSeed);
        for (Patient negativeSeed : negativeSeeds)
            calculateDistance(negativeSeed, positiveSeedSingleton);
        return Collections.min(negativeSeeds);
    }

    private void calculateDistance(Patient patient, Collection<Patient> otherPatients) {
        System.out.println("calculateDistance");
        int symptomDiff = 0;
        int negTestDiff = 0;
        int disDiff = 0;
        int ageDiff = 0;
        for (Patient otherPatient : otherPatients) {
            symptomDiff += Sets.symmetricDifference(new HashSet<>(patient.getSymptoms()), new HashSet<>(otherPatient.getSymptoms())).size();
            negTestDiff += Sets.symmetricDifference(new HashSet<>(patient.getNegativeTests()), new HashSet<>(otherPatient.getNegativeTests())).size();
            disDiff += Sets.symmetricDifference(new HashSet<>(patient.getPreviousAndCurrentDiseases()),
                    new HashSet<>(otherPatient.getPreviousAndCurrentDiseases())).size();
            if (patient.getAge() >= 0 && otherPatient.getAge() >= 0)
                ageDiff += Math.abs(patient.getAge() - otherPatient.getAge());
        }
        float symptomEv = (float) symptomDiff / (otherPatients.size() * ontology.getSymptoms().size());
        float negTestEv = (float) negTestDiff / (otherPatients.size() * ontology.getTests().size());
        float disEv = (float) disDiff / (otherPatients.size() * ontology.getDiseases().size());
        float ageEv = (float) ageDiff / (otherPatients.size() * 100);
        patient.setEvaluation(symptomEv + negTestEv + disEv + ageEv);
    }

    @SuppressWarnings({"unchecked"})
    private Collection<Complex> partialStar(Patient positivePatient, Patient negativePatient) {
        Collection<Complex> resultComplexes = new ArrayList<>();
        resultComplexes.addAll(createComplexes(positivePatient.getSymptoms(), negativePatient.getSymptoms(), Complex::setSymptomSelector));
        resultComplexes.addAll(createComplexes(positivePatient.getNegativeTests(), negativePatient.getNegativeTests(), Complex::setNegativeTestsSelector));
        resultComplexes.addAll(createComplexes(positivePatient.getPreviousAndCurrentDiseases(), negativePatient.getPreviousAndCurrentDiseases(), Complex::setPreviousDiseasesSelector));
        Complex ageComplex = createAgeComplex(positivePatient, negativePatient);
        if (ageComplex != null)
            resultComplexes.add(ageComplex);

        return resultComplexes;
    }

    private void removeCoveredExamples(Collection<Patient> trainingSet, Complex complex) {
        System.out.println("removeCoveredExamples");
        Iterator<Patient> it = trainingSet.iterator();
        while (it.hasNext()) {
            Patient p = it.next();
            if (complex.isPatientCovered(p))
                it.remove();
        }
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

    private Complex createAgeComplex(Patient positivePatient, Patient negativePatient) {
        int posAge = positivePatient.getAge();
        int negAge = negativePatient.getAge();
        if (posAge >= 0 && negAge >= 0 && posAge != negAge) {
            Complex ageComplex = new Complex();
            int midAge = Math.round(posAge + (negAge - posAge) * epsilon);
            if (negAge < posAge) {
                if (midAge == negAge)
                    ageComplex.setAgeSelector(LinearSelector.greaterThanSelector(midAge));
                else
                    ageComplex.setAgeSelector(LinearSelector.atLeastSelector(midAge));
            } else if (negAge > posAge) {
                if (midAge == negAge)
                    ageComplex.setAgeSelector(LinearSelector.lessThanSelector(midAge));
                else
                    ageComplex.setAgeSelector(LinearSelector.atMostSelector(midAge));
            }
            return ageComplex;
        }
        return null;
    }

}
