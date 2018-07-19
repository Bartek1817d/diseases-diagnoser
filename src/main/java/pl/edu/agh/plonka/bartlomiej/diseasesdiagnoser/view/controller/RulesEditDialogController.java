package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.ViewManager;

public class RulesEditDialogController {

    @FXML
    private ListView<Rule> rulesView;

    private ViewManager viewManager;
    private Stage dialogStage;
    private boolean okClicked = false;
    private PatientsService patientsService;

    public void init(ViewManager viewManager, Stage dialogStage, PatientsService patientsService) {
        this.viewManager = viewManager;
        this.dialogStage = dialogStage;
        this.patientsService = patientsService;

        rulesView.setItems(patientsService.getRules());
    }

    @FXML
    private void initialize() {
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
        boolean okClicked = viewManager.showRuleEditDialog(patientsService);
    }

}
