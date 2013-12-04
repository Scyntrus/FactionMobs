package com.gmail.scyntrus.ifactions;

public class Faction8 extends Faction {
	
	public com.massivecraft.factions.Faction faction;

	public Faction8 (com.massivecraft.factions.Faction faction) {
		this.faction = faction;
	}
	
	public Faction8 (Object faction) {
		this.faction = (com.massivecraft.factions.Faction) faction;
	}

	@Override
	public int getRelationTo(Faction other) {
		return Rel8.getRelation(this.faction, ((Faction8)other).faction);
	}

	@Override
	public boolean isNone() {
		return ((com.massivecraft.factions.Faction)faction).isNone();
	}

	@Override
	public String getName() {
		return ((com.massivecraft.factions.Faction)faction).getTag();
	}

	@Override
	public double getPower() {
		return ((com.massivecraft.factions.Faction)faction).getPower();
	}
}
