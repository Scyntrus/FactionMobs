package com.gmail.scyntrus.fmob;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.v1_8_R1.EntityInsentient;
import net.minecraft.server.v1_8_R1.EntityTypes;
import net.minecraft.server.v1_8_R1.NavigationAbstract;
import net.minecraft.server.v1_8_R1.PathfinderGoalSelector;

public class ReflectionManager {
	public static Field navigation_Distance = null;
	public static Field pathfinderGoalSelector_GoalList = null;
	public static Field entityInsentient_GoalSelector = null;
	public static Field entityInsentient_GoalTarget = null;
	
	public static boolean good_Navigation_Distance = false;
	public static boolean good_PathfinderGoalSelector_GoalList = false;
	public static boolean good_EntityInsentient_GoalSelector = false;
    public static boolean good_EntityInsentient_GoalTarget = false;

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
			navigation_Distance = NavigationAbstract.class.getDeclaredField("a"); //TODO: Update name on version change
			navigation_Distance.setAccessible(true);
			good_Navigation_Distance = true;
		} catch (Exception e1) {
			try {
				navigation_Distance = NavigationAbstract.class.getDeclaredField("field_75512_e");
				navigation_Distance.setAccessible(true);
				good_Navigation_Distance = true;
			} catch (Exception e2) {
				System.out.println("[FactionMobs] [Minor Error] Field not found: Navigation.e; Custom pathfinding distances cannot be set");
	    	    if (!FactionMobs.silentErrors) {
	    	    	e1.printStackTrace();
	    	    	e2.printStackTrace();
	    	    }
			}
		}
		try {
			pathfinderGoalSelector_GoalList = PathfinderGoalSelector.class.getDeclaredField("b"); //TODO: Update name on version change
			pathfinderGoalSelector_GoalList.setAccessible(true);
			good_PathfinderGoalSelector_GoalList = true;
		} catch (Exception e1) {
			try {
				pathfinderGoalSelector_GoalList = PathfinderGoalSelector.class.getDeclaredField("field_75782_a");
				pathfinderGoalSelector_GoalList.setAccessible(true);
				good_PathfinderGoalSelector_GoalList = true;
			} catch (Exception e2) {
				System.out.println("[FactionMobs] [Minor Error] Field not found: PathfinderGoalSelector.b; Unable to override mob goals");
	    	    if (!FactionMobs.silentErrors) {
	    	    	e1.printStackTrace();
	    	    	e2.printStackTrace();
	    	    }
			}
		}
		try {
            entityInsentient_GoalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
		    entityInsentient_GoalSelector.setAccessible(true);
		    good_EntityInsentient_GoalSelector = true;
		} catch ( Exception e1 ) {
            try {
                entityInsentient_GoalSelector = EntityInsentient.class.getDeclaredField("field_70714_bg");
                entityInsentient_GoalSelector.setAccessible(true);
                good_EntityInsentient_GoalSelector = true;
            } catch (Exception e2) {
                System.out.println("[FactionMobs] [Minor Error] Field not found: EntityInsentient.goalSelector; Unable to override zombie goals");
                if (!FactionMobs.silentErrors) {
                    e1.printStackTrace();
                    e2.printStackTrace();
                }
            }
		}
        try {
            entityInsentient_GoalTarget = EntityInsentient.class.getDeclaredField("goalTarget");
            entityInsentient_GoalTarget.setAccessible(true);
            good_EntityInsentient_GoalTarget = true;
        } catch ( Exception e1 ) {
            try {
                entityInsentient_GoalTarget = EntityInsentient.class.getDeclaredField("field_70714_bg");
                entityInsentient_GoalTarget.setAccessible(true);
                good_EntityInsentient_GoalTarget = true;
            } catch (Exception e2) {
                System.out.println("[FactionMobs] [Minor Error] Field not found: EntityInsentient.goalTarget; Unabled to override target");
                if (!FactionMobs.silentErrors) {
                    e1.printStackTrace();
                    e2.printStackTrace();
                }
            }
        }
		return true;
	}
}
