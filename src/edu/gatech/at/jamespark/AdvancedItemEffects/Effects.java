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

// TODO Better organization & javadoc
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

    // ********************************************************************************************//
    // Effect add methods
    // ********************************************************************************************//

    /**
     * Adds all item effects. Uses config file.
     * 
     * @param player
     *            Player to add all active effects listed in items' lore.
     * 
     */
    public void addItemEffects(Player player, boolean addArmor,
            boolean addInventory, boolean addHeld, List<String> itemLore) {
        // Checks for equipped items effects
        if (addArmor && plugin.getConfig().getBoolean("addEffects.equip")) {
            ItemStack[] armorList = player.getInventory().getArmorContents();
            for (int x = 0; x < armorList.length; x++) {
                if (armorList[x] != null && armorList[x].hasItemMeta()
                        && armorList[x].getItemMeta().hasLore()) {
                    List<String> equippedItemLore = armorList[x].getItemMeta()
                            .getLore();
                    if (equippedItemLore.contains(ChatColor.GOLD + "Effects:")) {
                        addAllItemEffects(player, equippedItemLore);
                    }
                }
            }
        }
        // Checks for items effects in inventory
        if (addInventory
                && plugin.getConfig().getBoolean("addEffects.inventory")) {

            // NOTE: This check is explicitly for PlayerItemHeldEvent
            if (itemLore != null
                    && itemLore.contains(ChatColor.GOLD + "Effects:")) {
                addAllItemEffects(player, itemLore);

            } else {
                ItemStack[] invList = player.getInventory().getContents();
                for (int x = 0; x < invList.length; x++) {
                    if (invList[x] != null && invList[x].hasItemMeta()
                            && invList[x].getItemMeta().hasLore()) {
                        List<String> inventoryItemLore = invList[x]
                                .getItemMeta().getLore();
                        if (inventoryItemLore.contains(ChatColor.GOLD
                                + "Effects:")) {
                            addAllItemEffects(player, inventoryItemLore);
                        }
                    }
                }
            }
            // Checks for item in hand effects
        } else if (addHeld && plugin.getConfig().getBoolean("addEffects.held")) {

            // Some events' getItemStack() do not match getItem
            // Checking amount of 0 was the closest workaround I was able to
            // come up with
            if (itemLore != null
                    && itemLore.contains(ChatColor.GOLD + "Effects:")) {
                addAllItemEffects(player, itemLore);

            } else {
                ItemStack heldItem = player.getItemInHand();
                if (heldItem.hasItemMeta() && heldItem.getItemMeta().hasLore()) {
                    List<String> heldItemLore = heldItem.getItemMeta()
                            .getLore();
                    if (heldItemLore.contains(ChatColor.GOLD + "Effects:")) {
                        addAllItemEffects(player, heldItemLore);
                    }
                }
            }
        }
    }

    public void addItemEffects(Player player, boolean addArmor,
            boolean addInventory, boolean addHeld) {
        addItemEffects(player, addArmor, addInventory, addHeld, null);
    }

    public void addAllBoundItemParticleEffects(Player player,
            List<String> heldItemLore) {
        for (int x = 0; x < particleEffectsList.length; x++) {
            int loreLineMatch = listContainsIgnoreCase(heldItemLore,
                    particleEffectsList[x]);
            if (loreLineMatch != -1) {
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

    // ********************************************************************************************//
    // Effect remove methods
    // ********************************************************************************************//

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

    // ********************************************************************************************//
    // Private helper methods
    // ********************************************************************************************//

    /**
     * Adds all particle and potion effects given a specific item's lore.
     * 
     * @param player
     *            Player to add item effects.
     * @param itemLore
     *            Lore that contains effects list to add listed effects to a
     *            player.
     */
    private void addItemPotionEffect(Player player, PotionEffectType type,
            int level) {
        player.addPotionEffect(new PotionEffect(type, 2147483647, level - 1,
                false));
        player.setMetadata(type.toString(),
                new FixedMetadataValue(plugin, true));
    }

    private void addAllItemEffects(Player player, List<String> itemLore) {
        addAllBoundItemParticleEffects(player, itemLore);
        addAllBoundItemPotionEffects(player, itemLore);
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
    private void removeAllBoundItemPotionEffects(Player player) {
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

    // ********************************************************************************************//
    // Remaining methods
    // ********************************************************************************************//

    public int listContainsIgnoreCase(List<String> list, String str) {
        Iterator<String> iter = list.iterator();

        int line = 0;
        while (iter.hasNext()) {
            if (iter.next().toUpperCase().contains(str.toUpperCase())) {
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
