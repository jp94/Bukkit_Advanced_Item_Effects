package edu.gatech.at.jamespark.AdvancedItemEffects;

import edu.gatech.at.jamespark.AdvancedItemEffects.constructors.EffectEffectsList;
import edu.gatech.at.jamespark.AdvancedItemEffects.constructors.PotionEffectsList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

// TODO Better organization & javadoc

/**
 * Contains helper methods
 *
 * @author James
 */
public class Effects {

    private String[] potionEffectsList = PotionEffectsList.getArray();
    private String[] particleEffectsList = EffectEffectsList.getArray();

    public final String POTION_KEY = "AIE_CURRENT_POTION_EFFECTS";
    public final String PARTICLE_KEY = "AIE_CURRENT_PARTICLE_EFFECTS";

    public Set<Player> playerList = new HashSet<>();
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
                playerList.add(player);
                addPlayerEffectMetadata(player, PARTICLE_KEY, effectSplit[1].toUpperCase());
            }
        }
    }

    public void addAllBoundItemPotionEffects(Player player,
                                             List<String> heldItemLore) throws NumberFormatException {
        System.out.println("POTION EFFECT ADDING");
        String currentLoreLine;
        for (int x = 0; x < potionEffectsList.length; x++) {
            System.out.println("Checking: " + potionEffectsList[x]);
            int matchingLineNumber = listContainsIgnoreCase(heldItemLore,
                    potionEffectsList[x]);
            if (matchingLineNumber != -1) {
                System.out.println("MATCH!");
                int multiplier = -1;
                System.out.println("LORE NAME: " + heldItemLore.get(matchingLineNumber));
                currentLoreLine = heldItemLore.get(matchingLineNumber);
                multiplier = Integer.parseInt(currentLoreLine.substring(currentLoreLine.lastIndexOf(ChatColor.WHITE.toString()) + 2));
                System.out.println("multiplier:" + multiplier);
                if (multiplier > 0) {
                    addItemPotionEffect(player,
                            PotionEffectType.getByName(potionEffectsList[x]),
                            multiplier);
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
        System.out.println("Removing effects");
        if (player.hasMetadata(POTION_KEY)) {
            // Get Player's active potion effects from this plugin
            ArrayList<String> playerPotionEffects = (ArrayList) player.getMetadata(POTION_KEY).get(0).value();
            System.out.println(playerPotionEffects + " < should be arraylist");
            // @TODO Check if all player's effects get removed
            // Clear Player's active potion effects
            // Note: We are avoiding other active potion effects not from this plugin
            for (String playerPotionEffect : playerPotionEffects) {
                System.out.println("Targeted: " + playerPotionEffects);
                PotionEffectType potionEffectType = PotionEffectType
                        .getByName(playerPotionEffect);
                System.out.println("Removing: " + potionEffectType);
                player.removePotionEffect(potionEffectType);
            }
            player.removeMetadata(POTION_KEY, plugin);
        }

        player.removeMetadata(PARTICLE_KEY, plugin);

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
        addPlayerEffectMetadata(player, POTION_KEY, type.getName());
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
        if (null == player || key == null || value == null || key.isEmpty() || value.isEmpty()) return;

        List playerMetadata = player.getMetadata(key);
        if (playerMetadata.isEmpty()) {
            System.out.println("Player metadata was null.");
            player.setMetadata(key, new FixedMetadataValue(plugin, new ArrayList<>()));
        }
        ArrayList<String> playerEffects = (ArrayList) player.getMetadata(key).get(0).value();
        playerEffects.add(value);
        System.out.println("Adding new effect: " + value);
        System.out.println("Contains?: " + ((ArrayList) player.getMetadata(key).get(0).value()).contains(value));
    }
}
