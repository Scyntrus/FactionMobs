package com.gmail.scyntrus.fmob;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.entity.Entity;

public class DisguiseConnector {
    public static void disguiseAsPlayer(net.minecraft.server.v1_11_R1.Entity entity) {
        Entity bukkitEntity = entity.getBukkitEntity();
        Disguise disguise = DisguiseAPI.getDisguise(bukkitEntity);
        if (disguise != null && disguise instanceof PlayerDisguise) {
            if (((PlayerDisguise) disguise).getName().equals(entity.getCustomName())) return;
        }
        disguise = new PlayerDisguise(entity.getCustomName(), null);
        DisguiseAPI.disguiseToAll(bukkitEntity, disguise);
    }
    public static void disguiseAsPolarBear(net.minecraft.server.v1_11_R1.Entity entity) {
        Entity bukkitEntity = entity.getBukkitEntity();
        Disguise disguise = DisguiseAPI.getDisguise(bukkitEntity);
        if (disguise != null && disguise.getType() == DisguiseType.POLAR_BEAR) {
            return;
        }
        disguise = new MobDisguise(DisguiseType.POLAR_BEAR);
        DisguiseAPI.disguiseToAll(bukkitEntity, disguise);
    }
    public static void disguiseAsIronGolem(net.minecraft.server.v1_11_R1.Entity entity) {
        Entity bukkitEntity = entity.getBukkitEntity();
        Disguise disguise = DisguiseAPI.getDisguise(bukkitEntity);
        if (disguise != null && disguise.getType() == DisguiseType.IRON_GOLEM) {
            return;
        }
        disguise = new MobDisguise(DisguiseType.IRON_GOLEM);
        DisguiseAPI.disguiseToAll(bukkitEntity, disguise);
    }
}
