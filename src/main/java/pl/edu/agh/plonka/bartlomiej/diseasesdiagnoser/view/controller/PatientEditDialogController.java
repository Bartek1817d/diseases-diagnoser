package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.Main;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.NamesUtils;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.Dialogs;

/**
 * Dialog to edit details of a patient.
 *
 * @author Bartłomiej Płonka
 */
public class PatientEditDialogController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField weightField;
    @FXML
    private TextField placeOfResidence;

    private Stage dialogStage;
    private Patient patient;
    private boolean okClicked = false;
    private Main main;
    private PatientsService patientsService;

    public void setMainApp(Main main, PatientsService patientsService) {
        this.main = main;
        this.patientsService = patientsService;
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
     * Sets the patient to be edited in the dialog.
     *
     * @param patient
     */
    public void setPatient(Patient patient) {
        this.patient = patient;

        firstNameField.setText(patient.getFirstName());
        lastNameField.setText(patient.getLastName());
        if (patient.getAge() > 0)
            ageField.setText(Integer.toString(patient.getAge()));
        if (patient.getHeight() > 0)
            heightField.setText(Integer.toString(patient.getHeight()));
        if (patient.getWeight() > 0)
            weightField.setText(Integer.toString(patient.getWeight()));
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            String fn = firstNameField.getText();
            String ln = lastNameField.getText();
            if (patient.getID() == null) {
                if (patientsService.getOntology().containsID(NamesUtils.generateID(fn, ln))) {
                    int i = 1;
                    String newID = NamesUtils.generateID(fn, ln, Integer.toString(i));
                    while (patientsService.getOntology().containsID(newID)) {
                        newID = NamesUtils.generateID(fn, ln, Integer.toString(++i));
                    }
                    patient.setID(newID);
                } else
                    patient.setID(NamesUtils.generateID(fn, ln));
            }
            patient.setFirstName(firstNameField.getText());
            patient.setLastName(lastNameField.getText());
            patient.setAge(Integer.parseInt(ageField.getText()));
            patient.setHeight(Integer.parseInt(heightField.getText()));
            patient.setWeight(Integer.parseInt(weightField.getText()));
            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (firstNameField.getText() == null || firstNameField.getText().length() == 0) {
            errorMessage += "No valid first name!\n";
        }
        if (lastNameField.getText() == null || lastNameField.getText().length() == 0) {
            errorMessage += "No valid last name!\n";
        }
        if (ageField.getText() == null || ageField.getText().length() == 0) {
            errorMessage += "No valid age!\n";
        } else {
            try {
                Integer.parseInt(ageField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid age (must be an integer)!\n";
            }
        }
        if (heightField.getText() == null || heightField.getText().length() == 0) {
            errorMessage += "No valid height!\n";
        } else {
            try {
                Integer.parseInt(heightField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid height (must be an integer)!\n";
            }
        }
        if (weightField.getText() == null || weightField.getText().length() == 0) {
            errorMessage += "No weight!\n";
        } else {
            try {
                Integer.parseInt(weightField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid weight (must be an integer)!\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.errorDialog(dialogStage, "Error creating patient", null, errorMessage);
            return false;
        }
    }
}