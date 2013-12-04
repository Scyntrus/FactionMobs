package com.gmail.scyntrus.ifactions;

public class Faction2 extends Faction {
	
	public com.massivecraft.factions.entity.Faction faction;

	public Faction2 (com.massivecraft.factions.entity.Faction faction) {
		this.faction = faction;
	}
	
	public Faction2 (Object faction) {
		this.faction = (com.massivecraft.factions.entity.Faction) faction;
	}
	
	@Override
	public int getRelationTo(Faction other) {
		com.massivecraft.factions.Rel rel = faction.getRelationTo(((Faction2)other).faction);
		if (rel.equals(com.massivecraft.factions.Rel.ENEMY)) {
			return -1;
		} else if (rel.equals(com.massivecraft.factions.Rel.NEUTRAL)) {
			return 0;
		} else if (rel.equals(com.massivecraft.factions.Rel.ALLY) || rel.equals(com.massivecraft.factions.Rel.MEMBER)) {
			return 1;
		}
		return 0;
	}

	@Override
	public boolean isNone() {
		return ((com.massivecraft.factions.entity.Faction)faction).isNone();
	}

	@Override
	public String getName() {
		return ((com.massivecraft.factions.entity.Faction)faction).getName();
	}

	@Override
	public double getPower() {
		return ((com.massivecraft.factions.entity.Faction)faction).getPower();
	}
}
