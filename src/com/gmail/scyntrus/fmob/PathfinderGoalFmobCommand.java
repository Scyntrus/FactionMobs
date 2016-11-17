package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_11_R1.PathfinderGoal;

public class PathfinderGoalFmobCommand extends PathfinderGoal {

    FactionMob mob;

    public PathfinderGoalFmobCommand(FactionMob mob) {
        this.mob = mob;
        this.a(1); //TODO: Update name on version change (setMutexBits)
    }

    @Override
    public boolean a() { //TODO: Update name on version change (shouldExecute)
        return mob.getCommand() != FactionMob.Command.wander;
    }

    @Override
    public boolean b() { //TODO: Update name on version change (continueExecuting)
        return (mob.getCommand() != FactionMob.Command.wander && mob.getEntity().getGoalTarget() == null);
    }

    @Override
    public void c() { //TODO: Update name on version change (startExecuting)
        //do Nothing
    }
}
