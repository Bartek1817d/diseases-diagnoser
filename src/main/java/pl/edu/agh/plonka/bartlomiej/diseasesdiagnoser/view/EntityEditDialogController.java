package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.Main;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.NamesUtils;

public class EntityEditDialogController {
    private Main main;
    private Stage dialogStage;
    private Entity entity;
    private boolean okClicked = false;

    @FXML
    private TextField entityName;
    @FXML
    private TextArea entityDescription;

    public void setMainApp(Main main) {
        this.main = main;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        entityName.setText(entity.getLabel());
        entityDescription.setText(entity.getComment());
    }

    @FXML
    private void handleOK() {
        if (isInputValid()) {
            String name = entityName.getText();
            if (entity.getID() == null) {
                String newID = NamesUtils.generateID(name);
                if (main.getOntology().containsID(newID)) {
                    int i = 1;
                    newID = NamesUtils.generateID(name, Integer.toString(i));
                    while (main.getOntology().containsID(newID)) {
                        newID = NamesUtils.generateID(name, Integer.toString(++i));
                    }
                    entity.setID(newID);
                } else
                    entity.setID(newID);
            }
            entity.setLabel(name);
            entity.setComment(entityDescription.getText());
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        if (entityName.getText() == null || entityName.getText().length() == 0) {
            Dialogs.errorDialog(dialogStage, "Error creating entity", null, "No valid name!\n");
            return false;
        } else
            return true;
    }
}
