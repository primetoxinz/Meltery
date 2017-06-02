package meltery;

import meltery.compat.minetweaker.Minetweaker;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by tyler on 6/1/17.
 */
@Mod(modid = Meltery.MODID, name = Meltery.NAME, version = Meltery.VERSION, dependencies = Meltery.DEPENDENCIES)
public class Meltery {
    public static final String MODID = "meltery";
    public static final String NAME = "Meltery";
    public static final String VERSION = "1.0.0";
    public static final String DEPENDENCIES = "required-after:tconstruct";

    public static Block MELTERY = new BlockMeltery();

    @SidedProxy(clientSide = "meltery.ClientProxy", serverSide = "meltery.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        GameRegistry.register(MELTERY);
        GameRegistry.register(new ItemBlock(MELTERY).setRegistryName(MELTERY.getRegistryName()));
        GameRegistry.registerTileEntity(TileMeltery.class,"tile.meltery");
        GameRegistry.addShapedRecipe(new ItemStack(MELTERY), "BBB","B B","BBB", 'B', Blocks.BRICK_BLOCK);
        proxy.preInit(e);
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {

        MelteryHandler.init();
        Minetweaker.init();

    }



}
