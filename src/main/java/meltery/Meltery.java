package meltery;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import meltery.common.block.BlockMeltery;
import meltery.common.CommonProxy;
import meltery.common.MelteryHandler;
import meltery.common.tile.TileMeltery;
import meltery.compat.minetweaker.Minetweaker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;
import java.util.Set;

/**
 * Created by tyler on 6/1/17.
 */
@Mod(modid = Meltery.MODID, name = Meltery.NAME, version = Meltery.VERSION, dependencies = Meltery.DEPENDENCIES)
public class Meltery {
    public static final String MODID = "meltery";
    public static final String NAME = "Meltery";
    public static final String VERSION = "1.0.0";
    public static final String DEPENDENCIES = "required-after:tconstruct;after:MineTweaker3";


    public static List<IBlockState> FUEL_SOURCE = Lists.newArrayList();

    public static Block MELTERY = new BlockMeltery();

    @SidedProxy(clientSide = "meltery.client.ClientProxy", serverSide = "meltery.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        registerBlock(MELTERY);
        GameRegistry.registerTileEntity(TileMeltery.class, "tile.meltery");
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MELTERY), "I I", "I I", "BBB", 'I', "ingotIron", 'B', Blocks.BRICK_BLOCK));
        proxy.preInit(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        MelteryHandler.init();
        if (Loader.isModLoaded("crafttweaker")) {
            Minetweaker.init();
        }

    }

    public void registerBlock(Block block) {
        block.setUnlocalizedName(block.getRegistryName().getResourcePath());
        GameRegistry.register(block);
        GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }
}
