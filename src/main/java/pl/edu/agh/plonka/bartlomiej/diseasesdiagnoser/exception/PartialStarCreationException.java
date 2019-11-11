package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;

import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.ObservableResourceFactory.getTranslation;

public class PartialStarCreationException extends Exception {
    public PartialStarCreationException(Patient positivePatient, Patient negativePatient) {
        super(String.format(getTranslation("ERROR_CREATING_PARTIAL_STAR_EXCEPTION"), positivePatient, negativePatient));
    }
}
