package meltery;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

/**
 * Created by tyler on 6/1/17.
 */
public class BlockMeltery extends BlockDirectional {

    public static final PropertyBool ENABLED = PropertyBool.create("enabled");
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockMeltery() {
        super(Material.ROCK);
        setCreativeTab(CreativeTabs.MISC);
        setRegistryName("meltery");
        setUnlocalizedName("meltery");
        setHardness(1.5F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe",1);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileMeltery();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ENABLED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();

    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, placer.getHorizontalFacing());
    }


    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TileMeltery) {
            ((TileMeltery) worldIn.getTileEntity(pos)).onBreak();
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }


    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te == null) {
            return super.getActualState(state, worldIn, pos);
        }
        TileMeltery meltery = (TileMeltery) te;
        return super.getActualState(state, worldIn, pos).withProperty(ENABLED, meltery.isRunning());
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileMeltery te = (TileMeltery) worldIn.getTileEntity(pos);
        if (te == null || !te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing) || !te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)) {
            return false;
        }

        ItemStack heldItem = playerIn.getHeldItem(hand);
        IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
        IItemHandler playerInventory = playerIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
        if (MelteryHandler.hasMeltingRecipe(heldItem)) {
            ItemStack insert = heldItem.copy();
            insert.setCount(1);
            boolean isEmpty = itemHandler.getStackInSlot(0).isEmpty();
            ItemStack result = ItemHandlerHelper.insertItem(itemHandler, insert, false);
            if (result.getCount() < heldItem.getCount()) {
                worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0f, 1.0f);
                heldItem.shrink(1);
                if(isEmpty)
                    te.setProgress(0);
                return true;
            }
        } else if (playerIn.isSneaking()) {
            ItemStack stack = itemHandler.extractItem(0, 1, false);
            if (playerInventory.insertItem(playerIn.inventory.currentItem, stack.copy(), false).isEmpty()) {
                worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_ADD_ITEM, SoundCategory.BLOCKS, 0.75f, 1.0f);
                te.setProgress(0);
                return true;
            }
            return false;
        }
        IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);

        FluidActionResult result = FluidUtil.interactWithFluidHandler(heldItem, fluidHandler, playerIn);
        if (result.isSuccess()) {
            playerIn.setHeldItem(hand, result.getResult());
            return true; // return true as we did something
        }
        // prevent interaction so stuff like buckets and other things don't place the liquid block
        return FluidUtil.getFluidHandler(heldItem) != null;
    }
}
