package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.ObservableResourceFactory.getStringBinding;

public class RulesEditDialogController {

    private static Float RULE_NAME_COLUMN_WIDTH = 200.0f;

    @FXML
    private TableView<Rule> rulesTable;
    @FXML
    private TableColumn<Rule, String> nameColumn;
    @FXML
    private TableColumn<Rule, String> contentColumn;

    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

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
            nameColumn.setPrefWidth(RULE_NAME_COLUMN_WIDTH);
            contentColumn.setPrefWidth(rulesTable.getWidth() - RULE_NAME_COLUMN_WIDTH);
        });

        nameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getName()));
        contentColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().toString()));

        bindTranslations();
    }

    @FXML
    private void handleOK() {
        okClicked = true;
        dialogStage.close();
    }

    private void bindTranslations() {
        nameColumn.textProperty().bind(getStringBinding("NAME"));
        contentColumn.textProperty().bind(getStringBinding("CONTENT"));
        newButton.textProperty().bind(getStringBinding("NEW"));
        editButton.textProperty().bind(getStringBinding("EDIT"));
        deleteButton.textProperty().bind(getStringBinding("DELETE"));
        okButton.textProperty().bind(getStringBinding("OK"));
        cancelButton.textProperty().bind(getStringBinding("CANCEL"));
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
