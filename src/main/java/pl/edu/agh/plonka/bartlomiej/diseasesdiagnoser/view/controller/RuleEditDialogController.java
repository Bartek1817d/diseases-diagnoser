package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import com.google.common.collect.Range;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

import static java.util.Collections.emptyList;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.*;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.ObservableResourceFactory.getStringBinding;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.ObservableResourceFactory.getTranslation;

public class RuleEditDialogController implements ResponseController<Rule> {

    @FXML
    private TextField nameField;
    @FXML
    private TextArea viewArea;

    @FXML
    private Label nameLabel;
    @FXML
    private Button ageButton;
    @FXML
    private Button heightButton;
    @FXML
    private Button weightButton;
    @FXML
    private Button symptomButton;
    @FXML
    private Button negativeTestButton;
    @FXML
    private Button previousDiseaseButton;
    @FXML
    private Button diseaseButton;
    @FXML
    private Button treatmentButton;
    @FXML
    private Button testButton;
    @FXML
    private Button causeButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private RuleBuilder ruleBuilder;
    private PatientsService patientsService;
    private ViewManager viewManager;
    private Stage dialogStage;
    private boolean okClicked;

    public void init(ViewManager viewManager, Stage dialogStage, PatientsService patientsService) {
        this.viewManager = viewManager;
        this.dialogStage = dialogStage;
        this.patientsService = patientsService;
        this.ruleBuilder = new RuleBuilder(patientsService.getOntology().getClasses().get(PATIENT_CLASS));
        viewArea.textProperty().bind(ruleBuilder);
    }

    public void init(ViewManager viewManager, Stage dialogStage, PatientsService patientsService, Rule rule) {
        this.viewManager = viewManager;
        this.dialogStage = dialogStage;
        this.patientsService = patientsService;
        this.ruleBuilder = new RuleBuilder(rule);

        nameField.setText(rule.getName());
        viewArea.setText(ruleBuilder.build().toString());
        viewArea.textProperty().bind(ruleBuilder);
    }

    @FXML
    private void initialize() {
        bindTranslations();
    }

    private void bindTranslations() {
        nameLabel.textProperty().bind(getStringBinding("NAME"));
        ageButton.textProperty().bind(getStringBinding("AGE"));
        heightButton.textProperty().bind(getStringBinding("HEIGHT"));
        weightButton.textProperty().bind(getStringBinding("WEIGHT"));
        symptomButton.textProperty().bind(getStringBinding("SYMPTOMS"));
        negativeTestButton.textProperty().bind(getStringBinding("NEGATIVE_TESTS"));
        previousDiseaseButton.textProperty().bind(getStringBinding("PREVIOUS_DISEASES"));
        diseaseButton.textProperty().bind(getStringBinding("DISEASES"));
        treatmentButton.textProperty().bind(getStringBinding("TREATMENTS"));
        testButton.textProperty().bind(getStringBinding("TESTS"));
        causeButton.textProperty().bind(getStringBinding("CAUSES"));
        clearButton.textProperty().bind(getStringBinding("CLEAR"));
        okButton.textProperty().bind(getStringBinding("OK"));
        cancelButton.textProperty().bind(getStringBinding("CANCEL"));
    }

    @Override
    public Response<Rule> getResponse() {
        return new Response<>(okClicked, ruleBuilder.withName(nameField.getText().trim()).build());
    }

    @FXML
    private void handleOK() {
        if (nameField.getText().trim().isEmpty()) {
            viewManager.errorDialog(getTranslation("ERROR_CREATING_RULE"), null, getTranslation("ERROR_CREATING_RULE_EMPTY_NAME"));
            return;
        }
        if (viewArea.getText().trim().isEmpty()) {
            viewManager.errorDialog(getTranslation("ERROR_CREATING_RULE"), null, getTranslation("ERROR_CREATING_RULE_EMPTY_CONTEXT"));
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
        Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(SYMPTOM_CLASS),
                emptyList(), patientsService);
        if (response.okClicked) {
            ruleBuilder.withSymptoms(response.content);
        }
    }

    @FXML
    private void handleAddDiseases() {
        Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(DISEASE_CLASS),
                emptyList(), patientsService);
        if (response.okClicked) {
            ruleBuilder.withDiseases(response.content);
        }
    }

    @FXML
    private void handleAddAge() {
        Response<Range<Integer>> response = viewManager.showRangeSelectorDialog(getTranslation("SELECT_AGE_RANGE"), 0, 100);
        if (response.okClicked) {
            ruleBuilder.withAge(response.content);
        }
    }

    @FXML
    private void handleAddHeight() {
        Response<Range<Integer>> response = viewManager.showRangeSelectorDialog(getTranslation("SELECT_HEIGHT_RANGE"), 0, 300);
        if (response.okClicked) {
            ruleBuilder.withHeight(response.content);
        }
    }

    @FXML
    private void handleAddWeight() {
        Response<Range<Integer>> response = viewManager.showRangeSelectorDialog(getTranslation("SELECT_WEIGHT_RANGE"), 0, 200);
        if (response.okClicked) {
            ruleBuilder.withWeight(response.content);
        }
    }

    @FXML
    private void handleAddTreatments() {
        Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(TREATMENT_CLASS),
                emptyList(), patientsService);
        if (response.okClicked) {
            ruleBuilder.withTreatments(response.content);
        }
    }

    @FXML
    private void handleAddTests() {
        Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(TESTING_CLASS),
                emptyList(), patientsService);
        if (response.okClicked) {
            ruleBuilder.withTests(response.content);
        }
    }

    @FXML
    private void handleAddNegativeTests() {
        Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(TESTING_CLASS),
                emptyList(), patientsService);
        if (response.okClicked) {
            ruleBuilder.withNegativeTests(response.content);
        }
    }

    @FXML
    private void handleAddPreviousDiseases() {
        Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(DISEASE_CLASS),
                emptyList(), patientsService);
        if (response.okClicked) {
            ruleBuilder.withPreviousDiseases(response.content);
        }
    }

    @FXML
    private void handleAddCauses() {
        Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(CAUSE_CLASS),
                emptyList(), patientsService);
        if (response.okClicked) {
            ruleBuilder.withCauses(response.content);
        }
    }

    @FXML
    private void handleClear() {
        ruleBuilder.clear();
    }
}
