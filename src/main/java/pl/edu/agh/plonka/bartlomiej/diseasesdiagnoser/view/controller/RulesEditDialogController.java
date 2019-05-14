package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.CreateRuleException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.RuleAlreadyExistsException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Response;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.ViewManager;

import java.util.Collection;

import static javafx.scene.control.SelectionMode.MULTIPLE;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.ControllerUtils.createDeleteEventHandler;

public class RulesEditDialogController {

    private static Float RULE_NAME_COLUMN_WIDTH = 200.0f;

    @FXML
    private TableView<Rule> rulesTable;
    @FXML
    private TableColumn<Rule, String> ruleNameColumn;
    @FXML
    private TableColumn<Rule, String> ruleContentColumn;

    private ViewManager viewManager;
    private Stage dialogStage;
    private boolean okClicked = false;
    private PatientsService patientsService;

    public void init(ViewManager viewManager, Stage dialogStage, PatientsService patientsService) {
        this.viewManager = viewManager;
        this.dialogStage = dialogStage;
        this.patientsService = patientsService;

        rulesTable.setItems(patientsService.getRules());
        rulesTable.setOnKeyPressed(createDeleteEventHandler(this::handleDeleteRules));
    }

    @FXML
    private void initialize() {
        rulesTable.getSelectionModel().setSelectionMode(MULTIPLE);
        rulesTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            ruleNameColumn.setPrefWidth(RULE_NAME_COLUMN_WIDTH);
            ruleContentColumn.setPrefWidth(rulesTable.getWidth() - RULE_NAME_COLUMN_WIDTH);
        });

        ruleNameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getName()));
        ruleContentColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().toString()));
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

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    public void handleNewRule() {
        Response<Rule> response = viewManager.showRuleEditDialog(null, patientsService);
        if (response.okClicked) {
            Rule rule = response.content;
            try {
                patientsService.addRule(rule);
            } catch (CreateRuleException | RuleAlreadyExistsException e) {
                viewManager.errorExceptionDialog("Failed to create rule", e.getMessage(), "Couldn't create rule " + rule.getName(), e);
            }
        }
    }

    @FXML
    public void handleEditRule() {
        Rule selectedRule = rulesTable.getSelectionModel().getSelectedItem();
        Response<Rule> response = viewManager.showRuleEditDialog(selectedRule, patientsService);
        if (response.okClicked) {
            Rule newRule = response.content;
            try {
                patientsService.deleteRule(selectedRule);
                patientsService.addRule(newRule);
            } catch (CreateRuleException | RuleAlreadyExistsException e) {
                viewManager.errorExceptionDialog("Failed to create rule", e.getMessage(), "Couldn't create rule " + newRule.getName(), e);
            }
        }
    }

    @FXML
    public void handleDeleteRules() {
        Collection<Rule> selectedRules = rulesTable.getSelectionModel().getSelectedItems();
        if (!selectedRules.isEmpty()) {
            patientsService.deleteRules(selectedRules);
        } else {
            viewManager.warningDialog("No Selection", "No Rule Selected",
                    "Please select a rule in the table.");
        }
    }

}
