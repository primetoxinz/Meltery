package meltery.client;

import meltery.common.tile.TileMeltery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;

import javax.annotation.Nonnull;

/**
 * Created by primetoxinz on 6/6/17.
 */
public class RenderMeltery extends TileEntitySpecialRenderer<TileMeltery> {

    protected static Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void renderTileEntityAt(@Nonnull TileMeltery tile, double x, double y, double z, float partialTicks, int destroyStage) {
        FluidTankAnimated tank = tile.getInternalTank();
        FluidStack liquid = tank.getFluid();
        if (liquid != null) {
            float height = ((float) liquid.amount - tank.renderOffset) / (float) tank.getCapacity();
            if (tank.renderOffset > 1.2f || tank.renderOffset < -1.2f) {
                tank.renderOffset -= (tank.renderOffset / 12f + 0.1f) * partialTicks;
            } else {
                tank.renderOffset = 0;
            }
            float d = RenderUtil.FLUID_OFFSET;

            RenderUtil.renderFluidCuboid(liquid, tile.getPos(), x, y+1/16d, z, d, d, d, 1d - d, Math.max(0,(14/16d)*height), 1d - d);
        }
        renderItem(x,y,z,tile);
    }


    public void renderItem(double x, double y, double z, TileMeltery meltery) {
        // calculate x/z parameters
        double x1 = meltery.getPos().getX();
        double y1 = meltery.getPos().getY();
        double z1 = meltery.getPos().getZ();

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderUtil.pre(x, y, z);
        GlStateManager.translate(0.5,1/16d,0.5);
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        RenderHelper.enableStandardItemLighting();

        int brightness = meltery.getWorld().getCombinedLight(meltery.getPos(), 0);

        ItemStack stack = meltery.inventory.getStackInSlot(0);
        boolean isItem = !(stack.getItem() instanceof ItemBlock);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (brightness % 0x10000) / 1f,
                (float) (brightness / 0x10000) / 1f);
        if (isItem) {
            GlStateManager.rotate(-90, 1, 0, 0);
        } else {
            GlStateManager.scale(0.5,0.5,0.5);
        }
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, meltery.getWorld(), null);
        model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.NONE, false);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
        if (isItem) {
            GlStateManager.rotate(90, 1, 0, 0);
        }
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableCull();
        RenderUtil.post();
    }
}

