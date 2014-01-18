package edu.gatech.at.jamespark.AdvancedItemEffects.Commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import edu.gatech.at.jamespark.AdvancedItemEffects.Effects;

public class AIEAddCommand implements CommandExecutor {

    private Effects effects;

    public AIEAddCommand(Effects effects) {
        this.effects = effects;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (label.equalsIgnoreCase("aieadd")) {
                if ((player.hasPermission("AIE.add")) || (player.isOp())) {
                    // TODO clean this up
                    if (player.getItemInHand().getItemMeta() == null) {
                        player.sendMessage(ChatColor.RED
                                + "You must be holding an item!");
                        return false;
                    }
                    if (args.length > 1) {
                        if (effects.stringArrayContainsIgnoreCase(
                                effects.potionEffectsList, args[0])) {
                            try {
                                int checkIfInt = Integer.parseInt(args[1]);
                                if (checkIfInt < 1 || checkIfInt > 2147483647) {
                                    player.sendMessage("Multiplier must be between 1 and 2147483647.");
                                    return false;
                                }
                            } catch (NumberFormatException e) {
                                player.sendMessage("Multiplier must be between 1 and 2147483647.");
                                return false;
                            }
                            ItemStack item = player.getItemInHand();
                            ItemMeta itemMeta = item.getItemMeta();
                            List<String> lore = itemMeta.getLore();
                            if (lore == null) {
                                itemMeta.setLore(Arrays.asList(ChatColor.GOLD
                                        + "Effects:"));
                                lore = itemMeta.getLore();
                            }
                            if (!(lore.contains(ChatColor.GOLD + "Effects:"))) {
                                lore.add(ChatColor.GOLD + "Effects:");
                                lore.add("          " + ChatColor.BLUE
                                        + args[0] + " " + ChatColor.WHITE
                                        + args[1]);
                            } else if (effects.listContainsIgnoreCase(lore,
                                    args[0]) != -1) {
                                player.sendMessage(ChatColor.RED
                                        + "This effect type is already used.");
                                return false;
                            } else {
                                lore.add("          " + ChatColor.BLUE
                                        + args[0] + " " + ChatColor.WHITE
                                        + args[1]);
                            }
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                            player.sendMessage(ChatColor.AQUA
                                    + "Magical unicorn has granted your wish.");
                            effects.addAllBoundItemPotionEffects(player, lore);
                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED
                                    + "Wrong effect type.");
                            return false;
                        }
                    } else if (args.length == 1) {
                        if (effects.stringArrayContainsIgnoreCase(
                                effects.particleEffectsList, args[0])) {
                            ItemStack item = player.getItemInHand();
                            ItemMeta itemMeta = item.getItemMeta();
                            List<String> lore = itemMeta.getLore();
                            if (lore == null) {
                                itemMeta.setLore(Arrays.asList(ChatColor.GOLD
                                        + "Effects:"));
                                lore = itemMeta.getLore();
                            }
                            if (!(lore.contains(ChatColor.GOLD + "Effects:"))) {
                                lore.add(ChatColor.GOLD + "Effects:");
                                lore.add("          " + ChatColor.GREEN
                                        + args[0]);
                            } else if (effects.listContainsIgnoreCase(lore,
                                    args[0]) != -1) {
                                player.sendMessage(ChatColor.RED
                                        + "This effect type is already used.");
                                return false;
                            } else {
                                lore.add("          " + ChatColor.GREEN
                                        + args[0]);
                            }
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                            player.sendMessage(ChatColor.AQUA
                                    + "Magical unicorn has granted your wish.");
                            effects.addAllBoundItemParticleEffects(player, lore);
                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED
                                    + "Wrong effect type.");
                            return false;
                        }
                    } else if (args.length == 0) {
                        String[] missingArgs = {
                                ChatColor.RED + "Incomplete command input.",
                                ChatColor.RED
                                        + "/aieadd [potion effect] [multiplier]",
                                ChatColor.RED + "/aieadd [particle effect]" };
                        player.sendMessage(missingArgs);
                        return false;
                    }
                } else {
                    player.sendMessage(player.getDisplayName()
                            + ChatColor.YELLOW
                            + " lacks \"AIE.add\" permission.");
                    return false;
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED
                    + "Must execute this command as a player.");
            return false;
        }
        return false;
    }
}
