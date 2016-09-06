package com.gmail.scyntrus.fmob;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.entity.Entity;

public class DisguiseConnector {
    public static void disguise(net.minecraft.server.v1_10_R1.Entity entity) {
        Entity bukkitEntity = entity.getBukkitEntity();
        Disguise disguise = DisguiseAPI.getDisguise(bukkitEntity);
        if (disguise != null && disguise instanceof PlayerDisguise) {
            if (((PlayerDisguise) disguise).getName().equals(entity.getCustomName())) return;
        }
        disguise = new PlayerDisguise(entity.getCustomName(), null);
        DisguiseAPI.disguiseToAll(bukkitEntity, disguise);
    }
}
