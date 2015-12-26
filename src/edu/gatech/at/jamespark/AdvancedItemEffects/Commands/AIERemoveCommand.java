package edu.gatech.at.jamespark.AdvancedItemEffects.commands;

import edu.gatech.at.jamespark.AdvancedItemEffects.Effects;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class AIERemoveCommand implements CommandExecutor {

    private Effects effects;

    public AIERemoveCommand(Effects effects) {
        this.effects = effects;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        // Prevent executing from server console.
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED
                    + "Must execute this command as a player.");
            return false;
        }

        // Command: aieremove
        if (label.equalsIgnoreCase("aieremove")) {

            Player player = (Player) sender;

            // Check necessary cases.
            if ((!player.hasPermission("AIE.remove")) && (!player.isOp())) {
                player.sendMessage(player.getDisplayName() + ChatColor.YELLOW
                        + " lacks \"AIE.remove\" permission.");
                return false;
            }

            if (player.getItemInHand().getItemMeta() == null) {
                player.sendMessage(ChatColor.RED
                        + "You must be holding an item!");
                return false;
            }

            if (args.length < 1 || args.length > 2) {
                String[] missingArgs = {
                        ChatColor.RED + "Incomplete command input.",
                        ChatColor.RED + "/aieremove [effect]"};
                player.sendMessage(missingArgs);
                return false;
            }

            if ((!effects.stringArrayContainsIgnoreCase(
                    effects.potionEffectsList, args[0]))
                    && !effects.stringArrayContainsIgnoreCase(
                    effects.particleEffectsList, args[0])) {
                player.sendMessage(ChatColor.RED + "Wrong effect type");
                return false;
            }

            ItemStack item = player.getItemInHand();
            ItemMeta itemMeta = item.getItemMeta();
            List<String> lore = itemMeta.getLore();

            if (lore != null && (lore.contains(ChatColor.GOLD + "Effects:"))) {
                if (lore.size() == 2) {
                    lore = null;
                } else {
                    lore.remove(effects.listContainsIgnoreCase(lore, args[0]));
                }
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                player.sendMessage(ChatColor.AQUA
                        + "An effect has been removed.");
                effects.removeAllBoundEffects(player);
                effects.addItemEffects(player, true, true, true);
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "There is no " + args[0]
                        + " effect to remove.");
                return false;
            }
        }
        return false;
    }
}
