package meltery.compat.jei;

import com.google.common.collect.ImmutableList;

import meltery.Meltery;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;

public class SmeltingRecipeCategory implements IRecipeCategory<SmeltingRecipeWrapper> {

    public static ResourceLocation background_loc =  new ResourceLocation("meltery","textures/gui/meltery.png");

    private final IDrawable background;

    public SmeltingRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(background_loc, 0, 0, 53, 45, 0, 0, 0, 0);
    }

    @Nonnull
    @Override
    public String getUid() {
        return JEI.MELTING_UID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return Util.translate("gui.jei.meltery.title");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {

    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SmeltingRecipeWrapper recipe, IIngredients ingredients) {
        IGuiItemStackGroup items = recipeLayout.getItemStacks();
        items.init(0, true, 6, 3);

        items.set(ingredients);

        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
        fluids.init(0, false, 31, 3, 18, 38, Material.VALUE_Block, false, null);
        fluids.set(ingredients);

        items.init(2, false, 6, 23);
        items.set(2, new ItemStack(Meltery.MELTERY));

    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return ImmutableList.of();
    }

    @Override
    public IDrawable getIcon() {
        // use the default icon
        return null;
    }
}