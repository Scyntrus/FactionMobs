package com.gmail.scyntrus.ifactions;

public class Faction {
	
	public Object faction;
	
	public Faction (Object faction) {
		this.faction = faction;
	}
	
	public int getRelationTo(Faction other) {
		if (Factions.factionsVersion == 2) {
			Rel2.getRelation(this.faction, other.faction);
		} else if (Factions.factionsVersion == 6) {
			Rel6.getRelation(this.faction, other.faction);
		} else if (Factions.factionsVersion == 8) {
			Rel8.getRelation(this.faction, other.faction);
		}
		return 0;
	}
	
	public boolean isNone() {
		if (Factions.factionsVersion == 2) {
			return ((com.massivecraft.factions.entity.Faction)faction).isNone();
		} else if (Factions.factionsVersion == 6) {
			return ((com.massivecraft.factions.Faction)faction).isNone();
		} else if (Factions.factionsVersion == 8) {
			return ((com.massivecraft.factions.Faction)faction).isNone();
		}
		return true;
	}
	
	public String getName() {
		if (Factions.factionsVersion == 2) {
			return ((com.massivecraft.factions.entity.Faction)faction).getName();
		} else if (Factions.factionsVersion == 6) {
			return ((com.massivecraft.factions.Faction)faction).getTag();
		} else if (Factions.factionsVersion == 8) {
			return ((com.massivecraft.factions.Faction)faction).getTag();
		}
		return "";
	}
	
	public double getPower() {
		if (Factions.factionsVersion == 2) {
			return ((com.massivecraft.factions.entity.Faction)faction).getPower();
		} else if (Factions.factionsVersion == 6) {
			return ((com.massivecraft.factions.Faction)faction).getPower();
		} else if (Factions.factionsVersion == 8) {
			return ((com.massivecraft.factions.Faction)faction).getPower();
		}
		return 0;
	}
	
	public int getPowerRounded() {
		return (int)Math.round(getPower());
	}
	
	public boolean equals(Faction other) {
		return this.getName().equals(other.getName());
	}
}
