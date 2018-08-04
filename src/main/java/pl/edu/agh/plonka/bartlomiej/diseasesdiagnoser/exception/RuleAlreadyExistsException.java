package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;

import static java.lang.String.format;

public class RuleAlreadyExistsException extends Exception {

    public RuleAlreadyExistsException(Rule rule) {
        super(format("Rule %s already exists", rule.getName()));
    }
}
