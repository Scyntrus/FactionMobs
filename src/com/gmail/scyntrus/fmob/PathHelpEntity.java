package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_13_R2.AxisAlignedBB;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class PathHelpEntity extends Entity {
    
    public PathHelpEntity() {
        super(null,null);
    }
    
    public PathHelpEntity set(double x, double y, double z) {
        this.locX = x;
        this.locY = y;
        this.locZ = z;
        return this;
    }
    
    @Override
    public AxisAlignedBB getBoundingBox() {
        return new AxisAlignedBB(this.locX,this.locY,this.locZ,this.locX,this.locY,this.locZ);
    }

    @Override
    protected void a(NBTTagCompound paramNBTTagCompound) {
    }

    @Override
    protected void b(NBTTagCompound paramNBTTagCompound) {
    }

    @Override
    protected void x_() {
    }

}
