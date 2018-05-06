package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.Main;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.Dialogs;

import java.io.File;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 *
 * @author Bartłomiej Płonka
 */
public class RootLayoutController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    // Reference to the main application
    private Main main;

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Creates an empty diseases knowledge base.
     */
    @FXML
    private void handleNew() throws OWLOntologyCreationException {
        LOG.info("Create new ontology.");
        main.createNewOntology();
    }

    /**
     * Opens a FileChooser to let the user select an diseases knowledge base to load.
     */
    @FXML
    private void handleOpen() throws OWLOntologyCreationException {
        LOG.info("Handle opening ontology.");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OWL files (*.owl)", "*.owl");
        fileChooser.getExtensionFilters().add(extFilter);

        LOG.debug("Fetching default directory.");
        File defaultDirectory = main.getDefaultDirectoryFile();
        if (defaultDirectory != null) {
            LOG.debug("Found default directory: " + defaultDirectory.getPath());
            fileChooser.setInitialDirectory(defaultDirectory);
        }

        File file = fileChooser.showOpenDialog(main.getPrimaryStage());

        if (file != null) {
            try {
                main.loadOntologyFromFile(file);
            } catch (OWLOntologyCreationException e) {
                LOG.error("Failed to load ontology from " + file.getPath() + ". Creating empty ontology.");
                Dialogs.errorExceptionDialog(main.getPrimaryStage(), "Error creating ontology", null,
                        "Cannot create ontology from file: " + file.getName(), e);
                main.createNewOntology();
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
        File file = main.getDefaultOntologyFile();
        if (file != null) {
            try {
                main.saveOntologyToFile(file);
            } catch (OWLOntologyStorageException e) {
                LOG.error("Failed to save ontology to file " + file.getPath());
                Dialogs.errorExceptionDialog(main.getPrimaryStage(), "Error saving ontology", null,
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
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OWL files (*.owl)", "*.owl");
        fileChooser.getExtensionFilters().add(extFilter);

        LOG.debug("Fetching default directory.");
        File defaultDirectory = main.getDefaultDirectoryFile();
        if (defaultDirectory != null) {
            LOG.debug("Found default directory: " + defaultDirectory.getPath());
            fileChooser.setInitialDirectory(defaultDirectory);
        }

        // Show save file dialog
        File file = fileChooser.showSaveDialog(main.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".owl")) {
                file = new File(file.getPath() + ".owl");
            }
            try {
                main.saveOntologyToFile(file);
            } catch (OWLOntologyStorageException e) {
                Dialogs.errorExceptionDialog(main.getPrimaryStage(), "Error saving ontology", null,
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
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Diseases Dagnoser");
        alert.setHeaderText("About");
        alert.setContentText("Author: Bartłomiej Płonka");

        alert.showAndWait();
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