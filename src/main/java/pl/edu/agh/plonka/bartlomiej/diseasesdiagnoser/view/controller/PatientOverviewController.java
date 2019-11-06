package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Response;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.PositiveIntegerStringBinding;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.ViewManager;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;

import static javafx.scene.control.SelectionMode.MULTIPLE;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.*;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.ControllerUtils.createDeleteEventHandler;

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

    private ViewManager viewManager;

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

        addDescriptionListeners();
        enableMultipleSelection();
        addListKeyBindings();
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param
     */
    public void init(ViewManager viewManager, PatientsService patientsService) {
        this.viewManager = viewManager;
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
            viewManager.warningDialog("No Selection", "No Patient Selected",
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
        boolean okClicked = viewManager.showPatientEditDialog(patient, patientsService);
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
            boolean okClicked = viewManager.showPatientEditDialog(patient, patientsService);
            if (okClicked) {
                patientsService.editPatient(patient);
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddSymptoms() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(SYMPTOM_CLASS),
                    selectedPatient.getSymptoms(), patientsService);
            if (response.okClicked) {
                selectedPatient.setSymptoms(response.content);
                patientsService.getOntology().updatePatient(selectedPatient);
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
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
                patientsService.getOntology().updatePatient(selectedPatient);
            } else {
                viewManager.warningDialog("No Selection", "No Symptoms Selected",
                        "Please select symptoms in the table.");
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddDiseases() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(DISEASE_CLASS),
                    selectedPatient.getDiseases(), patientsService);
            if (response.okClicked) {
                selectedPatient.setDiseases(response.content);
                patientsService.getOntology().updatePatient(selectedPatient);
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
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
                patientsService.getOntology().updatePatient(selectedPatient);
            } else {
                viewManager.warningDialog("No Selection", "No Diseases Selected",
                        "Please select diseases in the table.");
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddTests() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(TESTING_CLASS),
                    selectedPatient.getTests(), patientsService);
            if (response.okClicked) {
                selectedPatient.setTests(response.content);
                patientsService.getOntology().updatePatient(selectedPatient);
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
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
                patientsService.getOntology().updatePatient(selectedPatient);
            } else {
                viewManager.warningDialog("No Selection", "No Tests Selected",
                        "Please select tests in the table.");
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddTreatments() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(TREATMENT_CLASS),
                    selectedPatient.getTreatments(), patientsService);
            if (response.okClicked) {
                selectedPatient.setTreatments(response.content);
                patientsService.getOntology().updatePatient(selectedPatient);
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
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
                patientsService.getOntology().updatePatient(selectedPatient);
            } else {
                viewManager.warningDialog("No Selection", "No Treatments Selected",
                        "Please select treatments in the table.");
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddCauses() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(CAUSE_CLASS),
                    selectedPatient.getCauses(), patientsService);
            if (response.okClicked) {
                selectedPatient.setCauses(response.content);
                patientsService.getOntology().updatePatient(selectedPatient);
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
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
                patientsService.getOntology().updatePatient(selectedPatient);
            } else {
                viewManager.warningDialog("No Selection", "No Causes Selected",
                        "Please select causes in the table.");
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddNegativeTests() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(TESTING_CLASS),
                    selectedPatient.getNegativeTests(), patientsService);
            if (response.okClicked) {
                selectedPatient.setNegativeTests(response.content);
                patientsService.getOntology().updatePatient(selectedPatient);
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
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
                patientsService.getOntology().updatePatient(selectedPatient);
            } else {
                viewManager.warningDialog("No Selection", "No Negative Tests Selected",
                        "Please select negative tests in the table.");
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleAddPreviousAndCurrentDiseases() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Response<Collection<Entity>> response = viewManager.showEntitiesEditDialog(patientsService.getOntology().getClasses().get(DISEASE_CLASS),
                    selectedPatient.getPreviousAndCurrentDiseases(), patientsService);
            if (response.okClicked) {
                selectedPatient.setPreviousAndCurrentDiseases(response.content);
                patientsService.getOntology().updatePatient(selectedPatient);
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    @FXML
    private void handleDeletePreviousAndCurrentDiseases() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            ObservableList<Entity> previousAndCurrentDiseases = previousAndCurrentDiseasesList.getSelectionModel().getSelectedItems();
            if (!previousAndCurrentDiseases.isEmpty()) {
                selectedPatient.getPreviousAndCurrentDiseases().removeAll(previousAndCurrentDiseases);
                patientsService.getOntology().updatePatient(selectedPatient);
            } else {
                viewManager.warningDialog("No Selection", "No Negative Tests Selected",
                        "Please select negative tests in the table.");
            }
        } else {
            viewManager.warningDialog("No Selection", "No Patient Selected",
                    "Please select a patient in the table.");
        }
    }

    private void bindPatientProperties(Patient patient) {
        firstNameLabel.textProperty().bind(patient.getObservableFirstName());
        lastNameLabel.textProperty().bind(patient.getObservableLastName());
        ageLabel.textProperty().bind(new PositiveIntegerStringBinding(patient.getObservableAge()));
        heightLabel.textProperty().bind(new PositiveIntegerStringBinding(patient.getObservableHeight()));
        weightLabel.textProperty().bind(new PositiveIntegerStringBinding(patient.getObservableWeight()));

        symptomsList.setCellFactory(list -> new ListCell<Entity>() {
            @Override
            protected void updateItem(Entity item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null)
                    textProperty().bind(item.getObservableLabel());
            }
        });

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

    private void addDescriptionListeners() {
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

    private void enableMultipleSelection() {
        symptomsList.getSelectionModel().setSelectionMode(MULTIPLE);
        inferredSymptomsList.getSelectionModel().setSelectionMode(MULTIPLE);
        diseasesList.getSelectionModel().setSelectionMode(MULTIPLE);
        inferredDiseasesList.getSelectionModel().setSelectionMode(MULTIPLE);
        testsList.getSelectionModel().setSelectionMode(MULTIPLE);
        inferredTestsList.getSelectionModel().setSelectionMode(MULTIPLE);
        treatmentsList.getSelectionModel().setSelectionMode(MULTIPLE);
        inferredTreatmentsList.getSelectionModel().setSelectionMode(MULTIPLE);
        causesList.getSelectionModel().setSelectionMode(MULTIPLE);
        inferredCausesList.getSelectionModel().setSelectionMode(MULTIPLE);
        negativeTestsList.getSelectionModel().setSelectionMode(MULTIPLE);
        previousAndCurrentDiseasesList.getSelectionModel().setSelectionMode(MULTIPLE);
    }

    private void addListKeyBindings() {
        symptomsList.setOnKeyPressed(createDeleteEventHandler(this::handleDeleteSymptoms));
        diseasesList.setOnKeyPressed(createDeleteEventHandler(this::handleDeleteDiseases));
        testsList.setOnKeyPressed(createDeleteEventHandler(this::handleDeleteTests));
        treatmentsList.setOnKeyPressed(createDeleteEventHandler(this::handleDeleteTreatments));
        causesList.setOnKeyPressed(createDeleteEventHandler(this::handleDeleteCauses));
        negativeTestsList.setOnKeyPressed(createDeleteEventHandler(this::handleDeleteNegativeTests));
        previousAndCurrentDiseasesList.setOnKeyPressed(createDeleteEventHandler(this::handleDeletePreviousAndCurrentDiseases));
    }
}
