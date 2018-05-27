package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser;

import javafx.application.Application;
import javafx.stage.Stage;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.ViewManager;

import java.io.File;
import java.io.IOException;

import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.SystemDefaults.*;

public class Main extends Application {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final String BASE_URL = "http://www.agh.edu.pl/plonka/bartlomiej/ontologies/human_diseases.owl";

    private ViewManager viewManager;
    private PatientsService patientsService;

    public Main() {
    }

    @Override
    public void start(Stage primaryStage) throws IOException, OWLOntologyCreationException {
        LOG.info("Starting application");
        viewManager = new ViewManager(primaryStage);
        initOntology();
        viewManager.initRootLayout(patientsService, BASE_URL);
        viewManager.showPatientOverview(patientsService);
    }

    private void initOntology() throws OWLOntologyCreationException {
        LOG.info("Ontology initialization.");
        File ontologyFile = getDefaultOntologyFile();
        if (ontologyFile == null || !ontologyFile.exists()) {
            LOG.debug("Default ontology not found. Initialize empty ontology.");
            removeDefaultOntologyFile();
            createNewOntology();
        } else {
            try {
                LOG.debug("Found default ontology: " + ontologyFile + ". Loading from resource.");
                loadOntologyFromFile(ontologyFile);
            } catch (OWLOntologyCreationException e) {
                LOG.warn("Failed to load ontology " + ontologyFile + ". Creating empty ontology.");
                removeDefaultOntologyFile();
                createNewOntology();
            }
        }
    }


    private void loadOntologyFromFile(File file) throws OWLOntologyCreationException {
        patientsService = new PatientsService(file);
        setDefaultOntologyFile(file);
        setDefaultDirectoryFile(file.getParentFile());
        viewManager.setTitle("Diseases Diagnoser - " + file.getName());
    }

    private void createNewOntology() throws OWLOntologyCreationException {
        patientsService = new PatientsService(BASE_URL);
        viewManager.setTitle("Diseases Diagnoser");
    }

    public static void main(String[] args) {
        launch(args);
    }
}