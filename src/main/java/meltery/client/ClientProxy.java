package meltery.client;

import meltery.common.CommonProxy;
import meltery.Meltery;
import meltery.common.tile.TileMeltery;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by tyler on 6/1/17.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {

        setModelLocation(Meltery.MELTERY, "inventory");
        ClientRegistry.bindTileEntitySpecialRenderer(TileMeltery.class,new RenderMeltery());
    }
    @SideOnly(Side.CLIENT)
    private static void setModelLocation(Block block, String variantSettings) {
        setModelLocation(Item.getItemFromBlock(block),variantSettings);
    }

    @SideOnly(Side.CLIENT)
    private static void setModelLocation(Item item, String variantSettings) {
        ModelLoader.setCustomMeshDefinition(item, stack -> new ModelResourceLocation(item.getRegistryName().toString(), variantSettings));
    }

}