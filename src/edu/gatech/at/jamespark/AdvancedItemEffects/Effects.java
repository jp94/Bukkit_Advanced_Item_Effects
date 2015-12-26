package edu.gatech.at.jamespark.AdvancedItemEffects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// TODO Better organization & javadoc

/**
 * Contains helper methods
 *
 * @author James
 */
public class Effects {

    public final String[] potionEffectsList = {"SPEED", "SLOW",
            "FAST_DIGGING", "SLOW_DIGGING", "INCREASE_DAMAGE", "HEAL", "HARM",
            "JUMP", "CONFUSION", "REGENERATION", "DAMAGE_RESISTANCE",
            "FIRE_RESISTANCE", "WATER_BREATHING", "INVISIBILITY", "BLINDNESS",
            "NIGHT_VISION", "HUNGER", "WEAKNESS", "POISON", "WITHER",
            "HEALTH_BOOST", "ABSORPTION", "SATURATION"};
    public final String[] particleEffectsList = {"ENDER_SIGNAL",
            "MOBSPAWNER_FLAMES", "POTION_BREAK", "SMOKE"};

    public final String POTION_KEY = "AIE_CURRENT_POTION_EFFECTS";
    public final String PARTICLE_KEY = "AIE_CURRENT_PARTICLE_EFFECTS";

    public ArrayList<Player> playerList = new ArrayList<>();
    private Plugin plugin;

    public Effects(Plugin plugin) {
        this.plugin = plugin;
    }

    // ********************************************************************************************//
    // Effect add methods
    // ********************************************************************************************//

    /**
     * Adds all item effects. Uses config file.
     *
     * @param player Player to add all active effects listed in items' lore.
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
                        .replaceAll("\\s", "").split("��.");
                if (!(playerList.contains(player))) {
                    playerList.add(player);
                }
                addPlayerEffectMetadata(player, PARTICLE_KEY, effectSplit[1].toUpperCase());
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
                        .replaceAll("\\s", "").split("��.");
                if (effectSplit.length == 3) {
                    int multiplier = -1;
                    try {
                        multiplier = Integer.parseInt(effectSplit[2]);
                    } catch (NumberFormatException e) {
                    }
                    if (multiplier > 0) {
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
     * @param player Player to remove all active effects caused by this plugin.
     */
    public void removeAllBoundEffects(Player player) {
        if (null == player) return;

        if (player.hasMetadata(POTION_KEY)) {
            // Get Player's active potion effects from this plugin
            ArrayList playerPotionEffects = (ArrayList) player.getMetadata(POTION_KEY).get(0).value();

            // @TODO Check if all player's effects get removed
            // Clear Player's active potion effects
            // Note: We are avoiding other active potion effects not from this plugin
            for (Object playerPotionEffect : playerPotionEffects) {
                PotionEffectType potionEffectType = PotionEffectType
                        .getByName(playerPotionEffect.toString());  // @TODO check this later
                if (potionEffectType != null) {
                    player.removePotionEffect(potionEffectType);
                }
            }

            player.removeMetadata(POTION_KEY, plugin);
        }
        if (player.hasMetadata(PARTICLE_KEY)) {
            player.removeMetadata(PARTICLE_KEY, plugin);
        }

        // Remove Player from particle effect track list.
        playerList.remove(player);
    }

    // ********************************************************************************************//
    // Private helper methods
    // ********************************************************************************************//

    private void addItemPotionEffect(Player player, PotionEffectType type,
                                     int level) {
        player.addPotionEffect(new PotionEffect(type, 2147483647, level - 1,
                false));
        addPlayerEffectMetadata(player, POTION_KEY, type.toString());
    }

    private void addAllItemEffects(Player player, List<String> itemLore) {
        addAllBoundItemParticleEffects(player, itemLore);
        addAllBoundItemPotionEffects(player, itemLore);
    }


    // ********************************************************************************************//
    // Remaining methods
    // ********************************************************************************************//

    public int listContainsIgnoreCase(List<String> list, String str) {
        Iterator<String> iter = list.iterator();

        int line = 0;
        while (iter.hasNext()) {
            if (iter.next().toUpperCase().contains(str.toUpperCase())) return line;
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

    private void addPlayerEffectMetadata(Player player, String key, String value) {
        ArrayList<String> playerParticleEffects = (ArrayList) player.getMetadata(key).get(0).value();
        if (null == playerParticleEffects) {
            playerParticleEffects = new ArrayList<>();
            player.setMetadata(key, new FixedMetadataValue(plugin, playerParticleEffects));
        }
        playerParticleEffects.add(value);
    }
}
