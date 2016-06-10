package com.gmail.scyntrus.fmob;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.v1_10_R1.Chunk;
import net.minecraft.server.v1_10_R1.Entity;
import net.minecraft.server.v1_10_R1.EntityTypes;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;

public class ReflectionManager {
    public static Field pathfinderGoalSelector_GoalSet = null;
    public static Field chunk_EntitySlices = null;

    public static boolean good_PathfinderGoalSelector_GoalSet = false;

    public static Map<String, Class<? extends Entity>> mapC;
    public static Map<Class<? extends Entity>, String> mapD;
    public static Map<Class<? extends Entity>, Integer> mapF;
    public static Map<String, Integer> mapG;

    public static boolean init() {
        {
            Field fieldC = null;
            Field fieldD = null;
            Field fieldF = null;
            Field fieldG = null;
            try {
                fieldC = EntityTypes.class.getDeclaredField("c"); //TODO: Update name on version change (map 1)
                fieldD = EntityTypes.class.getDeclaredField("d"); //TODO: Update name on version change (map 2)
                fieldF = EntityTypes.class.getDeclaredField("f"); //TODO: Update name on version change (map 4)
                fieldG = EntityTypes.class.getDeclaredField("g"); //TODO: Update name on version change (map 5)
            } catch (Exception e1) {
                try {
                    fieldC = EntityTypes.class.getDeclaredField("field_75625_b");
                    fieldD = EntityTypes.class.getDeclaredField("field_75626_c");
                    fieldF = EntityTypes.class.getDeclaredField("field_75624_e");
                    fieldG = EntityTypes.class.getDeclaredField("field_75622_f");
                } catch (Exception e2) {
                    ErrorManager.handleError("Failed to reflect entity registration methods.");
                    ErrorManager.handleError(e1);
                    ErrorManager.handleError(e2);
                    return false;
                }
            }
            try {
                fieldC.setAccessible(true);
                fieldD.setAccessible(true);
                fieldF.setAccessible(true);
                fieldG.setAccessible(true);
                @SuppressWarnings("unchecked")
                Map<String, Class<? extends Entity>> tempMap1 = (Map<String, Class<? extends Entity>>) fieldC.get(null);
                mapC = tempMap1;
                @SuppressWarnings("unchecked")
                Map<Class<? extends Entity>, String> tempMap2 = (Map<Class<? extends Entity>, String>) fieldD.get(null);
                mapD = tempMap2;
                @SuppressWarnings("unchecked")
                Map<Class<? extends Entity>, Integer> tempMap3 = (Map<Class<? extends Entity>, Integer>) fieldF.get(null);
                mapF = tempMap3;
                @SuppressWarnings("unchecked")
                Map<String, Integer> tempMap4 = (Map<String, Integer>) fieldG.get(null);
                mapG = tempMap4;
            } catch (Exception e) {
                ErrorManager.handleError(e);
                return false;
            }
        }
        try {
            pathfinderGoalSelector_GoalSet = PathfinderGoalSelector.class.getDeclaredField("b"); //TODO: Update name on version change (goal set)
            pathfinderGoalSelector_GoalSet.setAccessible(true);
            good_PathfinderGoalSelector_GoalSet = true;
        } catch (Exception e1) {
            try {
                pathfinderGoalSelector_GoalSet = PathfinderGoalSelector.class.getDeclaredField("field_75782_a");
                pathfinderGoalSelector_GoalSet.setAccessible(true);
                good_PathfinderGoalSelector_GoalSet = true;
            } catch (Exception e2) {
                ErrorManager.handleError("[Minor Error] Field not found: PathfinderGoalSelector.b; Unable to override mob goals");
                ErrorManager.handleError(e1);
                ErrorManager.handleError(e2);
            }
        }
        try {
            chunk_EntitySlices = Chunk.class.getDeclaredField("entitySlices");
        } catch (Exception e1) {
            ErrorManager.handleError(e1);
            return false;
        }
        return true;
    }
}
