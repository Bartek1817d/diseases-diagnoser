package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import javafx.fxml.FXML;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.SystemDefaults;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.ViewManager;

import java.io.File;

import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.SystemDefaults.setDefaultDirectoryFile;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.SystemDefaults.setDefaultOntologyFile;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 *
 * @author Bartłomiej Płonka
 */
public class RootLayoutController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private PatientsService patientsService;
    private String ontologyUrl;
    private ViewManager viewManager;

    /**
     * Is called by the main application to give a reference back to itself.
     */
    public void init(ViewManager viewManager, PatientsService patientsService, String ontologyUrl) {
        this.patientsService = patientsService;
        this.ontologyUrl = ontologyUrl;
        this.viewManager = viewManager;
    }

    /**
     * Creates an empty diseases knowledge base.
     */
    @FXML
    private void handleNew() throws OWLOntologyCreationException {
        LOG.info("Create new ontology.");
        patientsService.createKnowledgeBase(ontologyUrl);
    }

    /**
     * Opens a FileChooser to let the user select an diseases knowledge base to load.
     */
    @FXML
    private void handleOpen() throws OWLOntologyCreationException {
        LOG.info("Handle opening ontology.");
        File file = viewManager.showOpenDialog("OWL files (*.owl)", "*.owl");
        if (file != null) {
            try {
                patientsService.createKnowledgeBase(file);
                setDefaultOntologyFile(file);
                setDefaultDirectoryFile(file.getParentFile());
            } catch (OWLOntologyCreationException e) {
                LOG.error("Failed to load ontology from " + file.getPath() + ". Creating empty ontology.");
                viewManager.errorExceptionDialog("Error creating ontology", null,
                        "Cannot create ontology from file: " + file.getName(), e);
                patientsService.createKnowledgeBase(ontologyUrl);
            }
        }
    }

    /**
     * Saves the file to the person file that is currently open. If there is no
     * open file, the "save as" dialog is shown.
     */
    @FXML
    private void handleSave() {
        LOG.info("Handle saving ontology.");
        File file = SystemDefaults.getDefaultOntologyFile();
        if (file != null) {
            try {
                patientsService.saveKnowledgeBase(file);
            } catch (OWLOntologyStorageException e) {
                LOG.error("Failed to save ontology to file " + file.getPath());
                viewManager.errorExceptionDialog("Error saving ontology", null,
                        "Cannot save ontology to file: " + file.getName(), e);
            }
        } else {
            handleSaveAs();
        }
    }

    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
    @FXML
    private void handleSaveAs() {
        LOG.info("Handle saving as ontology.");
        File file = viewManager.showSaveDialog("OWL files (*.owl)", "*.owl");
        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".owl")) {
                file = new File(file.getPath() + ".owl");
            }
            try {
                patientsService.saveKnowledgeBase(file);
                setDefaultOntologyFile(file);
                setDefaultDirectoryFile(file.getParentFile());
                viewManager.setTitle("Diseases Diagnoser - " + file.getName());
            } catch (OWLOntologyStorageException e) {
                viewManager.errorExceptionDialog("Error saving ontology", null,
                        "Cannot save ontology to file: " + file.getName(), e);
            }
        }
    }

    /**
     * Opens an about dialog.
     */
    @FXML
    private void handleAbout() {
        LOG.info("Open an about dialog.");
        viewManager.informationAlert("Diseases Dagnoser", "About", "Author: Bartłomiej Płonka");
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        LOG.info("Close application.");
        System.exit(0);
    }
}