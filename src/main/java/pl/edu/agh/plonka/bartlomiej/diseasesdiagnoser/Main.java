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
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.prefs.Preferences;

public class Main extends Application {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private Preferences preferences = Preferences.userNodeForPackage(Main.class);
    private Stage primaryStage;
    private BorderPane rootLayout;
    private static final String BASE_URL = "http://www.agh.edu.pl/plonka/bartlomiej/ontologies/human_diseases.owl";

    /**
     * The data as an observable set of Patients.
     */
    private PatientsService patientsService;

    /**
     * Constructor
     *
     * @throws OWLOntologyCreationException
     */
    public Main() {
    }

    @Override
    public void start(Stage primaryStage) throws IOException, OWLOntologyCreationException {
        LOG.info("Starting application");

        this.primaryStage = primaryStage;

        createOntology();
        initRootLayout();
        showPatientOverview();
    }

    /**
     * Initializes the root layout.
     */
    private void initRootLayout() throws IOException {
        LOG.info("Loading root layout from FXML file");
        FXMLLoader loader = new FXMLLoader();
        URL resource = getClass().getClassLoader().getResource("fxml/RootLayout.fxml");
        loader.setLocation(resource);
        rootLayout = loader.load();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);

        RootLayoutController controller = loader.getController();
        controller.setMain(primaryStage, patientsService, BASE_URL);

        primaryStage.show();
    }

    private void createOntology() throws OWLOntologyCreationException {
        LOG.info("Ontology initialization.");
        File ontologyFile = getDefaultOntologyFile();
        if (ontologyFile == null || !ontologyFile.exists()) {
            LOG.debug("Default ontology not found. Initialize empty ontology.");
            createNewOntology();
        } else {
            try {
                LOG.debug("Found default ontology: " + ontologyFile + ". Loading from resource.");
                setDefaultOntologyFile(ontologyFile);
                loadOntologyFromFile(ontologyFile);
            } catch (OWLOntologyCreationException e) {
                LOG.warn("Failed to load ontology " + ontologyFile + ". Creating empty ontology.");
                removeDefaultOntologyFile();
                createNewOntology();
            }
        }
    }

    /**
     * Shows the patient overview inside the root layout.
     */
    public void showPatientOverview() throws IOException {
        LOG.info("Loading patient overview layout from FXML file");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("fxml/PatientOverview.fxml"));
        AnchorPane patientOverview = loader.load();

        rootLayout.setCenter(patientOverview);

        PatientOverviewController controller = loader.getController();
        controller.setMainApp(this, patientsService);
    }

    /**
     * Opens a dialog to edit details for the specified patient. If the user
     * clicks OK, the changes are saved into the provided patient object and
     * true is returned.
     *
     * @param patient the patient object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showPatientEditDialog(Patient patient) throws IOException {
        LOG.info("Loading patient edit dialog layout from FXML file");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("fxml/PatientEditDialog.fxml"));
        AnchorPane page = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Patient");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Set the patient into the controller.
        PatientEditDialogController controller = loader.getController();
        controller.setMainApp(this, patientsService);
        controller.setDialogStage(dialogStage);
        controller.setPatient(patient);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();

        return controller.isOkClicked();
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
            controller.setEntities(rootEntity, currentEntities, patientsService.getOntology());

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
            controller.setMainApp(this, patientsService);
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
        String ontologyPath = preferences.get("ontologyFile", null);
        if (ontologyPath != null) {
            File ontologyFile = new File(ontologyPath);
            return ontologyFile.exists() && ontologyFile.isFile() ? ontologyFile : null;
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
        if (file != null && file.exists() && file.isFile()) {
            preferences.put("ontologyFile", file.getPath());
        }
    }

    public void removeDefaultOntologyFile() {
        preferences.remove("ontologyFile");
    }

    public File getDefaultDirectoryFile() {
        String directoryPath = preferences.get("defaultDirectory", null);
        if (directoryPath != null) {
            File directoryFile = new File(directoryPath);
            return directoryFile.exists() && directoryFile.isDirectory() ? directoryFile : null;
        } else {
            return null;
        }
    }

    public void setDefaultDirectoryFile(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            preferences.put("defaultDirectory", file.getPath());
        }
    }

    public void removeDefaultDirectoryFile() {
        preferences.remove("defaultDirectory");
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
        patientsService = new PatientsService(file);
        setDefaultOntologyFile(file);
        setDefaultDirectoryFile(file.getParentFile());
        primaryStage.setTitle("Diseases Diagnoser - " + file.getName());
    }

    public void saveOntologyToFile(File file) throws OWLOntologyStorageException {
        patientsService.saveKnowledgeBase(file);
        setDefaultOntologyFile(file);
        setDefaultDirectoryFile(file.getParentFile());
    }

    public void createNewOntology() throws OWLOntologyCreationException {
        patientsService = new PatientsService(BASE_URL);
        primaryStage.setTitle("Diseases Diagnoser");
    }

    public static void main(String[] args) {
        launch(args);
    }
}