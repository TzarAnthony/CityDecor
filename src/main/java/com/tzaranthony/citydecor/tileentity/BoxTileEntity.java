package com.tzaranthony.citydecor.tileentity;

import com.tzaranthony.citydecor.block.CardboardBox;
import com.tzaranthony.citydecor.util.CDTileEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class BoxTileEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    public static final int COLUMNS = 9;
    public static final int ROWS = 3;
    public static final int CONTAINER_SIZE = 27;
    public static final String ITEMS_TAG = "Items";
    private static final int[] SLOTS = IntStream.range(0, 27).toArray();
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
    private int openCount;

    public BoxTileEntity(BlockPos pos, BlockState state) {
        super(CDTileEntityType.BOX.get(), pos, state);
    }

    public int getContainerSize() {
        return this.itemStacks.size();
    }

    public boolean triggerEvent(int p_145842_1_, int p_145842_2_) {
        if (p_145842_1_ == 1) {
            this.openCount = p_145842_2_;
            if (p_145842_2_ == 0) {
                this.doNeighborUpdates();
            }

            if (p_145842_2_ == 1) {
                this.doNeighborUpdates();
            }

            return true;
        } else {
            return super.triggerEvent(p_145842_1_, p_145842_2_);
        }
    }

    private void doNeighborUpdates() {
        this.getBlockState().updateNeighbourShapes(this.getLevel(), this.getBlockPos(), 3);
    }

    public void startOpen(Player p_59692_) {
        if (!p_59692_.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount == 1) {
                this.level.gameEvent(p_59692_, GameEvent.CONTAINER_OPEN, this.worldPosition);
                this.level.playSound((Player)null, this.worldPosition, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    public void stopOpen(Player p_59688_) {
        if (!p_59688_.isSpectator()) {
            --this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount <= 0) {
                this.level.gameEvent(p_59688_, GameEvent.CONTAINER_CLOSE, this.worldPosition);
                this.level.playSound((Player)null, this.worldPosition, SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    protected Component getDefaultName() {
        return new TranslatableComponent("Cardboard Box");
    }

    public void load(CompoundTag p_155678_) {
        super.load(p_155678_);
        this.loadFromTag(p_155678_);
    }

    protected void saveAdditional(CompoundTag p_187513_) {
        super.saveAdditional(p_187513_);
        if (!this.trySaveLootTable(p_187513_)) {
            ContainerHelper.saveAllItems(p_187513_, this.itemStacks, false);
        }
    }

    public void loadFromTag(CompoundTag p_59694_) {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(p_59694_) && p_59694_.contains("Items", 9)) {
            ContainerHelper.loadAllItems(p_59694_, this.itemStacks);
        }
    }

    protected NonNullList<ItemStack> getItems() {
        return this.itemStacks;
    }

    protected void setItems(NonNullList<ItemStack> p_199721_1_) {
        this.itemStacks = p_199721_1_;
    }

    public int[] getSlotsForFace(Direction p_180463_1_) {
        return SLOTS;
    }

    public boolean canPlaceItemThroughFace(int p_59663_, ItemStack p_59664_, @Nullable Direction p_59665_) {
        return !(Block.byItem(p_59664_.getItem()) instanceof CardboardBox) || !(Block.byItem(p_59664_.getItem()) instanceof ShulkerBoxBlock);
    }

    public boolean canTakeItemThroughFace(int p_59682_, ItemStack p_59683_, Direction p_59684_) {
        return true;
    }

    protected AbstractContainerMenu createMenu(int p_213906_1_, Inventory p_213906_2_) {
        return new ShulkerBoxMenu(p_213906_1_, p_213906_2_, this);
    }
}