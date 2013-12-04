package com.gmail.scyntrus.ifactions;

public class Faction6 extends Faction {
	
	public com.massivecraft.factions.Faction faction;

	public Faction6 (com.massivecraft.factions.Faction faction) {
		this.faction = faction;
	}
	
	public Faction6 (Object faction) {
		this.faction = (com.massivecraft.factions.Faction) faction;
	}
	
	@Override
	public int getRelationTo(Faction other) {
		Object rel = faction.getRelationTo(((Faction6)other).faction);
		if (rel.equals(com.massivecraft.factions.struct.Relation.ENEMY)) {
			return -1;
		} else if (rel.equals(com.massivecraft.factions.struct.Relation.NEUTRAL)) {
			return 0;
		} else if (rel.equals(com.massivecraft.factions.struct.Relation.ALLY) || rel.equals(com.massivecraft.factions.struct.Relation.MEMBER)) {
			return 1;
		}
		return 0;
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
