package edu.gatech.at.jamespark.AdvancedItemEffects.constructors;

import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fetches list of PotionEffectType fields from the current Bukkit API.
 */
public class PotionEffectsList {

    private static ArrayList<String> potionEffectsList;
    private static String[] array;
    private static String string;

    private PotionEffectsList() {
        throw new AssertionError();
    }

    public static ArrayList<String> getInstance() {
        if (potionEffectsList != null) return potionEffectsList;
        potionEffectsList = new ArrayList<>();
        Field[] effectFields = PotionEffectType.class.getFields();
        for (Field effectField : effectFields) {
            potionEffectsList.add(effectField.getName());
        }
        return potionEffectsList;
    }

    public static String[] getArray() {
        if (array == null) array = getInstance().toArray(new String[getInstance().size()]);
        return array;
    }

    public static String getString() {
        if (string == null) string = Arrays.toString(getArray());
        return string;
    }
}