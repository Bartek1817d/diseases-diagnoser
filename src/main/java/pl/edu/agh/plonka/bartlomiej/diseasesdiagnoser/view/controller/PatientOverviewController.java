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
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.ObservableResourceFactory.getStringBinding;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.ObservableResourceFactory.getTranslation;

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
    private Tab personalDataTab;
    @FXML
    private Tab symptomTab;
    @FXML
    private Tab negativeTestTab;
    @FXML
    private Tab previousDiseaseTab;
    @FXML
    private Tab diseaseTab;
    @FXML
    private Tab testTab;
    @FXML
    private Tab treatmentTab;
    @FXML
    private Tab causeTab;


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
    private Label firstNameLabelL;
    @FXML
    private Label lastNameLabelL;
    @FXML
    private Label ageLabelL;
    @FXML
    private Label heightLabelL;
    @FXML
    private Label weightLabelL;

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

    @FXML
    private Button newPatientButton;
    @FXML
    private Button editPatientButton;
    @FXML
    private Button deletePatientButton;
    @FXML
    private Button newSymptomButton;
    @FXML
    private Button deleteSymptomButton;
    @FXML
    private Button newNegativeTestButton;
    @FXML
    private Button deleteNegativeTestButton;
    @FXML
    private Button newPreviousDiseaseButton;
    @FXML
    private Button deletePreviousDiseaseButton;
    @FXML
    private Button newDiseaseButton;
    @FXML
    private Button deleteDiseaseButton;
    @FXML
    private Button newTestButton;
    @FXML
    private Button deleteTestButton;
    @FXML
    private Button newTreatmentButton;
    @FXML
    private Button deleteTreatmentButton;
    @FXML
    private Button newCauseButton;
    @FXML
    private Button deleteCauseButton;

    @FXML
    private Label assertedSymptomsLabel;
    @FXML
    private Label inferredSymptomsLabel;
    @FXML
    private Label assertedNegativeTestsLabel;
    @FXML
    private Label inferredNegativeTestsLabel;
    @FXML
    private Label assertedPreviousDiseasesLabel;
    @FXML
    private Label inferredPreviousDiseasesLabel;
    @FXML
    private Label assertedDiseasesLabel;
    @FXML
    private Label inferredDiseasesLabel;
    @FXML
    private Label assertedTestsLabel;
    @FXML
    private Label inferredTestsLabel;
    @FXML
    private Label assertedTreatmentsLabel;
    @FXML
    private Label inferredTreatmentsLabel;
    @FXML
    private Label assertedCausesLabel;
    @FXML
    private Label inferredCausesLabel;

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

        bindResourceBundle();
        setListViewCellFactories();
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
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
                viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_SYMPTOMS_SELECTED"),
                        getTranslation("SELECT_SYMPTOMS"));
            }
        } else {
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
                viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_DISEASES_SELECTED"),
                        getTranslation("SELECT_DISEASES"));
            }
        } else {
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
                viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_TESTS_SELECTED"),
                        getTranslation("SELECT_TESTS"));
            }
        } else {
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
                viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_TREATMENTS_SELECTED"),
                        getTranslation("SELECT_TREATMENTS"));
            }
        } else {
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
                viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_CAUSES_SELECTED"),
                        getTranslation("SELECT_CAUSES"));
            }
        } else {
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
                viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_NEGATIVE_TESTS_SELECTED"),
                        getTranslation("SELECT_NEGATIVE_TESTS"));
            }
        } else {
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
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
                viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PREVIOUS_DISEASES_SELECTED"),
                        getTranslation("SELECT_PREVIOUS_DISEASES"));
            }
        } else {
            viewManager.warningDialog(getTranslation("NO_SELECTION"), getTranslation("NO_PATIENT_SELECTED"),
                    getTranslation("SELECT_PATIENT"));
        }
    }

    private void setListViewCellFactories() {
        symptomsList.setCellFactory(PatientOverviewController::listViewCellFactory);
        inferredSymptomsList.setCellFactory(PatientOverviewController::listViewCellFactory);
        diseasesList.setCellFactory(PatientOverviewController::listViewCellFactory);
        inferredDiseasesList.setCellFactory(PatientOverviewController::listViewCellFactory);
        testsList.setCellFactory(PatientOverviewController::listViewCellFactory);
        inferredTestsList.setCellFactory(PatientOverviewController::listViewCellFactory);
        treatmentsList.setCellFactory(PatientOverviewController::listViewCellFactory);
        inferredTreatmentsList.setCellFactory(PatientOverviewController::listViewCellFactory);
        causesList.setCellFactory(PatientOverviewController::listViewCellFactory);
        inferredCausesList.setCellFactory(PatientOverviewController::listViewCellFactory);
        negativeTestsList.setCellFactory(PatientOverviewController::listViewCellFactory);
        previousAndCurrentDiseasesList.setCellFactory(PatientOverviewController::listViewCellFactory);
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

    private static ListCell<Entity> listViewCellFactory(ListView<Entity> list) {
        return new ListCell<Entity>() {
            @Override
            protected void updateItem(Entity item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null)
                    textProperty().bind(item.getObservableLabel());
            }
        };
    }

    private void bindResourceBundle() {
        firstNameColumn.textProperty().bind(getStringBinding("FIRST_NAME"));
        lastNameColumn.textProperty().bind(getStringBinding("LAST_NAME"));
        firstNameLabelL.textProperty().bind(getStringBinding("FIRST_NAME"));
        lastNameLabelL.textProperty().bind(getStringBinding("LAST_NAME"));
        ageLabelL.textProperty().bind(getStringBinding("AGE"));
        heightLabelL.textProperty().bind(getStringBinding("HEIGHT"));
        weightLabelL.textProperty().bind(getStringBinding("WEIGHT"));
        newPatientButton.textProperty().bind(getStringBinding("NEW"));
        editPatientButton.textProperty().bind(getStringBinding("EDIT"));
        deletePatientButton.textProperty().bind(getStringBinding("DELETE"));
        newSymptomButton.textProperty().bind(getStringBinding("NEW_EDIT"));
        deleteSymptomButton.textProperty().bind(getStringBinding("DELETE"));
        newNegativeTestButton.textProperty().bind(getStringBinding("NEW_EDIT"));
        deleteNegativeTestButton.textProperty().bind(getStringBinding("DELETE"));
        newPreviousDiseaseButton.textProperty().bind(getStringBinding("NEW_EDIT"));
        deletePreviousDiseaseButton.textProperty().bind(getStringBinding("DELETE"));
        newDiseaseButton.textProperty().bind(getStringBinding("NEW_EDIT"));
        deleteDiseaseButton.textProperty().bind(getStringBinding("DELETE"));
        newTestButton.textProperty().bind(getStringBinding("NEW_EDIT"));
        deleteTestButton.textProperty().bind(getStringBinding("DELETE"));
        newTreatmentButton.textProperty().bind(getStringBinding("NEW_EDIT"));
        deleteTreatmentButton.textProperty().bind(getStringBinding("DELETE"));
        newCauseButton.textProperty().bind(getStringBinding("NEW_EDIT"));
        deleteCauseButton.textProperty().bind(getStringBinding("DELETE"));
        assertedSymptomsLabel.textProperty().bind(getStringBinding("ASSERTED"));
        inferredSymptomsLabel.textProperty().bind(getStringBinding("INFERRED"));
        assertedNegativeTestsLabel.textProperty().bind(getStringBinding("ASSERTED"));
        inferredNegativeTestsLabel.textProperty().bind(getStringBinding("INFERRED"));
        assertedPreviousDiseasesLabel.textProperty().bind(getStringBinding("ASSERTED"));
        inferredPreviousDiseasesLabel.textProperty().bind(getStringBinding("INFERRED"));
        assertedDiseasesLabel.textProperty().bind(getStringBinding("ASSERTED"));
        inferredDiseasesLabel.textProperty().bind(getStringBinding("INFERRED"));
        assertedTestsLabel.textProperty().bind(getStringBinding("ASSERTED"));
        inferredTestsLabel.textProperty().bind(getStringBinding("INFERRED"));
        assertedTreatmentsLabel.textProperty().bind(getStringBinding("ASSERTED"));
        inferredTreatmentsLabel.textProperty().bind(getStringBinding("INFERRED"));
        assertedCausesLabel.textProperty().bind(getStringBinding("ASSERTED"));
        inferredCausesLabel.textProperty().bind(getStringBinding("INFERRED"));
        personalDataTab.textProperty().bind(getStringBinding("PERSONAL_DATA"));
        symptomTab.textProperty().bind(getStringBinding("SYMPTOMS"));
        negativeTestTab.textProperty().bind(getStringBinding("NEGATIVE_TESTS"));
        previousDiseaseTab.textProperty().bind(getStringBinding("PREVIOUS_DISEASES"));
        diseaseTab.textProperty().bind(getStringBinding("DISEASES"));
        testTab.textProperty().bind(getStringBinding("TESTS"));
        treatmentTab.textProperty().bind(getStringBinding("TREATMENTS"));
        causeTab.textProperty().bind(getStringBinding("CAUSES"));
    }
}
