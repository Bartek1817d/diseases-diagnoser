package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;

public class PartialStarCreationException extends Exception {
    public PartialStarCreationException(Patient positivePatient, Patient negativePatient) {
        super(String.format("Could not create partial star for positive patient\n%s\n\nand negative patient\n%s", positivePatient, negativePatient));
    }
}
