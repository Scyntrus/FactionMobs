package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_13_R2.Chunk;
import net.minecraft.server.v1_13_R2.EntityPositionTypes;
import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.HeightMap;
import net.minecraft.server.v1_13_R2.PathfinderGoalSelector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionManager {
    public static Field pathfinderGoalSelector_GoalSet = null;
    public static Field chunk_EntitySlices = null;
    public static Method entityPositionTypes_a = null;

    public static boolean good_PathfinderGoalSelector_GoalSet = false;

    public static boolean init() {
        try {
            chunk_EntitySlices = Chunk.class.getDeclaredField("entitySlices");
            entityPositionTypes_a = EntityPositionTypes.class.getDeclaredMethod("a", //TODO: Update name on version change
                    EntityTypes.class, EntityPositionTypes.Surface.class, HeightMap.Type.class);
            entityPositionTypes_a.setAccessible(true);
        } catch (Exception e1) {
            ErrorManager.handleError(e1);
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
        return true;
    }
}
