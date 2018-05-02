package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Dialogs {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public static void errorDialog(Stage ownerStage, String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(ownerStage);
        if (alert != null)
            alert.setTitle(title);
        if (header != null)
            alert.setHeaderText(header);
        if (content != null)
            alert.setContentText(content);
        alert.showAndWait();
    }

    public static void warningDialog(Stage ownerStage, String title, String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.initOwner(ownerStage);
        if (alert != null)
            alert.setTitle(title);
        if (header != null)
            alert.setHeaderText(header);
        if (content != null)
            alert.setContentText(content);
        alert.showAndWait();
    }

    public static void errorExceptionDialog(Stage ownerStage, String title, String header, String content,
                                            Exception exception) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(ownerStage);
        if (alert != null)
            alert.setTitle(title);
        if (header != null)
            alert.setHeaderText(header);
        if (content != null)
            alert.setContentText(content);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
}
