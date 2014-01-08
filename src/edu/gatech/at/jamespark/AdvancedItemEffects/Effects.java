package edu.gatech.at.jamespark.AdvancedItemEffects;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Contains helper methods
 * 
 * @author James
 * 
 */
public class Effects {

    private Plugin plugin;

    public final String[] potionEffectsList = { "SPEED", "SLOW",
            "FAST_DIGGING", "SLOW_DIGGING", "INCREASE_DAMAGE", "HEAL", "HARM",
            "JUMP", "CONFUSION", "REGENERATION", "DAMAGE_RESISTANCE",
            "FIRE_RESISTANCE", "WATER_BREATHING", "INVISIBILITY", "BLINDNESS",
            "NIGHT_VISION", "HUNGER", "WEAKNESS", "POISON", "WITHER",
            "HEALTH_BOOST", "ABSORPTION", "SATURATION" };

    public final String[] particleEffectsList = { "ENDER_SIGNAL",
            "MOBSPAWNER_FLAMES", "POTION_BREAK", "SMOKE" };

    public Effects(Plugin plugin) {
        this.plugin = plugin;
    }

    private void addItemPotionEffect(Player player, PotionEffectType type,
            int level) {

        player.addPotionEffect(new PotionEffect(type, 2147483647, level - 1,
                false));
        player.setMetadata(type.toString(),
                new FixedMetadataValue(plugin, true));
    }

    public void addAllBoundItemPotionEffects(Player player,
            List<String> heldItemLore) {
        for (int x = 0; x < potionEffectsList.length; x++) {
            int loreLineMatch = listContainsIgnoreCase(heldItemLore,
                    potionEffectsList[x]);
            if (loreLineMatch != -1) {
                String[] effectSplit = heldItemLore.get(loreLineMatch)
                        .replaceAll("\\s", "").split("¡×.");
                if (effectSplit.length == 3) {
                    int multiplier = -1;
                    try {
                        multiplier = Integer.parseInt(effectSplit[2]);
                    } catch (NumberFormatException e) {
                    }
                    if (multiplier > 0 && multiplier <= 2147483647) {
                        addItemPotionEffect(player,
                                PotionEffectType.getByName(effectSplit[1]),
                                multiplier);
                    }
                }
            }
        }
    }

    public void addAllBoundItemParticleEffects(Player player,
            List<String> heldItemLore) {
        for (int x = 0; x < particleEffectsList.length; x++) {
            int loreLineMatch = listContainsIgnoreCase(heldItemLore,
                    particleEffectsList[x]);
            if (loreLineMatch != -1) {
                // TODO learn regex and fix this.
                String[] effectSplit = heldItemLore.get(loreLineMatch)
                        .replaceAll("\\s", "").split("¡×.");
                if (!player.hasMetadata("hasParticleEffectItem")) {
                    player.setMetadata("hasParticleEffectItem",
                            new FixedMetadataValue(plugin, true));
                }
                player.setMetadata(effectSplit[1].toUpperCase(),
                        new FixedMetadataValue(plugin, true));
            }
        }
    }

    public void addAllBoundEffects(Player player, List<String> heldItemLore) {
        addAllBoundItemParticleEffects(player, heldItemLore);
        addAllBoundItemPotionEffects(player, heldItemLore);
    }

    private void removeItemPotionEffect(Player player, PotionEffectType type) {
        player.removeMetadata(type.toString(), plugin);
        player.removePotionEffect(type);
    }

    /**
     * Removes all of the potion effects listed in player's metadata. NOTE: When
     * using this, good practice to check if player's item's lore.
     * 
     * 
     * @param player
     *            Player to remove all potion effects bound in metadata
     */
    public void removeAllBoundItemPotionEffects(Player player) {
        for (int x = 0; x < potionEffectsList.length; x++) {
            PotionEffectType potionEffectType = PotionEffectType
                    .getByName(potionEffectsList[x]);
            if (player.hasMetadata(potionEffectType.toString())) {
                removeItemPotionEffect(player, potionEffectType);
            }
        }
    }

    private void removeBoundItemParticleEffect(Player player, Effect type) {
        player.removeMetadata(type.toString(), plugin);
        if (player.hasMetadata("hasParticleEffectItem")) {
            player.removeMetadata("hasParticleEffectItem", plugin);
        }
    }

    private void removeAllBoundItemParticleEffects(Player player) {
        for (int x = 0; x < particleEffectsList.length; x++) {
            Effect effect = Effect.valueOf(particleEffectsList[x]);
            if (player.hasMetadata(effect.toString())) {
                removeBoundItemParticleEffect(player, effect);
            }
        }
    }

    public void removeAllBoundEffects(Player player) {
        removeAllBoundItemParticleEffects(player);
        removeAllBoundItemPotionEffects(player);
    }

    public int listContainsIgnoreCase(List<String> list, String str) {
        Iterator<String> iter = list.iterator();
        int line = 0;
        while (iter.hasNext()) {
            if (iter.next().toLowerCase().contains(str.toLowerCase())) {
                return line;
            }
            line++;
        }
        return -1;
    }

    public boolean stringArrayContainsIgnoreCase(String[] arr, String str) {
        for (int x = 0; x < arr.length; x++) {
            if (arr[x].equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }
}
