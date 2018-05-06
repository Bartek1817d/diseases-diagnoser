package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service.PatientsService;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller.EntityEditDialogController;

import java.io.IOException;

public class ViewManager {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private Stage primaryStage;
    private BorderPane rootLayout;

    public ViewManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public boolean showEntityEditDialog(Entity entity, PatientsService patientsService) {
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
            controller.setMainApp(patientsService);
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
}
