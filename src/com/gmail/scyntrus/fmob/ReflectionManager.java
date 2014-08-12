package com.gmail.scyntrus.fmob;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityTypes;
import net.minecraft.server.v1_7_R4.Navigation;
import net.minecraft.server.v1_7_R4.PathfinderGoalSelector;

public class ReflectionManager {
	public static Field navigationE = null;
	public static Field pathfinderGoalSelectorB = null;
	public static Field EntityGoalSelector = null;
	
	public static boolean goodNavigationE = false;
	public static boolean goodPathfinderGoalSelectorB = false;
	public static boolean goodEntitySelectors = false;

	@SuppressWarnings("rawtypes")
	public static Map mapC;
	@SuppressWarnings("rawtypes")
	public static Map mapD;
	@SuppressWarnings("rawtypes")
	public static Map mapF;
	@SuppressWarnings("rawtypes")
	public static Map mapG;
	
	@SuppressWarnings("rawtypes")
	public static boolean init() {
		try {
	    	Field fieldC = EntityTypes.class.getDeclaredField("c"); //TODO: Update name on version change
	        fieldC.setAccessible(true);
	    	Field fieldD = EntityTypes.class.getDeclaredField("d");
	        fieldD.setAccessible(true);
	    	Field fieldF = EntityTypes.class.getDeclaredField("f");
	        fieldF.setAccessible(true);
	    	Field fieldG = EntityTypes.class.getDeclaredField("g");
	        fieldG.setAccessible(true);
	        mapC = (Map) fieldC.get(null);
	        mapD = (Map) fieldD.get(null);
	        mapF = (Map) fieldF.get(null);
	        mapG = (Map) fieldG.get(null);
		} catch (Exception e1) {
			try {
		    	Field fieldC = EntityTypes.class.getDeclaredField("field_75625_b");
		        fieldC.setAccessible(true);
		    	Field fieldD = EntityTypes.class.getDeclaredField("field_75626_c");
		        fieldD.setAccessible(true);
		    	Field fieldF = EntityTypes.class.getDeclaredField("field_75624_e");
		        fieldF.setAccessible(true);
		    	Field fieldG = EntityTypes.class.getDeclaredField("field_75622_f");
		        fieldG.setAccessible(true);
		        mapC = (Map) fieldC.get(null);
		        mapD = (Map) fieldD.get(null);
		        mapF = (Map) fieldF.get(null);
		        mapG = (Map) fieldG.get(null);
			} catch (Exception e2) {
	    	    if (!FactionMobs.silentErrors) {
	    	    	e1.printStackTrace();
	    	    	e2.printStackTrace();
	    	    }
				return false;
			}
		}
		try {
			navigationE = Navigation.class.getDeclaredField("e"); //TODO: Update name on version change
			navigationE.setAccessible(true);
			goodNavigationE = true;
		} catch (Exception e1) {
			try {
				navigationE = Navigation.class.getDeclaredField("field_75512_e");
				navigationE.setAccessible(true);
				goodNavigationE = true;
			} catch (Exception e2) {
				System.out.println("[FactionMobs] [Minor Error] Field not found: Navigation.e; Custom pathfinding distances cannot be set");
	    	    if (!FactionMobs.silentErrors) {
	    	    	e1.printStackTrace();
	    	    	e2.printStackTrace();
	    	    }
			}
		}
		try {
			pathfinderGoalSelectorB = PathfinderGoalSelector.class.getDeclaredField("b"); //TODO: Update name on version change
			pathfinderGoalSelectorB.setAccessible(true);
			goodPathfinderGoalSelectorB = true;
		} catch (Exception e1) {
			try {
				pathfinderGoalSelectorB = PathfinderGoalSelector.class.getDeclaredField("field_75782_a");
				pathfinderGoalSelectorB.setAccessible(true);
				goodPathfinderGoalSelectorB = true;
			} catch (Exception e2) {
				System.out.println("[FactionMobs] [Minor Error] Field not found: PathfinderGoalSelector.b; Unable to override mob goals");
	    	    if (!FactionMobs.silentErrors) {
	    	    	e1.printStackTrace();
	    	    	e2.printStackTrace();
	    	    }
			}
		}
		try {
            EntityGoalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
		    EntityGoalSelector.setAccessible(true);
		    goodEntitySelectors = true;
		} catch ( Exception e1 ) {
            try {
                EntityGoalSelector = EntityInsentient.class.getDeclaredField("field_70714_bg");
                EntityGoalSelector.setAccessible(true);
                goodEntitySelectors = true;
            } catch (Exception e2) {
                System.out.println("[FactionMobs] [Minor Error] Field not found: EntityInsentient.goalSelector; Unable to override zombie goals");
                if (!FactionMobs.silentErrors) {
                    e1.printStackTrace();
                    e2.printStackTrace();
                }
            }
		}
		return true;
	}
}
