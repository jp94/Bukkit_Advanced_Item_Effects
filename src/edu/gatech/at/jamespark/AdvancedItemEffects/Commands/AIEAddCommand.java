package edu.gatech.at.jamespark.AdvancedItemEffects.commands;

import edu.gatech.at.jamespark.AdvancedItemEffects.Effects;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class AIEAddCommand implements CommandExecutor {

    private Effects effects;

    public AIEAddCommand(Effects effects) {
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

        // Command: aieadd
        if (label.equalsIgnoreCase("aieadd")) {

            Player player = (Player) sender;

            // Check necessary cases.
            if ((!player.hasPermission("AIE.add")) && (!player.isOp())) {
                player.sendMessage(player.getDisplayName() + ChatColor.YELLOW
                        + " lacks \"AIE.add\" permission.");
                return false;
            }

            if (player.getItemInHand().getItemMeta() == null) {
                player.sendMessage(ChatColor.RED
                        + "You must be holding an item!");
                return false;
            }

            if (args.length == 0) {
                String[] missingArgs = {
                        ChatColor.RED + "Incomplete command input.",
                        ChatColor.RED + "/aieadd [potion effect] [multiplier]",
                        ChatColor.RED + "/aieadd [particle effect]"};
                player.sendMessage(missingArgs);
                return false;
            }

            // Initiate variables
            ItemStack item = player.getItemInHand();
            ItemMeta itemMeta = item.getItemMeta();
            List<String> lore = itemMeta.getLore();

            // if /aieadd [potion effect] [multiplier]
            if (args.length > 1) {

                if (!effects.stringArrayContainsIgnoreCase(
                        effects.potionEffectsList, args[0])) {
                    player.sendMessage(ChatColor.RED + "Wrong effect type.");
                    return false;
                }

                // Checks for valid [multiplier]
                try {
                    int checkIfInt = Integer.parseInt(args[1]);
                    if (checkIfInt < 1) {
                        player.sendMessage("Multiplier must be between 1 and 2147483647.");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("Multiplier must be between 1 and 2147483647.");
                    return false;
                }

                if (lore == null) {
                    itemMeta.setLore(Arrays.asList(ChatColor.GOLD + "Effects:"));
                    lore = itemMeta.getLore();
                }

                if (!(lore.contains(ChatColor.GOLD + "Effects:"))) {
                    lore.add(ChatColor.GOLD + "Effects:");
                }

                if (effects.listContainsIgnoreCase(lore, args[0]) != -1) {
                    player.sendMessage(ChatColor.RED
                            + "This effect type is already used.");
                    return false;
                }

                lore.add("          " + ChatColor.BLUE + args[0].toUpperCase()
                        + " " + ChatColor.WHITE + args[1]);

                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                player.sendMessage(ChatColor.AQUA
                        + "A potion effect has been added.");
                effects.addAllBoundItemPotionEffects(player, lore);
                return true;

                // if /aieadd [particle effect]
            } else if (args.length == 1) {

                if (effects.stringArrayContainsIgnoreCase(
                        effects.potionEffectsList, args[0])) {
                    player.sendMessage(ChatColor.RED
                            + "/aieadd [potion effect] [multiplier]");
                    return false;
                } else if (!effects.stringArrayContainsIgnoreCase(
                        effects.particleEffectsList, args[0])) {
                    player.sendMessage(ChatColor.RED + "Wrong effect type.");
                    return false;
                }

                if (lore == null) {
                    itemMeta.setLore(Arrays.asList(ChatColor.GOLD + "Effects:"));
                    lore = itemMeta.getLore();
                }

                if (!(lore.contains(ChatColor.GOLD + "Effects:"))) {
                    lore.add(ChatColor.GOLD + "Effects:");
                }

                if (effects.listContainsIgnoreCase(lore, args[0]) != -1) {
                    player.sendMessage(ChatColor.RED
                            + "This effect type is already used.");
                    return false;
                }

                lore.add("          " + ChatColor.GREEN + args[0].toUpperCase());

                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                player.sendMessage(ChatColor.AQUA
                        + "A particle effect has been added.");
                effects.addAllBoundItemParticleEffects(player, lore);
                return true;
            }
        }
        return false;
    }
}
