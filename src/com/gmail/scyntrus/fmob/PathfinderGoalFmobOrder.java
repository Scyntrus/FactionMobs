package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_10_R1.PathfinderGoal;

public class PathfinderGoalFmobOrder extends PathfinderGoal {

    FactionMob mob;

    public PathfinderGoalFmobOrder(FactionMob mob) {
        this.mob = mob;
        this.a(1); //TODO: Update name on version change (setMutexBits)
    }

    @Override
    public boolean a() { //TODO: Update name on version change (shouldExecute)
        return !mob.isWandering();
    }

    @Override
    public boolean b() { //TODO: Update name on version change (continueExecuting)
        return (!mob.isWandering() && mob.getEntity().getGoalTarget() == null);
    }

    @Override
    public void c() { //TODO: Update name on version change (startExecuting)
        //do Nothing
    }
}
