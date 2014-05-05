package edu.gatech.at.jamespark.AdvancedItemEffects;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import edu.gatech.at.jamespark.AdvancedItemEffects.Commands.*;
import edu.gatech.at.jamespark.AdvancedItemEffects.Listeners.*;
import edu.gatech.at.jamespark.AdvancedItemEffects.Scheduler.ParticleScheduler;

// TODO Add custom Items to /give, /i etc. list
// TODO On item change, show text along with effects
public class Main extends JavaPlugin {

    private ParticleScheduler particleScheduler;

    @Override
    public void onEnable() {

        Effects effects = new Effects(this);
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerEventListener(effects), this);
        getCommand("aieadd").setExecutor(new AIEAddCommand(effects));
        getCommand("aieremove").setExecutor(new AIERemoveCommand(effects));
        getCommand("aieclear").setExecutor(new AIEClearCommand(effects));
        particleScheduler = new ParticleScheduler(effects);
        particleScheduler.runTaskTimer(this, 0, 60);
        // this.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        particleScheduler.cancel();
    }
}