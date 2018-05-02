package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.OntologyWrapper;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.prefs.Preferences;

public class Main extends Application {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private Stage primaryStage;
    private BorderPane rootLayout;
    private static final String BASE_URL = "http://www.agh.edu.pl/plonka/bartlomiej/ontologies/human_diseases.owl";
    private OntologyWrapper ontology;

    /**
     * The data as an observable set of Patients.
     */
    private ObservableList<Patient> patientData = FXCollections.observableArrayList();

    /**
     * Constructor
     *
     * @throws OWLOntologyCreationException
     */
    public Main() {
    }

    /**
     * Returns the data as an observable list of Patients.
     *
     * @return
     */
    public ObservableList<Patient> getPatientData() {
        return patientData;
    }

    @Override
    public void start(Stage primaryStage) throws IOException, OWLOntologyCreationException {
        LOG.info("Starting application");
        this.primaryStage = primaryStage;

        initRootLayout();
        initOntology();
        showPatientOverview();
    }

    /**
     * Initializes the root layout.
     */
    private void initRootLayout() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL resource = getClass().getClassLoader().getResource("fxml/RootLayout.fxml");
        loader.setLocation(resource);
        LOG.debug("Loading root layout from FXML file");
        rootLayout = loader.load();

        // Show the scene containing the root layout.
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);

        // Give the controller access to the main app.
        RootLayoutController controller = loader.getController();
        controller.setMain(this);

        primaryStage.show();
    }

    private void initOntology() throws OWLOntologyCreationException {
        LOG.info("Ontology initialization.");
        File ontologyFile = getDefaultOntologyFile();
        if (ontologyFile == null || !ontologyFile.exists()) {
            LOG.debug("Default ontology not found. Initialize empty ontology.");
            createNewOntology();
        } else {
            try {
                LOG.debug("Found default ontology: " + ontologyFile + ". Loading from resource.");
                loadOntologyFromFile(ontologyFile);
                setDefaultOntologyFile(ontologyFile);
            } catch (OWLOntologyCreationException e) {
                LOG.error("Failed to load ontology " + ontologyFile + ". Creating empty ontology.");
                removeDefaultOntologyFile();
                createNewOntology();
            }
        }
    }

    /**
     * Shows the patient overview inside the root layout.
     */
    public void showPatientOverview() {
        try {
            // Load patient overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/PatientOverview.fxml"));
            AnchorPane patientOverview = loader.load();

            // Set patient overview into the center of root layout.
            rootLayout.setCenter(patientOverview);

            // Give the controller access to the main app.
            PatientOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog to edit details for the specified patient. If the user
     * clicks OK, the changes are saved into the provided patient object and
     * true is returned.
     *
     * @param patient the patient object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showPatientEditDialog(Patient patient) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/PatientEditDialog.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Patient");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the patient into the controller.
            PatientEditDialogController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);
            controller.setPatient(patient);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showEntitiesEditDialog(Entity rootEntity, Collection<Entity> currentEntities, Collection<Entity> results) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/EntitiesEditDialog.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Patient");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EntitiesEditDialogController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);
            controller.setResultsContainer(results);
            controller.setEntities(rootEntity, currentEntities, ontology);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showEntityEditDialog(Entity entity) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/EntityEditDialog.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Entity");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EntityEditDialogController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);
            controller.setEntity(entity);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
     */
    public File getDefaultOntologyFile() {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String filePath = prefs.get("ontologyFile", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public void setDefaultOntologyFile(File file) {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        if (file != null) {
            prefs.put("ontologyFile", file.getPath());
        }
    }

    public void removeDefaultOntologyFile() {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        prefs.remove("ontologyFile");
    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void loadOntologyFromFile(File file) throws OWLOntologyCreationException {
        ontology = new OntologyWrapper(file);
        patientData.clear();
        patientData.addAll(ontology.getPatients());
        primaryStage.setTitle("Diseases Diagnoser - " + file.getName());
    }

    public void saveOntologyToFile(File file) {
        try {
            ontology.saveOntologyToFile(file);
            setDefaultOntologyFile(file);
        } catch (OWLOntologyStorageException exception) {
            Dialogs.errorExceptionDialog(primaryStage, "Error saving ontology", null,
                    "Cannot save ontology to file: " + file.getName(), exception);
        }
    }

    public void createNewOntology() throws OWLOntologyCreationException {
        ontology = new OntologyWrapper(BASE_URL);
        patientData.clear();
        primaryStage.setTitle("Diseases Diagnoser");
    }

    public OntologyWrapper getOntology() {
        return ontology;
    }

    public static void main(String[] args) {
        launch(args);
    }
}