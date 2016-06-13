package com.gmail.scyntrus.ifactions.k;

import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import com.gmail.scyntrus.ifactions.Faction;
import net.minecraft.server.v1_10_R1.Entity;
import org.bukkit.metadata.FixedMetadataValue;

class Kingdom extends Faction {

    public org.kingdoms.constants.kingdom.Kingdom k;

    public Kingdom (org.kingdoms.constants.kingdom.Kingdom faction) {
        this.k = faction;
    }

    public Kingdom (Object faction) {
        this.k = (org.kingdoms.constants.kingdom.Kingdom) faction;
    }

    @Override
    public int getRelationTo(Faction other) {
        if (k == null || isNone()) return 0;
        if (this.getName().equals(other.getName())) return 1;
        if (k.isAllianceWith(((Kingdom)other).k)) return 1;
        if (k.isEnemyWith(((Kingdom)other).k)) return -1;
        return 0;
    }

    @Override
    public boolean isNone() {
        return (k == null);
    }

    @Override
    public String getName() {
        if (k == null) return "";
        return k.getKingdomName();
    }

    @Override
    public double getPower() {
        if (k == null) return 0;
        return k.getMight() + k.getLand(); // Power will be might + land
    }

    @Override
    public boolean monstersNotAllowed() {
        return false;
    }

    @Override
    public void processMob(FactionMob mob) {
        mob.getEntity().getBukkitEntity().setMetadata("kingdom+" + this.getName(), new FixedMetadataValue(FactionMobs.instance, true));
    }

    @Override
    public boolean dontAttack(Entity entity) {
        return entity.getBukkitEntity().hasMetadata("kingdom+" + this.getName());
    }
}
