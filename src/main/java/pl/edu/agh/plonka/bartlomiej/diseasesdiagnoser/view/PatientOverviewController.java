package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

        patientTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            firstNameLabel.setText(newValue != null ? newValue.getFirstName() : "");
            lastNameLabel.setText(newValue != null ? newValue.getLastName(): "");
            ageLabel.setText(newValue != null && newValue.getAge() != -1 ? Integer.toString(newValue.getAge()) : "");
            heightLabel.setText(newValue != null && newValue.getHeight() != -1 ? Integer.toString(newValue.getHeight()) : "");
            weightLabel.setText(newValue != null && newValue.getWeight() != -1 ? Integer.toString(newValue.getWeight()) : "");
            symptomsList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getSymptoms()) : FXCollections.emptyObservableList());
            inferredSymptomsList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getInferredSymptoms()) : FXCollections.emptyObservableList());
            diseasesList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getDiseases()) : FXCollections.emptyObservableList());
            inferredDiseasesList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getInferredDiseases()) : FXCollections.emptyObservableList());
            testsList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getTests()) : FXCollections.emptyObservableList());
            inferredTestsList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getInferredTests()) : FXCollections.emptyObservableList());
            treatmentsList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getTreatments()) : FXCollections.emptyObservableList());
            inferredTreatmentsList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getInferredTreatments()) : FXCollections.emptyObservableList());
            causesList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getCauses()) : FXCollections.emptyObservableList());
            inferredCausesList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getInferredCauses()) : FXCollections.emptyObservableList());
            negativeTestsList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getNegativeTests()) : FXCollections.emptyObservableList());
            previousAndCurrentDiseasesList.setItems(newValue != null ? FXCollections.observableArrayList(newValue.getPreviousAndCurrentDiseases()) : FXCollections.emptyObservableList());
        });

        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        ChangeListener<Entity> symptomSelectionListener =
                (observable, oldValue, newValue) -> symptomDescription.setText(newValue != null ? newValue.getComment() : "");
        symptomsList.getSelectionModel().selectedItemProperty().addListener(symptomSelectionListener);
        inferredSymptomsList.getSelectionModel().selectedItemProperty().addListener(symptomSelectionListener);

        ChangeListener<Entity> diseaseSelectionListener =
                (observable, oldValue, newValue) -> diseaseDescription.setText(newValue != null ? newValue.getComment() : "");
        diseasesList.getSelectionModel().selectedItemProperty().addListener(diseaseSelectionListener);
        inferredDiseasesList.getSelectionModel().selectedItemProperty().addListener(diseaseSelectionListener);

        ChangeListener<Entity> testSelectionListener =
                (observable, oldValue, newValue) -> testDescription.setText(newValue != null ? newValue.getComment() : "");
        testsList.getSelectionModel().selectedItemProperty().addListener(testSelectionListener);
        inferredTestsList.getSelectionModel().selectedItemProperty().addListener(testSelectionListener);

        ChangeListener<Entity> treatmentSelectionListener =
                (observable, oldValue, newValue) -> treatmentDescription.setText(newValue != null ? newValue.getComment() : "");
        treatmentsList.getSelectionModel().selectedItemProperty().addListener(treatmentSelectionListener);
        inferredTreatmentsList.getSelectionModel().selectedItemProperty().addListener(treatmentSelectionListener);

        ChangeListener<Entity> causeSelectionListener =
                (observable, oldValue, newValue) -> causeDescription.setText(newValue != null ? newValue.getComment() : "");
        causesList.getSelectionModel().selectedItemProperty().addListener(causeSelectionListener);
        inferredCausesList.getSelectionModel().selectedItemProperty().addListener(causeSelectionListener);

        ChangeListener<Entity> negativeTestSelectionListener =
                (observable, oldValue, newValue) -> negativeTestDescription.setText(newValue != null ? newValue.getComment() : "");
        negativeTestsList.getSelectionModel().selectedItemProperty().addListener(negativeTestSelectionListener);

        ChangeListener<Entity> prevDisSelLis =
                (observable, oldValue, newValue) -> previousAndCurrentDiseasesDescription.setText(newValue != null ? newValue.getComment() : "");
        previousAndCurrentDiseasesList.getSelectionModel().selectedItemProperty().addListener(prevDisSelLis);
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main
     */
    public void setMainApp(Main main, PatientsService patientsService) {
        this.main = main;
        this.patientsService = patientsService;

        // Add observable list data to the table
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
//                showPatientDetails(patient);
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
            Set<Entity> symptoms = new HashSet<Entity>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Symptom"),
                    selectedPatient.getSymptoms(), symptoms);
            if (okClicked) {
                selectedPatient.setSymptoms(symptoms);
                main.getOntology().updatePatient(selectedPatient);
//                showPatientDetails(selectedPatient);
            }
        } else {
            // Nothing selected.
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
//                showPatientDetails(selectedPatient);
            } else {
                // Nothing selected.
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Symptoms Selected",
                        "Please select symptoms in the table.");
            }
        } else {
            // Nothing selected.
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddDiseases() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> diseases = new HashSet<Entity>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Disease"),
                    selectedPatient.getDiseases(), diseases);
            if (okClicked) {
                selectedPatient.setDiseases(diseases);
                main.getOntology().updatePatient(selectedPatient);
//                showPatientDetails(selectedPatient);
            }
        } else {
            // Nothing selected.
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
//                showPatientDetails(selectedPatient);
            } else {
                // Nothing selected.
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Diseases Selected",
                        "Please select diseases in the table.");
            }
        } else {
            // Nothing selected.
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddTests() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> tests = new HashSet<Entity>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Testing"),
                    selectedPatient.getTests(), tests);
            if (okClicked) {
                selectedPatient.setTests(tests);
                main.getOntology().updatePatient(selectedPatient);
//                showPatientDetails(selectedPatient);
            }
        } else {
            // Nothing selected.
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
//                showPatientDetails(selectedPatient);
            } else {
                // Nothing selected.
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Tests Selected",
                        "Please select tests in the table.");
            }
        } else {
            // Nothing selected.
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddTreatments() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> treatments = new HashSet<Entity>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Treatment"),
                    selectedPatient.getTreatments(), treatments);
            if (okClicked) {
                selectedPatient.setTreatments(treatments);
                main.getOntology().updatePatient(selectedPatient);
//                showPatientDetails(selectedPatient);
            }
        } else {
            // Nothing selected.
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
            Set<Entity> causes = new HashSet<Entity>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Cause"),
                    selectedPatient.getCauses(), causes);
            if (okClicked) {
                selectedPatient.setCauses(causes);
                main.getOntology().updatePatient(selectedPatient);
//                showPatientDetails(selectedPatient);
            }
        } else {
            // Nothing selected.
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
//                showPatientDetails(selectedPatient);
            } else {
                // Nothing selected.
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Causes Selected",
                        "Please select causes in the table.");
            }
        } else {
            // Nothing selected.
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddNegativeTests() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> tests = new HashSet<Entity>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Testing"),
                    selectedPatient.getNegativeTests(), tests);
            if (okClicked) {
                selectedPatient.setNegativeTests(tests);
                main.getOntology().updatePatient(selectedPatient);
//                showPatientDetails(selectedPatient);
            }
        } else {
            // Nothing selected.
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
//                showPatientDetails(selectedPatient);
            } else {
                // Nothing selected.
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Negative Tests Selected",
                        "Please select negative tests in the table.");
            }
        } else {
            // Nothing selected.
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddPreviousAndCurrentDiseases() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Set<Entity> diseases = new HashSet<Entity>();
            boolean okClicked = main.showEntitiesEditDialog(main.getOntology().getClasses().get("Testing"),
                    selectedPatient.getPreviousAndCurrentDiseases(), diseases);
            if (okClicked) {
                selectedPatient.setPreviousAndCurrentDiseases(diseases);
                main.getOntology().updatePatient(selectedPatient);
//                showPatientDetails(selectedPatient);
            }
        } else {
            // Nothing selected.
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
//                showPatientDetails(selectedPatient);
            } else {
                // Nothing selected.
                Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Negative Tests Selected",
                        "Please select negative tests in the table.");
            }
        } else {
            // Nothing selected.
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }
}