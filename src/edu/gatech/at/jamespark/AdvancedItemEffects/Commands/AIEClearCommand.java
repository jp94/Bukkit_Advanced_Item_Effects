package edu.gatech.at.jamespark.AdvancedItemEffects.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import edu.gatech.at.jamespark.AdvancedItemEffects.Effects;

public class AIEClearCommand implements CommandExecutor {

    private Effects effects;

    public AIEClearCommand(Effects effects) {
        this.effects = effects;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (label.equalsIgnoreCase("AIEClear")) {
                if ((player.hasPermission("AIE.clear")) || (player.isOp())) {
                    // TODO clean this up
                    if (player.getItemInHand().getItemMeta() == null) {
                        player.sendMessage(ChatColor.RED
                                + "You must be holding an item!");
                        return false;
                    }
                    ItemStack item = player.getItemInHand();
                    ItemMeta itemMeta = item.getItemMeta();
                    List<String> lore = itemMeta.getLore();
                    if (lore != null
                            && (lore.contains(ChatColor.GOLD + "Effects:"))) {

                        // TODO remove "Effects:" as well.
                        for (int x = lore.size() - 1; x >= effects
                                .listContainsIgnoreCase(lore, "Effects:")
                                && x > -1; x--) {
                            lore.remove(x);
                        }

                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                        player.sendMessage(ChatColor.AQUA
                                + "Magical unicorn has granted your wish.");
                        effects.removeAllBoundEffects(player);
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED
                                + "There are no effects to remove.");
                        return false;
                    }
                } else {
                    player.sendMessage(player.getDisplayName()
                            + ChatColor.YELLOW
                            + " lacks \"AIE.clear\" permission.");
                    return false;
                }
            } else {
                sender.sendMessage(ChatColor.RED
                        + "Must execute this command as a player.");
                return false;
            }
        }
        return false;
    }
}
