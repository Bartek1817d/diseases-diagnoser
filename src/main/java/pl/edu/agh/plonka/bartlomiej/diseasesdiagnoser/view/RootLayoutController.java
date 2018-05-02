package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.Main;

import java.io.File;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 *
 * @author Bartłomiej Płonka
 */
public class RootLayoutController {

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
    private void handleNew() {
        main.getPatientData().clear();
        main.setDefaultOntologyFile(null);
    }

    /**
     * Opens a FileChooser to let the user select an diseases knowledge base to load.
     */
    @FXML
    private void handleOpen() throws OWLOntologyCreationException {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OWL files (*.owl)", "*.owl");
        fileChooser.getExtensionFilters().add(extFilter);

        File lastOntology = main.getDefaultOntologyFile();
        if (lastOntology != null && lastOntology.exists()) {
            fileChooser.setInitialDirectory(lastOntology.getParentFile());
        }

        // Show open file dialog
        File file = fileChooser.showOpenDialog(main.getPrimaryStage());

        if (file != null) {
            try {
                main.loadOntologyFromFile(file);
                main.setDefaultOntologyFile(file);
            } catch (OWLOntologyCreationException e) {
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
        File personFile = main.getDefaultOntologyFile();
        if (personFile != null) {
            main.saveOntologyToFile(personFile);
        } else {
            handleSaveAs();
        }
    }

    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OWL files (*.owl)", "*.owl");
        fileChooser.getExtensionFilters().add(extFilter);

        File lastOntology = main.getDefaultOntologyFile();
        if (lastOntology != null && lastOntology.exists()) {
            fileChooser.setInitialDirectory(lastOntology.getParentFile());
        }

        // Show save file dialog
        File file = fileChooser.showSaveDialog(main.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".owl")) {
                file = new File(file.getPath() + ".owl");
            }
            main.saveOntologyToFile(file);
        }
    }

    /**
     * Opens an about dialog.
     */
    @FXML
    private void handleAbout() {
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
        System.exit(0);
    }
}