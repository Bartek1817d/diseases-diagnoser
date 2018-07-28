package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view;

import javafx.stage.Stage;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Response;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller.ResponseController;

public class ResponseStage<T> extends Stage {

    ResponseController<T> controller;

    public ResponseStage(ResponseController<T> controller) {
        this.controller = controller;
    }

    public Response<T> showAndWaitForResponse() {
        showAndWait();
        return controller.getResponse();
    }
}
