package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.CreateRuleException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.PartialStarCreationException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.RuleAlreadyExistsException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.MachineLearning;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Language;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.SystemDefaults;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.ViewManager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.GENERATED_RULE_PREFIX;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Language.ENGLISH;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Language.POLISH;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.SystemDefaults.setDefaultDirectoryFile;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.SystemDefaults.setDefaultOntologyFile;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.ObservableResourceFactory.*;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 *
 * @author Bartłomiej Płonka
 */
public class RootLayoutController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @FXML
    private Menu fileMenu;
    @FXML
    private Menu viewMenu;
    @FXML
    private Menu rulesMenu;
    @FXML
    private Menu helpMenu;
    @FXML
    private MenuItem newMenu;
    @FXML
    private MenuItem openMenu;
    @FXML
    private MenuItem saveMenu;
    @FXML
    private MenuItem saveAsMenu;
    @FXML
    private MenuItem exitMenu;
    @FXML
    private MenuItem languageMenu;
    @FXML
    private MenuItem rulesEditMenu;
    @FXML
    private MenuItem rulesLearnMenu;
    @FXML
    private MenuItem aboutMenu;
    @FXML
    private RadioMenuItem englishMenu;
    @FXML
    private RadioMenuItem polishMenu;

    private PatientsService patientsService;
    private MachineLearning machineLearning;
    private String ontologyUrl;
    private ViewManager viewManager;

    /**
     * Is called by the main application to give a reference back to itself.
     */
    public void init(ViewManager viewManager, PatientsService patientsService, MachineLearning machineLearning, String ontologyUrl) {
        this.patientsService = patientsService;
        this.machineLearning = machineLearning;
        this.ontologyUrl = ontologyUrl;
        this.viewManager = viewManager;
    }

    @FXML
    private void initialize() {
        bindResourceBundle();
        initLanguageMenu();
    }

    private void initLanguageMenu() {
        ToggleGroup toggleGroup = new ToggleGroup();
        polishMenu.setToggleGroup(toggleGroup);
        englishMenu.setToggleGroup(toggleGroup);
        englishMenu.setSelected(true);
    }

    private void bindResourceBundle() {
        fileMenu.textProperty().bind(getStringBinding("FILE"));
        viewMenu.textProperty().bind(getStringBinding("VIEW"));
        rulesMenu.textProperty().bind(getStringBinding("RULES"));
        helpMenu.textProperty().bind(getStringBinding("HELP"));
        newMenu.textProperty().bind(getStringBinding("NEW"));
        openMenu.textProperty().bind(getStringBinding("OPEN"));
        saveMenu.textProperty().bind(getStringBinding("SAVE"));
        saveAsMenu.textProperty().bind(getStringBinding("SAVE_AS"));
        exitMenu.textProperty().bind(getStringBinding("EXIT"));
        languageMenu.textProperty().bind(getStringBinding("LANGUAGE"));
        rulesEditMenu.textProperty().bind(getStringBinding("RULES_EDIT"));
        rulesLearnMenu.textProperty().bind(getStringBinding("RULES_LEARN"));
        aboutMenu.textProperty().bind(getStringBinding("ABOUT"));
        englishMenu.textProperty().bind(getStringBinding("ENGLISH"));
        polishMenu.textProperty().bind(getStringBinding("POLISH"));
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
                viewManager.errorExceptionDialog(getTranslation("ERROR_CREATING_ONTOLOGY"), null,
                        getTranslation("ERROR_CREATING_ONTOLOGY_FROM_FILE") + ' ' + file.getName(), e);
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
                viewManager.errorExceptionDialog(getTranslation("ERROR_SAVING_ONTOLOGY"), null,
                        getTranslation("ERROR_SAVING_ONTOLOGY_TO_FILE") + ' ' + file.getName(), e);
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
                viewManager.errorExceptionDialog(getTranslation("ERROR_SAVING_ONTOLOGY"), null,
                        getTranslation("ERROR_SAVING_ONTOLOGY_TO_FILE") + ' ' + file.getName(), e);
            }
        }
    }

    @FXML
    private void handleEditRules() {
        LOG.info("Handle edit rules.");
        boolean okClicked = viewManager.showRulesEditDialog(patientsService);
    }

    @FXML
    private void handleRunMachineLearning() {
        LOG.info("Handle run machine learning algorithm");
        Collection<Patient> patients = patientsService.getPatients();
        try {
            Collection<Rule> newGeneratedRules = machineLearning.sequentialCovering(new HashSet<>(patients));
            Set<Rule> oldGeneratedRules = patientsService.getRules()
                    .stream()
                    .filter(this::isGeneratedRule)
                    .collect(toSet());
            patientsService.deleteRules(oldGeneratedRules);
            patientsService.addRules(newGeneratedRules);
        } catch (PartialStarCreationException e) {
            viewManager.errorExceptionDialog(getTranslation("ERROR_GENERATING_RULES"), e.getMessage(),
                    getTranslation("ERROR_CREATING_PARTIAL_STAR"), e);
        } catch (CreateRuleException | RuleAlreadyExistsException e) {
            viewManager.errorExceptionDialog(getTranslation("ERROR_GENERATING_RULES"), e.getMessage(),
                    getTranslation("ERROR_SAVING_RULES"), e);
        } catch (Throwable e) {
            viewManager.errorExceptionDialog(getTranslation("ERROR_GENERATING_RULES"), e.getMessage(), null, e);
        }
    }

    /**
     * Opens an about dialog.
     */
    @FXML
    private void handleAbout() {
        LOG.info("Open an about dialog.");
        viewManager.informationAlert("Diseases Dagnoser", getTranslation("ABOUT"), getTranslation("ABOUT_CONTEXT"));
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        LOG.info("Close application.");
        System.exit(0);
    }

    @FXML
    private void handleChangeLanguageToEnglish() {
        handleChangeLanguage(ENGLISH);
    }

    @FXML
    private void handleChangeLanguageToPolish() {
        handleChangeLanguage(POLISH);
    }

    private void handleChangeLanguage(Language language) {
        setLanguage(language);
        patientsService.changeLanguage();
    }

    private boolean isGeneratedRule(Rule rule) {
        return rule.getName().trim().startsWith(GENERATED_RULE_PREFIX);
    }

}