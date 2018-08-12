package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;

import java.util.Collection;
import java.util.Comparator;

public class ComplexComparator implements Comparator<Complex> {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private Concepts learnedConcept;
    private Collection<Patient> trainingSet;
    private Collection<Patient> uncoveredSet;
    private int positiveExamples = 0;
    private int negativeExamples;

    public ComplexComparator(Collection<Patient> trainingSet, Collection<Patient> uncoveredSet, Patient positiveSeed) {
        this.learnedConcept = new Concepts(positiveSeed);
        this.trainingSet = trainingSet;
        this.uncoveredSet = uncoveredSet;

        for (Patient seed : trainingSet)
            if (learnedConcept.covers(seed))
                positiveExamples++;
        negativeExamples = trainingSet.size() - positiveExamples;
    }

    private Pair coveredPositiveAndNegativeSeeds(Collection<Patient> trainingSet, Complex complex) {
        int positive = 0;
        int negative = 0;
        for (Patient seed : trainingSet)
            if (complex.isPatientCovered(seed))
                if (learnedConcept.covers(seed))
                    positive++;
                else
                    negative++;
        return new Pair(positive, negative);
    }

    @Override
    public int compare(Complex complex1, Complex complex2) {
        if (complex1.getEvaluation1() == null) {
            Pair p = coveredPositiveAndNegativeSeeds(trainingSet, complex1);
            complex1.setEvaluation1(p.positive);
            complex1.setEvaluation2(negativeExamples - p.negative);
        }
        if (complex2.getEvaluation2() == null) {
            Pair p = coveredPositiveAndNegativeSeeds(trainingSet, complex2);
            complex2.setEvaluation1(p.positive);
            complex2.setEvaluation2(negativeExamples - p.negative);
        }
        if (complex1.getEvaluation1() < complex2.getEvaluation1())
            return 1;
        if (complex1.getEvaluation1() > complex2.getEvaluation1())
            return -1;
        if (complex1.getEvaluation2() < complex2.getEvaluation2())
            return 1;
        if (complex1.getEvaluation2() > complex2.getEvaluation2())
            return -1;

        if (complex1.getEvaluation3() == null) {
            Pair p = coveredPositiveAndNegativeSeeds(uncoveredSet, complex1);
            complex1.setEvaluation3(p.positive);
        }
        if (complex2.getEvaluation3() == null) {
            Pair p = coveredPositiveAndNegativeSeeds(uncoveredSet, complex2);
            complex2.setEvaluation3(p.positive);
        }
        if (complex1.getEvaluation3() < complex2.getEvaluation3())
            return 1;
        if (complex1.getEvaluation3() > complex2.getEvaluation3())
            return -1;

        return 0;
    }

    private class Pair {
        private int positive;
        private int negative;

        private Pair(int positive, int negative) {
            this.positive = positive;
            this.negative = negative;
        }
    }

}
