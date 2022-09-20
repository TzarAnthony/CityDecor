package com.tzaranthony.citydecor.block;

import net.minecraft.world.level.block.TrapDoorBlock;

public class MetalTrapdoor extends TrapDoorBlock {
    public MetalTrapdoor(String name) {
        super(CDBlockProperties.MetalTrapdoor());
        this.setRegistryName(name);
    }
}