package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.BUNDLE_PATH;

public class ObservableResourceFactory {

    private static ObjectProperty<ResourceBundle> resources = new SimpleObjectProperty<>();
    private static String language;

    static {
        resources.set(getBundle(BUNDLE_PATH, new Locale("en")));
        language = "en";
    }

    private static ObjectProperty<ResourceBundle> resourcesProperty() {
        return resources;
    }

    public static String getLanguage() {
        return language;
    }

    public static void setLanguage(String language) {
        ObservableResourceFactory.language = language;
        resources.set(getBundle(BUNDLE_PATH, new Locale(ObservableResourceFactory.language)));
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
