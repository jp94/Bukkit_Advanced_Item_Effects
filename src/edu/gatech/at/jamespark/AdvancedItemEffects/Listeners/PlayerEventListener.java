package edu.gatech.at.jamespark.AdvancedItemEffects.Listeners;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import edu.gatech.at.jamespark.AdvancedItemEffects.Effects;

// TODO Methods using removeSingleItemEffects instead of addArmorAndHeldItemEffects have issues if an equipped item has one of the same effects as an held item that has been removed.
public class PlayerEventListener implements Listener {

    private Effects effects;

    public PlayerEventListener(Effects effects) {
        this.effects = effects;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (droppedItem.getItemMeta().hasLore()) {
            List<String> droppedItemLore = droppedItem.getItemMeta().getLore();
            if (droppedItemLore.contains(ChatColor.GOLD + "Effects:")
                    && (!player.getItemInHand().isSimilar(droppedItem))) {
                effects.removeSingleItemEffects(player, droppedItemLore);
            }
        }
    }

    @EventHandler
    public void onPlayerInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();

            effects.removeAllBoundEffects(player);
            effects.addArmorAndHeldItemEffects(player);
        }
    }

    // TODO This hasn't been actually tested yet in-game.
    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBrokenItem().getItemMeta().hasLore()) {
            List<String> brokenItemLore = event.getBrokenItem().getItemMeta()
                    .getLore();
            if (brokenItemLore.contains(ChatColor.GOLD + "Effects:")) {
                effects.removeSingleItemEffects(player, brokenItemLore);
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        ItemStack previousItem = player.getInventory().getItemInHand();
        if (previousItem != null && previousItem.hasItemMeta()
                && previousItem.getItemMeta().hasLore()) {
            List<String> previousItemLore = previousItem.getItemMeta()
                    .getLore();
            if (previousItemLore.contains(ChatColor.GOLD + "Effects:")) {
                effects.removeSingleItemEffects(player, previousItemLore);
            }
        }

        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());
        if (heldItem != null && heldItem.getItemMeta().hasLore()) {
            List<String> heldItemLore = heldItem.getItemMeta().getLore();
            if (heldItemLore.contains(ChatColor.GOLD + "Effects:")) {
                effects.addAllItemEffects(player, heldItemLore);
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack pickedItem = event.getItem().getItemStack();
        if (pickedItem.getItemMeta().hasLore()) {
            List<String> pickedItemLore = pickedItem.getItemMeta().getLore();
            if (pickedItemLore.contains(ChatColor.GOLD + "Effects:")) {
                effects.addAllItemEffects(player, pickedItemLore);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        effects.addArmorAndHeldItemEffects(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        effects.removeAllBoundEffects(player);
    }
}
