package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.Main;

import java.io.File;
import java.util.prefs.Preferences;

public class SystemDefaults {

    private static final Preferences preferences = Preferences.userNodeForPackage(Main.class);
    private static final String ONTOLOGY_FILE_KEY = "ontologyFile";
    private static final String DIRECTORY_KEY = "defaultDirectory";

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
     */
    public static File getDefaultOntologyFile() {
        String ontologyPath = preferences.get(ONTOLOGY_FILE_KEY, null);
        if (ontologyPath != null) {
            File ontologyFile = new File(ontologyPath);
            return ontologyFile.exists() && ontologyFile.isFile() ? ontologyFile : null;
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public static void setDefaultOntologyFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            preferences.put("ontologyFile", file.getPath());
        }
    }

    public static void removeDefaultOntologyFile() {
        preferences.remove(ONTOLOGY_FILE_KEY);
    }

    public static File getDefaultDirectoryFile() {
        String directoryPath = preferences.get(DIRECTORY_KEY, null);
        if (directoryPath != null) {
            File directoryFile = new File(directoryPath);
            return directoryFile.exists() && directoryFile.isDirectory() ? directoryFile : null;
        } else {
            return null;
        }
    }

    public static void setDefaultDirectoryFile(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            preferences.put(DIRECTORY_KEY, file.getPath());
        }
    }

}
