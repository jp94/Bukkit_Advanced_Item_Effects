package edu.gatech.at.jamespark.AdvancedItemEffects;

import edu.gatech.at.jamespark.AdvancedItemEffects.commands.AIEAddCommand;
import edu.gatech.at.jamespark.AdvancedItemEffects.commands.AIEClearCommand;
import edu.gatech.at.jamespark.AdvancedItemEffects.commands.AIERemoveCommand;
import edu.gatech.at.jamespark.AdvancedItemEffects.listeners.PlayerEventListener;
import edu.gatech.at.jamespark.AdvancedItemEffects.schedulers.ParticleScheduler;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

// https://github.com/jp94/Bukkit_Advanced_Item_Effects/commits/master

// @TODO On item change, show text along with effects
public class Main extends JavaPlugin {

    private ParticleScheduler particleScheduler;

    @Override
    public void onEnable() {

        Effects effects = new Effects(this);
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerEventListener(effects, this),
                this);
        getCommand("aieadd").setExecutor(new AIEAddCommand(effects));
        getCommand("aieremove").setExecutor(new AIERemoveCommand(effects));
        getCommand("aieclear").setExecutor(new AIEClearCommand(effects));
        particleScheduler = new ParticleScheduler(effects);
        particleScheduler.runTaskTimer(this, 0, 60);
        this.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        particleScheduler.cancel();
    }
}