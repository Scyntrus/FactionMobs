package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_14_R1.AxisAlignedBB;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.Packet;

public class PathHelpEntity extends Entity {

    public PathHelpEntity() {
        super(EntityTypes.FISHING_BOBBER /* arbitrary benign type */, null);
    }

    public PathHelpEntity set(double x, double y, double z) {
        this.locX = x;
        this.locY = y;
        this.locZ = z;
        return this;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return new AxisAlignedBB(this.locX, this.locY, this.locZ, this.locX, this.locY, this.locZ);
    }

    @Override
    public Packet<?> N() {
        return null;
    }

    @Override
    protected void initDatawatcher() {
    }

    @Override
    protected void a(NBTTagCompound paramNBTTagCompound) {
    }

    @Override
    protected void b(NBTTagCompound paramNBTTagCompound) {
    }
}
