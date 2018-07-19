package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;

public class CreateRuleException extends Exception {

    public CreateRuleException(Rule rule, Throwable cause) {
        super("Could not create rule " + rule, cause);
    }
}
