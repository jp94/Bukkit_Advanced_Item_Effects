package edu.gatech.at.jamespark.AdvancedItemEffects.Scheduler;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import edu.gatech.at.jamespark.AdvancedItemEffects.Effects;

public class ParticleScheduler extends BukkitRunnable {

    private Effects effects;

    public ParticleScheduler(Effects effects) {
        this.effects = effects;
    }

    @Override
    public void run() {

        // Checking players with effects. More efficient than without this
        // check.
        for (Player player : effects.playerList) {
            
            for (int x = 0; x < effects.particleEffectsList.length; x++) {
                String particleEffect = effects.particleEffectsList[x];
                if (player.hasMetadata(particleEffect)) {
                    player.getWorld()
                            .playEffect(
                                    player.getLocation(),
                                    Effect.valueOf(particleEffect.toUpperCase()),
                                    7, 40);
                }
            }
        }
    }
}
