package edu.gatech.at.jamespark.AdvancedItemEffects.constructors;

import org.bukkit.Effect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fetches list of Effect fields from the current Bukkit API.
 */
public class EffectEffectsList {

    private static ArrayList<String> effectEffectsList;
    private static String[] array;
    private static String string;
    private static String[] incompatibleEffects = {"ITEM_BREAK", "TILE_BREAK", "TILE_DUST"};

    private EffectEffectsList() {
        throw new AssertionError();
    }

    public static ArrayList<String> getInstance() {
        if (effectEffectsList != null) return effectEffectsList;
        effectEffectsList = new ArrayList<>();
        Field[] effectFields = Effect.class.getFields();
        for (Field effectField : effectFields) {
            effectEffectsList.add(effectField.getName());
        }
        for (String incompatibleEffect : incompatibleEffects) {
            effectEffectsList.remove(incompatibleEffect);
        }
        return effectEffectsList;
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
