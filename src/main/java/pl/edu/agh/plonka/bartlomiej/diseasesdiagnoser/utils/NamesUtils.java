package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class NamesUtils {
    public static String generateID(String... keywords) {
        StringBuilder str = new StringBuilder();
        for (String keyword : keywords) {
            str.append(StringUtils.stripAccents(StringUtils.deleteWhitespace(keyword)));
        }
        return str.toString();
    }

    public static String generate(Collection<String> keywords) {
        StringBuilder str = new StringBuilder();
        for (String keyword : keywords) {
            str.append(StringUtils.stripAccents(StringUtils.deleteWhitespace(keyword)));
        }
        return str.toString();
    }

    public static String parseName(String fullName) {
        return fullName.substring(fullName.lastIndexOf('#') + 1);
    }

    public static void main(String args[]) {
        System.out.println(parseName("dggfgsdfs#"));
    }
}
