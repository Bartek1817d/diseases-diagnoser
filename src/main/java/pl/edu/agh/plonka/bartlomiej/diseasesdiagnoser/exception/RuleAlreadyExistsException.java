package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;

import static java.lang.String.format;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.ObservableResourceFactory.getTranslation;

public class RuleAlreadyExistsException extends Exception {

    public RuleAlreadyExistsException(Rule rule) {
        super(format(getTranslation("RULE_ALREADY_EXISTS"), rule.getName()));
    }
}
