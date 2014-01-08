package edu.gatech.at.jamespark.AdvancedItemEffects.Listeners;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import edu.gatech.at.jamespark.AdvancedItemEffects.Effects;

public class PlayerEventListener implements Listener {

    private Effects effects;

    public PlayerEventListener(Effects effects) {
        this.effects = effects;
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());
        if (heldItem != null) {
            List<String> heldItemLore = heldItem.getItemMeta().getLore();
            if (heldItemLore != null
                    && heldItemLore.contains(ChatColor.GOLD + "Effects:")) {
                effects.removeAllBoundEffects(player);
                effects.addAllBoundEffects(player, heldItemLore);
            } else {
                effects.removeAllBoundEffects(player);
            }
        } else {
            effects.removeAllBoundEffects(player);
        }

        if (heldItem != null) {
            List<String> heldItemLore = heldItem.getItemMeta().getLore();
            if (heldItemLore != null
                    && heldItemLore.contains(ChatColor.GOLD + "Effects:")) {

            } else {
            }
        } else {

        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        List<String> droppedItemLore = event.getItemDrop().getItemStack()
                .getItemMeta().getLore();
        if (droppedItemLore != null
                && droppedItemLore.contains(ChatColor.GOLD + "Effects:")
                && (!player.getItemInHand().isSimilar(droppedItem))) {
            effects.removeAllBoundEffects(player);
        }

    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        List<String> pickedItemLore = event.getItem().getItemStack()
                .getItemMeta().getLore();

        if (pickedItemLore != null
                && pickedItemLore.contains(ChatColor.GOLD + "Effects:")) {
            effects.addAllBoundEffects(player, pickedItemLore);
        }
    }

    // TODO This hasn't been actually tested yet in-game.
    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        List<String> brokenItemLore = event.getBrokenItem().getItemMeta()
                .getLore();
        if (brokenItemLore != null
                && brokenItemLore.contains(ChatColor.GOLD + "Effects:")) {
            effects.removeAllBoundEffects(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        effects.removeAllBoundEffects(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemMeta itemMeta = player.getItemInHand().getItemMeta();
        if (itemMeta != null && itemMeta.hasLore()) {
            List<String> heldItemLore = player.getItemInHand().getItemMeta()
                    .getLore();
            if (!player.isDead()
                    && heldItemLore.contains(ChatColor.GOLD + "Effects:")) {
                effects.addAllBoundEffects(player, player.getItemInHand()
                        .getItemMeta().getLore());
            }
        }
    }
}
