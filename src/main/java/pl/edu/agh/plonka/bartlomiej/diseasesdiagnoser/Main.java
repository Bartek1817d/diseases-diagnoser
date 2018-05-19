package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.ViewManager;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller.PatientOverviewController;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller.RootLayoutController;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.SystemDefaults.*;

public class Main extends Application {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private Stage primaryStage;
    private BorderPane rootLayout;
    private static final String BASE_URL = "http://www.agh.edu.pl/plonka/bartlomiej/ontologies/human_diseases.owl";

    /**
     * The data as an observable set of Patients.
     */
    private ViewManager viewManager;
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

        viewManager = new ViewManager(primaryStage);
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
        controller.setMainApp(viewManager, patientsService);
    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void loadOntologyFromFile(File file) throws OWLOntologyCreationException {
        patientsService = new PatientsService(file);
        setDefaultOntologyFile(file);
        setDefaultDirectoryFile(file.getParentFile());
        primaryStage.setTitle("Diseases Diagnoser - " + file.getName());
    }

    private void createNewOntology() throws OWLOntologyCreationException {
        patientsService = new PatientsService(BASE_URL);
        primaryStage.setTitle("Diseases Diagnoser");
    }

    public static void main(String[] args) {
        launch(args);
    }
}