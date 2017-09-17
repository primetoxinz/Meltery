package meltery.common.tile;

import meltery.Meltery;
import meltery.Utils;
import meltery.common.MelteryHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import javax.annotation.Nonnull;

/**
 * Created by tyler on 6/1/17.
 */
public class TileMeltery extends TileEntity implements ITickable {


    public FluidTankAnimated tank;
    public SimpleStackHandler inventory;
    private int progress;
    public static int MAX_FLUID = 9000;

    public TileMeltery() {
        this.inventory = new SimpleStackHandler(1, this);
        this.tank = new FluidTankAnimated(MAX_FLUID,this);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) || (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        } else {
            return super.getCapability(capability, facing);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.merge(inventory.serializeNBT());
        tank.writeToNBT(tag);
        tag.setInteger("progress", progress);
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (inventory == null)
            inventory = new SimpleStackHandler(1, this);
        if (tank == null)
            tank = new FluidTankAnimated(MAX_FLUID,this);
        tank.readFromNBT(compound);
        inventory.deserializeNBT(compound);
        progress = compound.getInteger("progress");
        super.readFromNBT(compound);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        writeToNBT(new NBTTagCompound());
    }

    public void onBreak() {
        IItemHandler inv = getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inv != null) {
            for (int i = 0; i < inv.getSlots(); i++) {
                Utils.ejectInventoryContents(world, pos, inv);
            }
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        this.readFromNBT(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void update() {
        if (hasFuel()) {
            ItemStack melt = inventory.getStackInSlot(0);
            MeltingRecipe recipe = MelteryHandler.getMelteryRecipe(melt);

            if (recipe != null) {
                if (progress > recipe.getUsableTemperature()) {
                    FluidStack fluidStack = recipe.getResult();
                    if ((tank.getCapacity() - tank.getFluidAmount()) >= tank.fill(fluidStack, false)) {
                        tank.fill(fluidStack, true);
                        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.BLOCKS, 1.0f, 0.75f);
                        melt.shrink(1);
                    } else {
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 0.75f);
                    }
                    setProgress(0);
                }
                incrementProcress();
            }
        }
        if(tank.getFluidAmount() > 0) {
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                BlockPos side = pos.offset(facing);
                if (!world.isAirBlock(side)) {
                    if (world.getTileEntity(side) != null) {
                        TileEntity tile = world.getTileEntity(side);
                        if (!(tile instanceof TileMeltery) && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
                            IFluidHandler fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
                            if (fluidHandler instanceof IFluidTank) {
                                FluidUtil.tryFluidTransfer(fluidHandler, tank, 140, true);
                            }
                        }

                    }
                }
            }
        }
    }


    public void setProgress(int progress) {
        this.progress = progress;
        world.markBlockRangeForRenderUpdate(pos,pos);
    }

    public void incrementProcress() {
        setProgress(progress + 1);
    }

    public boolean hasFuel() {
	    IBlockState state = world.getBlockState(pos.down());
        return Meltery.FUEL_SOURCE.contains(state) || state.getMaterial() == Material.LAVA;
    }

    public boolean isRunning() {
        return progress > 1;
    }

    public FluidTankAnimated getInternalTank() {
        return tank;
    }

    public class SimpleStackHandler extends ItemStackHandler {
        private TileMeltery tile;

        public SimpleStackHandler(int size, TileMeltery tile) {
            super(size);
            this.tile = tile;
        }

        @Override
        public void onContentsChanged(int slot) {
            tile.markDirty();
        }

        @Override
        public String toString() {
            return stacks.toString();
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 64;
        }

        public boolean isEmpty() {
            return getStackInSlot(0).isEmpty();
        }
        public boolean isFull() {
            ItemStack stack = getStackInSlot(0);
            return stack.getCount() == stack.getMaxStackSize();
        }

        public boolean canInput(ItemStack stack) {
            ItemStack slot = getStackInSlot(0);
            if(slot.isItemEqual(stack))
                return !isFull();
            return isEmpty();
        }
    }
}
