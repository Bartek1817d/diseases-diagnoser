package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils;

import com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.OntologyWrapper;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.*;

import java.io.File;
import java.util.*;

public class MachineLearning {

    private OntologyWrapper ontology;
    private Random random = new Random();

    public MachineLearning(OntologyWrapper ontology) {
        this.ontology = ontology;
    }

    // 0 for restrictive, 1 for general
    private static final float epsilon = 0.5f;

    public Collection<Rule> sequentialCovering(Set<Patient> trainingSet) {
        Collection<Rule> rules = new HashSet<Rule>();
        Set<Patient> uncoveredSet = new HashSet<Patient>(trainingSet);
        while (!uncoveredSet.isEmpty()) {
            // System.out.println("Covered " + (float)((trainingSet.size() -
            // uncoveredSet.size()) / trainingSet.size()));
            System.out.println(uncoveredSet.size());
            Complex complex = findComplex(trainingSet, uncoveredSet);
            Entity category = category(complex, trainingSet, uncoveredSet);
            removeCoveredExamplex(uncoveredSet, complex);
            rules.addAll(complex.generateRules(category, ontology));
        }
        return rules;
    }

    private Complex findComplex(Set<Patient> trainingSet, Set<Patient> uncoveredSet) {
        System.out.println("findComplex");
        Patient positiveSeed = positiveSeed(trainingSet, uncoveredSet);
        Star star = new Star();
        Patient negativeSeed = negativeSeed(trainingSet, star, positiveSeed);
        while (negativeSeed != null) {
            Collection<Complex> partialStar = partialStar(positiveSeed, negativeSeed);
            if (partialStar.isEmpty()) {
                System.out.println("Partial star is empty");
                return null;
            }
            star.intersection(partialStar);
            star.deleteNarrowComplexes();
            Collections.sort(star, new ComplexComparator(trainingSet, uncoveredSet, positiveSeed));
            star.leaveFirstElements(5);
            negativeSeed = negativeSeed(trainingSet, star, positiveSeed);
        }
        return star.get(0);
    }

