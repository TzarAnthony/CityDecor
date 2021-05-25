//package com.tzaranthony.modernvanilla.tileentity;
//
//import com.tzaranthony.modernvanilla.block.Chair;
//import net.minecraft.item.DyeColor;
//import net.minecraft.network.play.server.SUpdateTileEntityPacket;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.tileentity.TileEntityType;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//public class ChairTileEntity extends TileEntity {
//    private DyeColor color;
//
//    public ChairTileEntity() {
//        super(TileEntityType.Couch);
//    }
//
//    public ChairTileEntity(DyeColor colorIn) {
//        this();
//        this.setColor(colorIn);
//    }
//
//    /**
//     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
//     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
//     */
//    public SUpdateTileEntityPacket getUpdatePacket() {
//        return new SUpdateTileEntityPacket(this.pos, 11, this.getUpdateTag());
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public DyeColor getColor() {
//        if (this.color == null) {
//            this.color = ((Chair) this.getBlockState().getBlock()).getColor();
//        }
//
//        return this.color;
//    }
//
//    public void setColor(DyeColor color) {
//        this.color = color;
//    }
//}
