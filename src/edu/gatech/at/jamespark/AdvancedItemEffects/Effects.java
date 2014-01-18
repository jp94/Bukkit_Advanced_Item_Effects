package edu.gatech.at.jamespark.AdvancedItemEffects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

    public ArrayList<Player> playerList = new ArrayList<>();

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

    public void addAllBoundItemParticleEffects(Player player,
            List<String> heldItemLore) {
        for (int x = 0; x < particleEffectsList.length; x++) {
            int loreLineMatch = listContainsIgnoreCase(heldItemLore,
                    particleEffectsList[x]);
            if (loreLineMatch != -1) {
                // TODO learn regex and fix this.
                String[] effectSplit = heldItemLore.get(loreLineMatch)
                        .replaceAll("\\s", "").split("¡×.");
                if (!(playerList.contains(player))) {
                    playerList.add(player);
                }
                player.setMetadata(effectSplit[1].toUpperCase(),
                        new FixedMetadataValue(plugin, true));
            }
        }
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

    /**
     * Adds all particle and potion effects given a specific item's lore.
     * 
     * @param player
     *            Player to add item effects.
     * @param itemLore
     *            Lore that contains effects list to add listed effects to a
     *            player.
     */
    public void addAllItemEffects(Player player, List<String> itemLore) {
        addAllBoundItemParticleEffects(player, itemLore);
        addAllBoundItemPotionEffects(player, itemLore);
    }

    /**
     * Adds armor and held item effects. DOES NOT REMOVE BUT ONLY ADDS EFFECTS.
     * CHECK FOR DUPLICATE EFFECTS BY REMOVING EFFECTS PRIOR TO THIS HELPER
     * METHOD.
     * 
     * @param player
     *            Player to add all active effects listed in items' lore.
     */
    public void addArmorAndHeldItemEffects(Player player) {
        // Checks for equipped items effects
        ItemStack[] armorList = player.getInventory().getArmorContents();
        for (int x = 0; x < armorList.length; x++) {
            if (armorList[x].hasItemMeta()
                    && armorList[x].getItemMeta().hasLore()) {
                List<String> equippedItemLore = armorList[x].getItemMeta()
                        .getLore();
                if (equippedItemLore.contains(ChatColor.GOLD + "Effects:")) {
                    addAllItemEffects(player, equippedItemLore);
                }
            }
        }

        // Checks for item in hand effects
        ItemStack heldItem = player.getItemInHand();
        if (heldItem.hasItemMeta() && heldItem.getItemMeta().hasLore()) {
            List<String> heldItemLore = heldItem.getItemMeta().getLore();
            if (heldItemLore.contains(ChatColor.GOLD + "Effects:")) {
                addAllItemEffects(player, heldItemLore);
            }
        }
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
        if (playerList.contains(player)) {
            playerList.remove(player);
        }
    }

    private void removeAllParticleEffects(Player player) {
        for (int x = 0; x < particleEffectsList.length; x++) {
            Effect effect = Effect.valueOf(particleEffectsList[x]);
            if (player.hasMetadata(effect.toString())) {
                removeBoundItemParticleEffect(player, effect);
            }
        }
    }

    /**
     * Removes player effects listed in heldItemLore.
     * 
     * @param player
     *            Player to remove effects
     * @param heldItemLore
     *            Lore of an item that contains effects list which will be
     *            removed from player
     */
    public void removeSingleItemEffects(Player player, List<String> heldItemLore) {

        for (int x = 0; x < particleEffectsList.length; x++) {
            int loreLineMatch = listContainsIgnoreCase(heldItemLore,
                    particleEffectsList[x]);
            if (loreLineMatch != -1) {
                Effect effect = Effect.valueOf(particleEffectsList[x]);
                if (player.hasMetadata(effect.toString())) {
                    removeBoundItemParticleEffect(player, effect);
                }
            }
        }

        for (int x = 0; x < potionEffectsList.length; x++) {
            int loreLineMatch = listContainsIgnoreCase(heldItemLore,
                    potionEffectsList[x]);
            if (loreLineMatch != -1) {
                PotionEffectType type = PotionEffectType
                        .getByName(potionEffectsList[x]);
                player.removeMetadata(type.toString(), plugin);
                player.removePotionEffect(type);
            }
        }
    }

    /**
     * Removes all player's bound effects caused by this plugin.
     * 
     * @param player
     *            Player to remove all active effects caused by this plugin.
     */
    public void removeAllBoundEffects(Player player) {
        removeAllParticleEffects(player);
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