    private Entity category(Complex complex, Collection<Patient> trainingSet, Collection<Patient> uncoveredSet) {
        System.out.println("category");
        Map<Entity, Integer> voteBox = new HashMap<Entity, Integer>();
        for (Patient trainingSeed : trainingSet) {
            if (complex.isPatientCovered(trainingSeed)) {
                Entity decision = trainingSeed.getDiseases().iterator().next();
                if (voteBox.containsKey(decision))
                    voteBox.put(decision, voteBox.get(decision) + 1);
                else
                    voteBox.put(decision, 1);
            }
        }
        return Collections.max(voteBox.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
    }

    private Patient positiveSeed(Set<Patient> trainingSet, Set<Patient> uncoveredSet) {
        System.out.println("positiveSeed");
        Set<Patient> coveredSet = Sets.difference(trainingSet, uncoveredSet);
        for (Patient uncovered : uncoveredSet) {
            calculateDistance(uncovered, coveredSet);
        }
        if (uncoveredSet.isEmpty())
            return null;
        return Collections.max(uncoveredSet);
    }

    private Patient negativeSeed(Collection<Patient> trainingSet, Star star, Patient positiveSeed) {
        System.out.println("negativeSeed");
        List<Patient> negativeSeeds = new ArrayList<Patient>();
        for (Patient patient : trainingSet)
            if (!patient.getDiseases().containsAll(positiveSeed.getDiseases()) && star.isPatientCovered(patient))
                negativeSeeds.add(patient);
        Set<Patient> positiveSeedSingleton = Collections.singleton(positiveSeed);
        for (Patient negativeSeed : negativeSeeds)
            calculateDistance(negativeSeed, positiveSeedSingleton);
        if (negativeSeeds.isEmpty())
            return null;
        return Collections.min(negativeSeeds);
    }

    private void calculateDistance(Patient patient, Collection<Patient> otherPatients) {
        System.out.println("calculateDistance");
        int symptomDiff = 0;
        int negTestDiff = 0;
        int disDiff = 0;
        int ageDiff = 0;
        for (Patient otherPatient : otherPatients) {
            symptomDiff += Sets.symmetricDifference(patient.getSymptoms(), otherPatient.getSymptoms()).size();
            negTestDiff += Sets.symmetricDifference(patient.getNegativeTests(), otherPatient.getNegativeTests()).size();
            disDiff += Sets.symmetricDifference(patient.getPreviousAndCurrentDiseases(),
                    otherPatient.getPreviousAndCurrentDiseases()).size();
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
        System.out.println("partialStar");
        System.out.println(positivePatient.getID() + " " + negativePatient.getID());
        System.out.println(negativePatient);

        Collection<Complex> resultComplexes = new ArrayList<Complex>();

        if (!positivePatient.getSymptoms().isEmpty() && !negativePatient.getSymptoms().isEmpty()
                && !positivePatient.getSymptoms().containsAll(negativePatient.getSymptoms())) {
            NominalSelector<Entity> symptomsSelector = new NominalSelector<Entity>(ontology.getSymptoms().values());
            symptomsSelector.removeAll(negativePatient.getSymptoms());
            if (!Collections.disjoint(symptomsSelector, positivePatient.getSymptoms())) {
                // if
                // (symptomsSelector.containsAll(positivePatient.getSymptoms()))
                // {
                symptomsSelector.addAll(positivePatient.getSymptoms());
                Complex symptomsComplex = new Complex();
                symptomsComplex.setSymptomSelector(symptomsSelector);
                resultComplexes.add(symptomsComplex);
            }
        }

        if (!positivePatient.getNegativeTests().isEmpty() && !negativePatient.getNegativeTests().isEmpty()
                && !positivePatient.getNegativeTests().containsAll(negativePatient.getNegativeTests())) {
            NominalSelector<Entity> negativeTestsSelector = new NominalSelector<Entity>(ontology.getTests().values());
            negativeTestsSelector.removeAll(negativePatient.getNegativeTests());
            if (!Collections.disjoint(negativeTestsSelector, positivePatient.getNegativeTests())) {
                // if
                // (negativeTestsSelector.containsAll(positivePatient.getNegativeTests()))
                // {
                negativeTestsSelector.addAll(positivePatient.getNegativeTests());
                Complex negativeTestsComplex = new Complex();
                negativeTestsComplex.setNegativeTestsSelector(negativeTestsSelector);
                resultComplexes.add(negativeTestsComplex);
            }
        }

        if (!positivePatient.getPreviousAndCurrentDiseases().isEmpty()
                && !negativePatient.getPreviousAndCurrentDiseases().isEmpty() && !positivePatient
                .getPreviousAndCurrentDiseases().containsAll(negativePatient.getPreviousAndCurrentDiseases())) {
            NominalSelector<Entity> previousDiseasesSelector = new NominalSelector<Entity>(
                    ontology.getDiseases().values());
            previousDiseasesSelector.removeAll(negativePatient.getPreviousAndCurrentDiseases());
            if (!Collections.disjoint(previousDiseasesSelector, positivePatient.getPreviousAndCurrentDiseases())) {
                // if
                // (previousDiseasesSelector.containsAll(positivePatient.getPreviousAndCurrentDiseases()))
                // {
                previousDiseasesSelector.addAll(positivePatient.getPreviousAndCurrentDiseases());
                Complex diseasesComplex = new Complex();
                diseasesComplex.setPreviousDiseasesSelector(previousDiseasesSelector);
                resultComplexes.add(diseasesComplex);
            }
        }

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
                resultComplexes.add(ageComplex);
            } else if (negAge > posAge) {
                if (midAge == negAge)
                    ageComplex.setAgeSelector(LinearSelector.lessThanSelector(midAge));
                else
                    ageComplex.setAgeSelector(LinearSelector.atMostSelector(midAge));
                resultComplexes.add(ageComplex);
            }
        }
        for (Complex c : resultComplexes)
            System.out.println(c);
        return resultComplexes;
    }

    private void removeCoveredExamplex(Collection<Patient> trainingSet, Complex complex) {
        System.out.println("removeCoveredExamples");
        Iterator<Patient> it = trainingSet.iterator();
        while (it.hasNext()) {
            Patient p = it.next();
            if (complex.isPatientCovered(p))
                it.remove();
        }
    }

    public static void main(String args[]) throws OWLOntologyCreationException {
        OntologyWrapper ontology = new OntologyWrapper(new File("res/human_diseases.owl"));
        MachineLearning machineLearning = new MachineLearning(ontology);
        machineLearning.sequentialCovering(new HashSet<Patient>(ontology.getAssertedPatients()));
    }

}
