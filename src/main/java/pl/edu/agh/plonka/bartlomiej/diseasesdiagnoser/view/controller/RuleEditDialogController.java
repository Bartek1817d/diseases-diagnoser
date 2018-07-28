package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import com.google.common.collect.Range;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.RuleBuilder;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Response;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.ViewManager;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Collections.emptyList;

public class RuleEditDialogController implements ResponseController<Rule> {

    @FXML
    private TextField ruleNameField;
    @FXML
    private TextArea ruleViewArea;

    private RuleBuilder ruleBuilder;
    private PatientsService patientsService;
    private ViewManager viewManager;
    private Stage dialogStage;
    private boolean okClicked;

    public void init(ViewManager viewManager, Stage dialogStage, PatientsService patientsService, Rule rule) {
        this.viewManager = viewManager;
        this.dialogStage = dialogStage;
        this.patientsService = patientsService;
        this.ruleBuilder = new RuleBuilder(patientsService.getOntology().getClasses().get("Patient"));
    }

    @Override
    public Response<Rule> getResponse() {
        return new Response<>(okClicked, ruleBuilder.withName(ruleNameField.getText().trim()).build());
    }

    @FXML
    private void handleOK() {
        if (ruleNameField.getText().trim().isEmpty()) {
            viewManager.errorDialog("Failed to create rule", null, "Cannot create rule with empty name!");
            return;
        }
        if (ruleViewArea.getText().trim().isEmpty()) {
            viewManager.errorDialog("Failed to create rule", null, "Cannot create empty rule!");
            return;
        }
        okClicked = true;
        dialogStage.close();
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        okClicked = false;
        dialogStage.close();
    }

    @FXML
    private void handleAddSymptoms() {
        Collection<Entity> symptoms = new HashSet<>();
        boolean okClicked = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get("Symptom"),
                emptyList(), symptoms, patientsService);
        if (okClicked) {
            ruleBuilder.withSymptoms(symptoms);
            ruleViewArea.setText(ruleBuilder.build().toString());
        }
    }

    @FXML
    private void handleAddDiseases() {
        Collection<Entity> diseases = new HashSet<>();
        boolean okClicked = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get("Disease"),
                emptyList(), diseases, patientsService);
        if (okClicked) {
            ruleBuilder.withDiseases(diseases);
            ruleViewArea.setText(ruleBuilder.build().toString());
        }
    }

    @FXML
    private void handleAddAge() {
        Response<Range<Integer>> response = viewManager.showRangeSelectorDialog("Select age range", 0, 100);
        if (response.okClicked) {
            ruleBuilder.withAge(response.content);
            ruleViewArea.setText(ruleBuilder.build().toString());
        }
    }
}
