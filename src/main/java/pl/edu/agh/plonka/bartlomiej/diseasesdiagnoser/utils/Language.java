package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils;

public enum Language {
    ENGLISH("en"),
    POLISH("pl");

    private String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
