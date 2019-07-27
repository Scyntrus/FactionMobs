package com.gmail.scyntrus.ifactions.feudal;

import com.gmail.scyntrus.fmob.FactionMob;
import de.browniecodez.feudal.main.Main;
import me.forseth11.Turrets.TurretAttackMobEvent;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import us.forseth11.feudal.kingdoms.Kingdom;
import us.forseth11.feudal.user.User;

public class TurretsListener implements Listener {

    Plugin plugin;

    TurretsListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerCommand(TurretAttackMobEvent e) {
        if (((CraftLivingEntity) e.getTarget()).getHandle() instanceof FactionMob) {
            User user = Main.getUser(e.getTurret().getOwnerUUID());
            if (user != null) {
                Kingdom k = Main.getKingdom(user.getKingdomUUID());
                if (k != null) {
                    if (new FeudalKingdom(k)
                            .getRelationTo(((FactionMob) ((CraftLivingEntity) e.getTarget()).getHandle())
                                    .getFaction()) > 0) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
