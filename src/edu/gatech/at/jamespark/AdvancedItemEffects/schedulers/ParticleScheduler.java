package edu.gatech.at.jamespark.AdvancedItemEffects.schedulers;

import edu.gatech.at.jamespark.AdvancedItemEffects.Effects;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ParticleScheduler extends BukkitRunnable {

    private Effects effects;

    public ParticleScheduler(Effects effects) {
        this.effects = effects;
    }

    @Override
    public void run() {

        for (Player player : effects.playerList) {
            for (String particleEffect : (ArrayList<String>) player.getMetadata(effects.PARTICLE_KEY).get(0).value()) {
                if (player.hasMetadata(particleEffect)) {
                    player.getWorld()
                            .playEffect(
                                    player.getLocation(),
                                    Effect.valueOf(particleEffect), // @TODO removed toUpperCase() on particleEffect
                                    7, 40);
                }
            }
        }
    }
}
