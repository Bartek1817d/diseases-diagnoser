package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.Main;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.PositiveIntegerStringBinding;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class PatientOverviewController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private PatientsService patientsService;

    @FXML
    private TableView<Patient> patientTable;
    @FXML
    private TableColumn<Patient, String> firstNameColumn;
    @FXML
    private TableColumn<Patient, String> lastNameColumn;

    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label heightLabel;
    @FXML
    private Label weightLabel;

    @FXML
    private ListView<Entity> symptomsList;
    @FXML
    private ListView<Entity> inferredSymptomsList;
    @FXML
    private ListView<Entity> diseasesList;
    @FXML
    private ListView<Entity> inferredDiseasesList;
    @FXML
    private ListView<Entity> testsList;
    @FXML
    private ListView<Entity> inferredTestsList;
    @FXML
    private ListView<Entity> treatmentsList;
    @FXML
    private ListView<Entity> inferredTreatmentsList;
    @FXML
    private ListView<Entity> causesList;
    @FXML
    private ListView<Entity> inferredCausesList;
    @FXML
    private ListView<Entity> negativeTestsList;
    @FXML
    private ListView<Entity> previousAndCurrentDiseasesList;
    @FXML
    private TextArea symptomDescription;
    @FXML
    private TextArea diseaseDescription;
    @FXML
    private TextArea testDescription;
    @FXML
    private TextArea treatmentDescription;
    @FXML
    private TextArea causeDescription;
    @FXML
    private TextArea negativeTestDescription;
    @FXML
    private TextArea previousAndCurrentDiseasesDescription;

    // Reference to the main application.
    private Main main;

    /**
     * The constructor. The constructor is called before the initialize()
     * method.
     */
    public PatientOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().getObservableFirstName());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().getObservableLastName());

        patientTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                bindPatientProperties(newValue);
            } else {
                unbindPatientProperties();
                clearPersonalDataLabels();
            }
        });


        Function<TextArea, ChangeListener<Entity>> listenerGenerator = textArea -> (observable, oldValue, newValue) -> {
            if (newValue != null) {
                textArea.textProperty().bind(newValue.getObservableComment());
            } else {
                textArea.textProperty().unbind();
                textArea.setText("");
            }

        };
        symptomsList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(symptomDescription));
        inferredSymptomsList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(symptomDescription));
        diseasesList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(diseaseDescription));
        inferredDiseasesList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(diseaseDescription));
        testsList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(testDescription));
        inferredTestsList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(testDescription));
        treatmentsList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(treatmentDescription));
        inferredTreatmentsList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(treatmentDescription));
        causesList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(causeDescription));
        inferredCausesList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(causeDescription));
        negativeTestsList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(negativeTestDescription));
        previousAndCurrentDiseasesList.getSelectionModel().selectedItemProperty().addListener(listenerGenerator.apply(previousAndCurrentDiseasesDescription));
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main
     */
    public void setMainApp(Main main, PatientsService patientsService) {
        this.main = main;
        this.patientsService = patientsService;

        patientTable.setItems(patientsService.getPatients());
    }

    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void handleDeletePatient() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            patientsService.deletePatient(selectedPatient);
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new patient.
     */
    @FXML
    private void handleNewPatient() throws IOException {
        Patient patient = new Patient();
        boolean okClicked = main.showPatientEditDialog(patient);
        if (okClicked) {
            patientsService.addPatient(patient);
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected patient.
     */
    @FXML
    private void handleEditPatient() throws IOException {
        Patient patient = patientTable.getSelectionModel().getSelectedItem();
        if (patient != null) {
            boolean okClicked = main.showPatientEditDialog(patient);
            if (okClicked) {
                patientsService.editPatient(patient);
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddSymptoms() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> symptoms = new HashSet<>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Symptom"),
                    selectedPatient.getSymptoms(), symptoms);
            if (okClicked) {
                selectedPatient.setSymptoms(symptoms);
                main.getOntology().updatePatient(selectedPatient);
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleDeleteSymptoms() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            ObservableList<Entity> symptoms = symptomsList.getSelectionModel().getSelectedItems();
            if (!symptoms.isEmpty()) {
                selectedPatient.getSymptoms().removeAll(symptoms);
                main.getOntology().updatePatient(selectedPatient);
            } else {
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Symptoms Selected",
                        "Please select symptoms in the table.");
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddDiseases() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> diseases = new HashSet<>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Disease"),
                    selectedPatient.getDiseases(), diseases);
            if (okClicked) {
                selectedPatient.setDiseases(diseases);
                main.getOntology().updatePatient(selectedPatient);
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleDeleteDiseases() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            ObservableList<Entity> diseases = diseasesList.getSelectionModel().getSelectedItems();
            if (!diseases.isEmpty()) {
                selectedPatient.getDiseases().removeAll(diseases);
                main.getOntology().updatePatient(selectedPatient);
            } else {
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Diseases Selected",
                        "Please select diseases in the table.");
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddTests() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> tests = new HashSet<>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Testing"),
                    selectedPatient.getTests(), tests);
            if (okClicked) {
                selectedPatient.setTests(tests);
                main.getOntology().updatePatient(selectedPatient);
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleDeleteTests() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            ObservableList<Entity> tests = testsList.getSelectionModel().getSelectedItems();
            if (!tests.isEmpty()) {
                selectedPatient.getTests().removeAll(tests);
                main.getOntology().updatePatient(selectedPatient);
            } else {
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Tests Selected",
                        "Please select tests in the table.");
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddTreatments() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> treatments = new HashSet<>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Treatment"),
                    selectedPatient.getTreatments(), treatments);
            if (okClicked) {
                selectedPatient.setTreatments(treatments);
                main.getOntology().updatePatient(selectedPatient);
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleDeleteTreatments() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            ObservableList<Entity> treatments = treatmentsList.getSelectionModel().getSelectedItems();
            if (!treatments.isEmpty()) {
                selectedPatient.getTreatments().removeAll(treatments);
                main.getOntology().updatePatient(selectedPatient);
//                showPatientDetails(selectedPatient);
            } else {
                // Nothing selected.
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Treatments Selected",
                        "Please select treatments in the table.");
            }
        } else {
            // Nothing selected.
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddCauses() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> causes = new HashSet<>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Cause"),
                    selectedPatient.getCauses(), causes);
            if (okClicked) {
                selectedPatient.setCauses(causes);
                main.getOntology().updatePatient(selectedPatient);
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleDeleteCauses() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            ObservableList<Entity> causes = causesList.getSelectionModel().getSelectedItems();
            if (!causes.isEmpty()) {
                selectedPatient.getCauses().removeAll(causes);
                main.getOntology().updatePatient(selectedPatient);
            } else {
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Causes Selected",
                        "Please select causes in the table.");
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddNegativeTests() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> tests = new HashSet<>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Testing"),
                    selectedPatient.getNegativeTests(), tests);
            if (okClicked) {
                selectedPatient.setNegativeTests(tests);
                main.getOntology().updatePatient(selectedPatient);
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleDeleteNegativeTests() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            ObservableList<Entity> negativeTests = negativeTestsList.getSelectionModel().getSelectedItems();
            if (!negativeTests.isEmpty()) {
                selectedPatient.getNegativeTests().removeAll(negativeTests);
                main.getOntology().updatePatient(selectedPatient);
            } else {
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Negative Tests Selected",
                        "Please select negative tests in the table.");
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddPreviousAndCurrentDiseases() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> diseases = new HashSet<>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Testing"),
                    selectedPatient.getPreviousAndCurrentDiseases(), diseases);
            if (okClicked) {
                selectedPatient.setPreviousAndCurrentDiseases(diseases);
                main.getOntology().updatePatient(selectedPatient);
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleDeletePreviousAndCurrentDiseases() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            ObservableList<Entity> negativeTests = negativeTestsList.getSelectionModel().getSelectedItems();
            if (!negativeTests.isEmpty()) {
                selectedPatient.getNegativeTests().removeAll(negativeTests);
                selectedPatient.getTests().removeAll(negativeTests);
                main.getOntology().updatePatient(selectedPatient);
            } else {
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Negative Tests Selected",
                        "Please select negative tests in the table.");
            }
        } else {
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    private void bindPatientProperties(Patient patient) {
        firstNameLabel.textProperty().bind(patient.getObservableFirstName());
        lastNameLabel.textProperty().bind(patient.getObservableLastName());
        ageLabel.textProperty().bind(new PositiveIntegerStringBinding(patient.getObservableAge()));
        heightLabel.textProperty().bind(new PositiveIntegerStringBinding(patient.getObservableHeight()));
        weightLabel.textProperty().bind(new PositiveIntegerStringBinding(patient.getObservableWeight()));

        symptomsList.setItems(patient.getSymptoms());
        inferredSymptomsList.setItems(patient.getInferredSymptoms());
        diseasesList.setItems(patient.getDiseases());
        inferredDiseasesList.setItems(patient.getInferredDiseases());
        testsList.setItems(patient.getTests());
        inferredTestsList.setItems(patient.getInferredTests());
        treatmentsList.setItems(patient.getTreatments());
        inferredTreatmentsList.setItems(patient.getInferredTreatments());
        causesList.setItems(patient.getCauses());
        inferredCausesList.setItems(patient.getInferredCauses());
        negativeTestsList.setItems(patient.getNegativeTests());
        previousAndCurrentDiseasesList.setItems(patient.getPreviousAndCurrentDiseases());
    }

    private void unbindPatientProperties() {
        firstNameLabel.textProperty().unbind();
        lastNameLabel.textProperty().unbind();
        ageLabel.textProperty().unbind();
        heightLabel.textProperty().unbind();
        weightLabel.textProperty().unbind();

        symptomsList.setItems(FXCollections.emptyObservableList());
        inferredSymptomsList.setItems(FXCollections.emptyObservableList());
        diseasesList.setItems(FXCollections.emptyObservableList());
        inferredDiseasesList.setItems(FXCollections.emptyObservableList());
        testsList.setItems(FXCollections.emptyObservableList());
        inferredTestsList.setItems(FXCollections.emptyObservableList());
        treatmentsList.setItems(FXCollections.emptyObservableList());
        inferredTreatmentsList.setItems(FXCollections.emptyObservableList());
        causesList.setItems(FXCollections.emptyObservableList());
        inferredCausesList.setItems(FXCollections.emptyObservableList());
        negativeTestsList.setItems(FXCollections.emptyObservableList());
        previousAndCurrentDiseasesList.setItems(FXCollections.emptyObservableList());
    }

    private void clearPersonalDataLabels() {
        firstNameLabel.setText("");
        lastNameLabel.setText("");
        ageLabel.setText("");
        heightLabel.setText("");
        weightLabel.setText("");
    }
}
