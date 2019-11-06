package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;

public class ObservableResourceFactory {

    private static ObjectProperty<ResourceBundle> resources = new SimpleObjectProperty<>();
    static {
        resources.set(getBundle("bundles/MyBundle", new Locale("en")));
    }

    private static ObjectProperty<ResourceBundle> resourcesProperty() {
        return resources;
    }

    public static void setResources(ResourceBundle resources) {
        resourcesProperty().set(resources);
    }

    public static StringBinding getStringBinding(String key) {
        return new StringBinding() {
            {
                bind(resourcesProperty());
            }

            @Override
            public String computeValue() {
                return resources.get().getString(key);
            }
        };
    }
}
