package edu.gatech.at.jamespark.AdvancedItemEffects.Listeners;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
//import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import edu.gatech.at.jamespark.AdvancedItemEffects.Effects;

public class PlayerEventListener implements Listener {

    private Effects effects;

    public PlayerEventListener(Effects effects) {
        this.effects = effects;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        // Prevents unnecessary looping.
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (droppedItem.getItemMeta().hasLore()) {
            List<String> droppedItemLore = droppedItem.getItemMeta().getLore();
            if (droppedItemLore.contains(ChatColor.GOLD + "Effects:")
                    && (!player.getItemInHand().isSimilar(droppedItem))) {
                // Remove and add effects
                effects.removeAllBoundEffects(player);
                effects.addItemEffects(player, true, true, false);
            }
        }
    }

    // TODO Not very efficient. Wait on bukkit update.
    @EventHandler
    public void onPlayerInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            effects.removeAllBoundEffects(player);
            effects.addItemEffects(player, true, true, true);
        }
    }

    // @EventHandler
    // public void onInventoryDragEventt(InventoryDragEvent event) {
    // System.out.println(event.getWhoClicked().getName());
    // }

    // TODO This hasn't been actually tested yet in-game.
    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBrokenItem().getItemMeta().hasLore()) {
            List<String> brokenItemLore = event.getBrokenItem().getItemMeta()
                    .getLore();
            if (brokenItemLore.contains(ChatColor.GOLD + "Effects:")) {
                effects.removeAllBoundEffects(player);
                effects.addItemEffects(player, true, true, true);
            }
        }
    }

    // NOTE: This event.getItem().getItemStack() does not match with
    // player.getItemInHand()
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        // This event has two steps for optimizations.
        // What player was holding, and what player is holding now.

        ItemStack previousItem = player.getInventory().getItemInHand();
        if (previousItem != null && previousItem.hasItemMeta()
                && previousItem.getItemMeta().hasLore()) {
            List<String> previousItemLore = previousItem.getItemMeta()
                    .getLore();
            if (previousItemLore.contains(ChatColor.GOLD + "Effects:")) {

                effects.removeAllBoundEffects(player);
                effects.addItemEffects(player, true, true, false);
            }
        }

        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());
        if (heldItem != null && heldItem.getItemMeta().hasLore()) {
            List<String> heldItemLore = heldItem.getItemMeta().getLore();
            if (heldItemLore.contains(ChatColor.GOLD + "Effects:")) {
                effects.addItemEffects(player, false, false, true, heldItemLore);
            }
        }
    }

    // NOTE: This event.getItem().getItemStack() does not match with
    // player.getItemInHand()
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack pickedItem = event.getItem().getItemStack();
        if (pickedItem.getItemMeta().hasLore()) {
            List<String> pickedItemLore = pickedItem.getItemMeta().getLore();
            if (pickedItemLore.contains(ChatColor.GOLD + "Effects:")) {
                effects.addItemEffects(player, false, true, true,
                        pickedItemLore);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        effects.removeAllBoundEffects(player);
        effects.addItemEffects(player, true, true, true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        effects.removeAllBoundEffects(player);
    }
}
