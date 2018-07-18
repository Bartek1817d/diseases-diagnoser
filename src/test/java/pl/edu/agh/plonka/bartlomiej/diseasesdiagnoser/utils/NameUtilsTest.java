package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils;

import org.junit.Assert;
import org.junit.Test;

public class NameUtilsTest {

    private static final String KEY_1 = "key1";
    private static final String KEY_2 = "key2";

    @Test
    public void generateName() {
        String name = NameUtils.generateName(KEY_1, KEY_2);

        Assert.assertEquals(KEY_1 + KEY_2, name);
    }

    @Test
    public void generateName1() {
    }

    @Test
    public void parseName() {
    }
}