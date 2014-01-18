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

public class AIERemoveCommand implements CommandExecutor {

    private Effects effects;

    public AIERemoveCommand(Effects effects) {
        this.effects = effects;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (label.equalsIgnoreCase("AIEremove")) {
                if ((player.hasPermission("AIE.remove")) || (player.isOp())) {
                    // TODO clean this up
                    if (player.getItemInHand().getItemMeta() == null) {
                        player.sendMessage(ChatColor.RED
                                + "You must be holding an item!");
                        return false;
                    }
                    if (args.length > 0 && args.length < 3) {
                        if ((effects.stringArrayContainsIgnoreCase(
                                effects.potionEffectsList, args[0]))
                                || effects.stringArrayContainsIgnoreCase(
                                        effects.particleEffectsList, args[0])) {
                            ItemStack item = player.getItemInHand();
                            ItemMeta itemMeta = item.getItemMeta();
                            List<String> lore = itemMeta.getLore();
                            if (lore != null
                                    && (lore.contains(ChatColor.GOLD
                                            + "Effects:"))) {
                                lore.remove(effects.listContainsIgnoreCase(
                                        lore, args[0]));
                                itemMeta.setLore(lore);
                                item.setItemMeta(itemMeta);
                                player.sendMessage(ChatColor.AQUA
                                        + "Magical unicorn has granted your wish.");
                                // TODO
                                effects.removeAllBoundEffects(player);
                                effects.addArmorAndHeldItemEffects(player);
                                return true;
                            } else {
                                player.sendMessage(ChatColor.RED
                                        + "There is no " + args[0]
                                        + " effect to remove.");
                                return false;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED
                                    + "Wrong effect type");
                            return false;
                        }
                    } else {
                        String[] missingArgs = {
                                ChatColor.RED + "Incomplete command input.",
                                ChatColor.RED + "/aieremove [effect]" };
                        player.sendMessage(missingArgs);
                        return false;
                    }
                } else {
                    player.sendMessage(player.getDisplayName()
                            + ChatColor.YELLOW
                            + " lacks \"AIE.remove\" permission.");
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
