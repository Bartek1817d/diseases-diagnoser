package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.Main;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.OntologyWrapper;

import java.util.*;

public class EntitiesEditDialogController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @FXML
    private TreeView<Entity> entitiesTree;

    private Stage dialogStage;
    private boolean okClicked = false;
    private Main main;
    private Set<CheckBoxTreeItem<Entity>> entities = new HashSet<CheckBoxTreeItem<Entity>>();
    private Collection<Entity> results;
    private Map<String, Entity> allIndividuals;
    private OntologyWrapper ontology;

    public void setMainApp(Main main) {
        this.main = main;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setResultsContainer(Collection<Entity> results) {
        this.results = results;
    }

    public void setEntities(Entity rootEntity, Collection<Entity> currentIndividuals, OntologyWrapper ontology) {
        Collection<Entity> classes = ontology.getClasses().values();
        this.ontology = ontology;
        switch (rootEntity.getID()) {
            case "Symptom":
                allIndividuals = ontology.getSymptoms();
                break;
            case "Disease":
                allIndividuals = ontology.getDiseases();
                break;
            case "Testing":
                allIndividuals = ontology.getTests();
                break;
            case "Treatment":
                allIndividuals = ontology.getTreatments();
                break;
            case "Cause":
                allIndividuals = ontology.getCauses();
                break;
        }
        Map<Entity, TreeItem<Entity>> classMap = new HashMap<Entity, TreeItem<Entity>>();
        for (Entity classEntity : classes) {
            TreeItem<Entity> symptomItem = classMap.get(classEntity);
            if (symptomItem == null) {
                symptomItem = new CheckBoxTreeItem<Entity>(classEntity);
                symptomItem.setExpanded(false);
                classMap.put(classEntity, symptomItem);
            }
            if (classEntity.getClasses().isEmpty() && classEntity.getID().equals(rootEntity.getID()))
                entitiesTree.setRoot(symptomItem);
            else
                for (Entity parentClass : classEntity.getClasses()) {
                    TreeItem<Entity> symptomItemParent = classMap.get(parentClass);
                    if (symptomItemParent == null) {
                        symptomItemParent = new CheckBoxTreeItem<Entity>(parentClass);
                        symptomItemParent.setExpanded(false);
                        classMap.put(parentClass, symptomItemParent);
                    }
                    symptomItemParent.getChildren().add(symptomItem);
                }
        }
        entitiesTree.setShowRoot(false);
        entitiesTree.setCellFactory(CheckBoxTreeCell.forTreeView());

        for (Entity individual : allIndividuals.values()) {
            CheckBoxTreeItem<Entity> individualItem = new CheckBoxTreeItem<Entity>(individual);
            if (currentIndividuals.contains(individual))
                individualItem.setSelected(true);

            entities.add(individualItem);
            for (Entity classEntity : individual.getClasses())
                classMap.get(classEntity).getChildren().add(individualItem);
        }
    }

    @FXML
    private void initialize() {
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOK() {
        for (CheckBoxTreeItem<Entity> entity : entities) {
            if (entity.isSelected()) {
                results.add(entity.getValue());
            }
        }
        okClicked = true;
        dialogStage.close();
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @FXML
    private void handleNewEntity() {
        TreeItem<Entity> treeItem = entitiesTree.getSelectionModel().getSelectedItem();
        if (treeItem != null && !entities.contains(treeItem)) {
            Entity newEntity = new Entity();
            boolean okClicked = main.showEntityEditDialog(newEntity);
            if (okClicked) {
                newEntity.addClass(treeItem.getValue());
                CheckBoxTreeItem<Entity> newTreeItem = new CheckBoxTreeItem<Entity>(newEntity);
                entities.add(newTreeItem);
                treeItem.getChildren().add(newTreeItem);
                ontology.addEntity(newEntity);
                allIndividuals.put(newEntity.getID(), newEntity);
            }
        } else {
            // Nothing selected.
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Class Selected",
                    "Please select parent class in the table.");
        }
    }

    @FXML
    private void handleEditEntity() {
        TreeItem<Entity> treeItem = entitiesTree.getSelectionModel().getSelectedItem();
        if (treeItem != null && entities.contains(treeItem)) {
            Entity entity = treeItem.getValue();
            boolean okClicked = main.showEntityEditDialog(entity);
            if (okClicked) {
                entitiesTree.refresh();
                ontology.addEntity(entity);
            }
        } else {
            // Nothing selected.
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Item Selected",
                    "Please select item in the table.");
        }
    }

    @FXML
    private void handleDeleteEntity() {
        TreeItem<Entity> treeEntity = entitiesTree.getSelectionModel().getSelectedItem();
        if (treeEntity != null && this.entities.contains(treeEntity)) {
            Entity entity = treeEntity.getValue();
            treeEntity.getParent().getChildren().remove(treeEntity);
            this.entities.remove(treeEntity);
            ontology.deleteEntity(entity);
            allIndividuals.remove(entity.getID());
        } else {
            // Nothing selected.
            Dialogs.warningDialog(main.getPrimaryStage(), "No Selection", "No Entities Selected",
                    "Please select entities in the table.");
        }
    }
}
