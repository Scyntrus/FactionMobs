package com.gmail.scyntrus.fmob;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import net.minecraft.server.v1_11_R1.Chunk;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityTypes;
import net.minecraft.server.v1_11_R1.PathfinderGoalSelector;

public class ReflectionManager {
    public static Field pathfinderGoalSelector_GoalSet = null;
    public static Field chunk_EntitySlices = null;

    public static boolean good_PathfinderGoalSelector_GoalSet = false;

    public static boolean init() {
        if (!CustomEntityRegistry.init()) {
            return false;
        }
        try {
            pathfinderGoalSelector_GoalSet = PathfinderGoalSelector.class.getDeclaredField("b"); //TODO: Update name on version change (goal set: taskEntries)
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
