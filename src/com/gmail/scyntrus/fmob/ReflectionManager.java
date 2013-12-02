package com.gmail.scyntrus.fmob;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.server.v1_7_R1.EntityTypes;
import net.minecraft.server.v1_7_R1.Navigation;
import net.minecraft.server.v1_7_R1.PathfinderGoalSelector;

public class ReflectionManager {
	
	public static Method entityTypesA = null;
	public static Field navigationE = null;
	public static Field pathfinderGoalSelectorA = null;
	
	public static boolean goodNavigationE = false;
	public static boolean goodPathfinderGoalSelectorA = false;
	
	public static boolean init() {
		try {
			entityTypesA = EntityTypes.class.getDeclaredMethod("a", new Class[] {Class.class, String.class, int.class});
		} catch (Exception e1) {
			try {
				entityTypesA = EntityTypes.class.getDeclaredMethod("func_75618_a", new Class[] {Class.class, String.class, int.class});
			} catch (Exception e2) {
	        	e1.printStackTrace();
	        	e2.printStackTrace();
				return false;
			}
		}
		entityTypesA.setAccessible(true);
		try {
			navigationE = Navigation.class.getDeclaredField("e");
			navigationE.setAccessible(true);
			goodNavigationE = true;
		} catch (Exception e1) {
			try {
				navigationE = Navigation.class.getDeclaredField("field_75512_e");
				navigationE.setAccessible(true);
				goodNavigationE = true;
			} catch (Exception e2) {
				System.out.println("[Faction Mobs] [Minor Error] Field not found: Navigation.e; Custom pathfinding distances cannot be set");
			}
		}
		try {
			pathfinderGoalSelectorA = PathfinderGoalSelector.class.getDeclaredField("a");
			pathfinderGoalSelectorA.setAccessible(true);
			goodPathfinderGoalSelectorA = true;
		} catch (Exception e1) {
			try {
				pathfinderGoalSelectorA = PathfinderGoalSelector.class.getDeclaredField("field_75782_a");
				pathfinderGoalSelectorA.setAccessible(true);
				goodPathfinderGoalSelectorA = true;
			} catch (Exception e2) {
				System.out.println("[Faction Mobs] [Minor Error] Field not found: PathfinderGoalSelector.a; Unable to override mob goals");
			}
		}
		return true;
	}
}
