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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PatientOverviewController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

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
        // Initialize the patient table with the two columns.
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        ChangeListener<Entity> symptomSelectionListener = new ChangeListener<Entity>() {
            @Override
            public void changed(ObservableValue<? extends Entity> observable, Entity oldValue, Entity newValue) {
                if (newValue != null)
                    symptomDescription.setText(newValue.getComment());
                else
                    symptomDescription.setText("");
            }
        };
        symptomsList.getSelectionModel().selectedItemProperty().addListener(symptomSelectionListener);
        inferredSymptomsList.getSelectionModel().selectedItemProperty().addListener(symptomSelectionListener);

        ChangeListener<Entity> diseaseSelectionListener = new ChangeListener<Entity>() {
            @Override
            public void changed(ObservableValue<? extends Entity> observable, Entity oldValue, Entity newValue) {
                if (newValue != null)
                    diseaseDescription.setText(newValue.getComment());
                else
                    diseaseDescription.setText("");
            }
        };
        diseasesList.getSelectionModel().selectedItemProperty().addListener(diseaseSelectionListener);
        inferredDiseasesList.getSelectionModel().selectedItemProperty().addListener(diseaseSelectionListener);

        ChangeListener<Entity> testSelectionListener = new ChangeListener<Entity>() {
            @Override
            public void changed(ObservableValue<? extends Entity> observable, Entity oldValue, Entity newValue) {
                if (newValue != null)
                    testDescription.setText(newValue.getComment());
                else
                    testDescription.setText("");
            }
        };
        testsList.getSelectionModel().selectedItemProperty().addListener(testSelectionListener);
        inferredTestsList.getSelectionModel().selectedItemProperty().addListener(testSelectionListener);

        ChangeListener<Entity> treatmentSelectionListener = new ChangeListener<Entity>() {
            @Override
            public void changed(ObservableValue<? extends Entity> observable, Entity oldValue, Entity newValue) {
                if (newValue != null)
                    treatmentDescription.setText(newValue.getComment());
                else
                    treatmentDescription.setText("");
            }
        };
        treatmentsList.getSelectionModel().selectedItemProperty().addListener(treatmentSelectionListener);
        inferredTreatmentsList.getSelectionModel().selectedItemProperty().addListener(treatmentSelectionListener);

        ChangeListener<Entity> causeSelectionListener = new ChangeListener<Entity>() {
            @Override
            public void changed(ObservableValue<? extends Entity> observable, Entity oldValue, Entity newValue) {
                if (newValue != null)
                    causeDescription.setText(newValue.getComment());
                else
                    causeDescription.setText("");
            }
        };
        causesList.getSelectionModel().selectedItemProperty().addListener(causeSelectionListener);
        inferredCausesList.getSelectionModel().selectedItemProperty().addListener(causeSelectionListener);

        ChangeListener<Entity> negativeTestSelectionListener = new ChangeListener<Entity>() {
            @Override
            public void changed(ObservableValue<? extends Entity> observable, Entity oldValue, Entity newValue) {
                if (newValue != null)
                    negativeTestDescription.setText(newValue.getComment());
                else
                    negativeTestDescription.setText("");
            }
        };
        negativeTestsList.getSelectionModel().selectedItemProperty().addListener(negativeTestSelectionListener);

        ChangeListener<Entity> prevDisSelLis = new ChangeListener<Entity>() {
            @Override
            public void changed(ObservableValue<? extends Entity> observable, Entity oldValue, Entity newValue) {
                if (newValue != null)
                    previousAndCurrentDiseasesDescription.setText(newValue.getComment());
                else
                    previousAndCurrentDiseasesDescription.setText("");
            }
        };
        previousAndCurrentDiseasesList.getSelectionModel().selectedItemProperty().addListener(prevDisSelLis);

        // Clear patient details.
        showPatientDetails(null);

        // Listen for selection changes and show the patient details when
        // changed.
        patientTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showPatientDetails(newValue));
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main
     */
    public void setMainApp(Main main) {
        this.main = main;

        // Add observable list data to the table
        patientTable.setItems(main.getPatientData());
    }

    /**
     * Fills all text fields to show details about the patient. If the specified
     * patient is null, all text fields are cleared.
     *
     * @param patient the patient or null
     */
    private void showPatientDetails(Patient patient) {
        if (patient != null) {
            // Fill the labels with info from the patient object.
            firstNameLabel.setText(patient.getFirstName() == null ? "" : patient.getFirstName());
            lastNameLabel.setText(patient.getLastName() == null ? "" : patient.getLastName());
            ageLabel.setText(patient.getAge() == -1 ? "" : Integer.toString(patient.getAge()));
            heightLabel.setText(patient.getHeight() == -1 ? "" : Integer.toString(patient.getHeight()));
            weightLabel.setText(patient.getWeight() == -1 ? "" : Integer.toString(patient.getWeight()));
            ObservableList<Entity> items;
            items = FXCollections.observableArrayList(patient.getSymptoms());
            symptomsList.setItems(items);
            items = FXCollections.observableArrayList(patient.getInferredSymptoms());
            inferredSymptomsList.setItems(items);
            items = FXCollections.observableArrayList(patient.getDiseases());
            diseasesList.setItems(items);
            items = FXCollections.observableArrayList(patient.getInferredDiseases());
            inferredDiseasesList.setItems(items);
            items = FXCollections.observableArrayList(patient.getTests());
            testsList.setItems(items);
            items = FXCollections.observableArrayList(patient.getInferredTests());
            inferredTestsList.setItems(items);
            items = FXCollections.observableArrayList(patient.getTreatments());
            treatmentsList.setItems(items);
            items = FXCollections.observableArrayList(patient.getInferredTreatments());
            inferredTreatmentsList.setItems(items);
            items = FXCollections.observableArrayList(patient.getCauses());
            causesList.setItems(items);
            items = FXCollections.observableArrayList(patient.getInferredCauses());
            inferredCausesList.setItems(items);
            items = FXCollections.observableArrayList(patient.getNegativeTests());
            negativeTestsList.setItems(items);
            items = FXCollections.observableArrayList(patient.getPreviousAndCurrentDiseases());
            previousAndCurrentDiseasesList.setItems(items);
        } else {
            // Patient is null, remove all the text.
            firstNameLabel.setText("");
            lastNameLabel.setText("");
            ageLabel.setText("");
            heightLabel.setText("");
            weightLabel.setText("");
            symptomsList.getItems().clear();
            inferredSymptomsList.getItems().clear();
            diseasesList.getItems().clear();
            inferredDiseasesList.getItems().clear();
            testsList.getItems().clear();
            inferredTestsList.getItems().clear();
            treatmentsList.getItems().clear();
            inferredTreatmentsList.getItems().clear();
            causesList.getItems().clear();
            inferredCausesList.getItems().clear();
            negativeTestsList.getItems().clear();
            previousAndCurrentDiseasesList.getItems().clear();
            symptomDescription.setText("");
            diseaseDescription.setText("");
            testDescription.setText("");
            treatmentDescription.setText("");
            causeDescription.setText("");
            negativeTestDescription.setText("");
            previousAndCurrentDiseasesDescription.clear();
        }
    }

    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void handleDeletePatient() {
        // int selectedIndex =
        // patientTable.getSelectionModel().getSelectedIndex();
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            // patientTable.getItems().remove(selectedIndex);
            patientTable.getItems().remove(selectedPatient);
            main.getOntology().deleteEntity(selectedPatient);
        } else {
            // Nothing selected.
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

        Patient tempPatient = new Patient();
        boolean okClicked = main.showPatientEditDialog(tempPatient);
        if (okClicked) {
            main.getPatientData().add(tempPatient);
            main.getOntology().addPatient(tempPatient);
        }

    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected patient.
     */
    @FXML
    private void handleEditPatient() throws IOException {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            boolean okClicked = main.showPatientEditDialog(selectedPatient);
            if (okClicked) {
                main.getOntology().updatePatient(selectedPatient);
                showPatientDetails(selectedPatient);
            }

        } else {
            // Nothing selected.
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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
                showPatientDetails(selectedPatient);
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