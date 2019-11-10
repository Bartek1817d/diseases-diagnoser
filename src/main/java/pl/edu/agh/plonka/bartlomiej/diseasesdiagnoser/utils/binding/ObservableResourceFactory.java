package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Language;

import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.BUNDLE_PATH;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Language.ENGLISH;

public class ObservableResourceFactory {

    private static ObjectProperty<ResourceBundle> resources = new SimpleObjectProperty<>();
    private static Language language;

    static {
        resources.set(getBundle(BUNDLE_PATH, new Locale("en")));
        language = ENGLISH;
    }

    private static ObjectProperty<ResourceBundle> resourcesProperty() {
        return resources;
    }

    public static Language getLanguage() {
        return language;
    }

    public static void setLanguage(Language language) {
        ObservableResourceFactory.language = language;
        resources.set(getBundle(BUNDLE_PATH, new Locale(ObservableResourceFactory.language.getCode())));
    }

    public static String getTranslation(String key) {
        return resources.get().getString(key);
    }

    public static StringBinding getStringBinding(String key) {
        return new StringBinding() {
            {
                bind(resourcesProperty());
            }

            @Override
            public String computeValue() {
                return getTranslation(key);
            }
        };
    }
}
