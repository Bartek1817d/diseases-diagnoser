package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;

import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.ObservableResourceFactory.getTranslation;

public class CreateRuleException extends Exception {

    public CreateRuleException(Rule rule, Throwable cause) {
        super(getTranslation("ERROR_CREATING_RULE") + ' ' + rule.getName(), cause);
    }
}
