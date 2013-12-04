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
		return Rel2.getRelation(this.faction, ((Faction2)other).faction);
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
