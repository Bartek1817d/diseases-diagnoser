package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.RuleBuilder;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.ViewManager;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Collections.emptyList;

public class RuleEditDialogController {

    @FXML
    private TextArea textArea;

    private RuleBuilder ruleBuilder;
    private PatientsService patientsService;
    private ViewManager viewManager;
    private Stage dialogStage;
    private boolean okClicked = false;

    public void init(ViewManager viewManager, Stage dialogStage, PatientsService patientsService) {
        this.viewManager = viewManager;
        this.dialogStage = dialogStage;
        this.patientsService = patientsService;
        this.ruleBuilder = new RuleBuilder(patientsService.getOntology().getClasses().get("Patient"));
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOK() {
        okClicked = true;
        dialogStage.close();
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @FXML
    private void handleAddSymptoms() {
        Collection<Entity> symptoms = new HashSet<>();
        boolean okClicked = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get("Symptom"),
                emptyList(), symptoms, patientsService);
        if (okClicked) {
            ruleBuilder.withSymptoms(symptoms);
            textArea.setText(ruleBuilder.build().toString());
        }
    }

    @FXML
    private void handleAddDiseases() {
        Collection<Entity> diseases = new HashSet<>();
        boolean okClicked = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get("Disease"),
                emptyList(), diseases, patientsService);
        if (okClicked) {
            ruleBuilder.withDiseases(diseases);
            textArea.setText(ruleBuilder.build().toString());
        }
    }
}
