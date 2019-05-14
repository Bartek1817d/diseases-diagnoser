package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import static javafx.scene.input.KeyCode.DELETE;

public class ControllerUtils {

    public static EventHandler<? super KeyEvent> createDeleteEventHandler(Runnable action) {
        return createEventHandler(DELETE, action);
    }

    public static EventHandler<? super KeyEvent> createEventHandler(KeyCode keyCode, Runnable action) {
        return keyEvent -> {
            if (keyEvent.getCode() == keyCode) {
                action.run();
            }
        };
    }

}
