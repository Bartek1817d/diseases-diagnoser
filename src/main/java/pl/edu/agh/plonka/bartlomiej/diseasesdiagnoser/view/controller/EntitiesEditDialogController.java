package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.ViewManager;

import java.util.*;

public class EntitiesEditDialogController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @FXML
    private TreeView<Entity> entitiesTree;

    private Stage dialogStage;
    private boolean okClicked = false;
    private ViewManager viewManager;
    private PatientsService patientsService;
    private Set<CheckBoxTreeItem<Entity>> entities = new HashSet<>();
    private Collection<Entity> results;
    private Map<String, Entity> allIndividuals;

    public void init(ViewManager viewManager, Stage dialogStage, PatientsService patientsService) {
        this.viewManager = viewManager;
        this.dialogStage = dialogStage;
        this.patientsService = patientsService;
    }

    public void setResultsContainer(Collection<Entity> results) {
        this.results = results;
    }

    public void setEntities(Entity rootEntity, Collection<Entity> currentIndividuals) {
        Collection<Entity> classes = patientsService.getOntology().getClasses().values();
        switch (rootEntity.getID()) {
            case "Symptom":
                allIndividuals = patientsService.getOntology().getSymptoms();
                break;
            case "Disease":
                allIndividuals = patientsService.getOntology().getDiseases();
                break;
            case "Testing":
                allIndividuals = patientsService.getOntology().getTests();
                break;
            case "Treatment":
                allIndividuals = patientsService.getOntology().getTreatments();
                break;
            case "Cause":
                allIndividuals = patientsService.getOntology().getCauses();
                break;
        }
        Map<Entity, TreeItem<Entity>> classMap = new HashMap<>();
        for (Entity classEntity : classes) {
            TreeItem<Entity> symptomItem = classMap.get(classEntity);
            if (symptomItem == null) {
                symptomItem = new CheckBoxTreeItem<>(classEntity);
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
            boolean okClicked = viewManager.showEntityEditDialog(newEntity, patientsService);
            if (okClicked) {
                newEntity.addClass(treeItem.getValue());
                CheckBoxTreeItem<Entity> newTreeItem = new CheckBoxTreeItem<Entity>(newEntity);
                entities.add(newTreeItem);
                treeItem.getChildren().add(newTreeItem);
                patientsService.getOntology().addEntity(newEntity);
                allIndividuals.put(newEntity.getID(), newEntity);
            }
        } else {
            // Nothing selected.
            viewManager.warningDialog("No Selection", "No Class Selected",
                    "Please select parent class in the table.");
        }
    }

    @FXML
    private void handleEditEntity() {
        TreeItem<Entity> treeItem = entitiesTree.getSelectionModel().getSelectedItem();
        if (treeItem != null && entities.contains(treeItem)) {
            Entity entity = treeItem.getValue();
            boolean okClicked = viewManager.showEntityEditDialog(entity, patientsService);
            if (okClicked) {
                entitiesTree.refresh();
                patientsService.getOntology().addEntity(entity);
            }
        } else {
            // Nothing selected.
            viewManager.warningDialog("No Selection", "No Item Selected",
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
            patientsService.getOntology().deleteEntity(entity);
            allIndividuals.remove(entity.getID());
        } else {
            // Nothing selected.
            viewManager.warningDialog("No Selection", "No Entities Selected",
                    "Please select entities in the table.");
        }
    }
}
