package meltery.compat.jei;

import meltery.Meltery;
import meltery.MelteryHandler;
import meltery.MelteryRecipe;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;

/**
 * Created by tyler on 6/1/17.
 */
@JEIPlugin
public class JEI extends BlankModPlugin {
    public static final String MELTING_UID = "melting";
    private static IGuiHelper helper;
    @Override
    public void register(IModRegistry registry) {
        if(helper == null)
            helper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new SmeltingRecipeCategory(helper));
        registry.handleRecipes(MelteryRecipe.class, SmeltingRecipeWrapper::new, MELTING_UID);
        registry.addRecipes(MelteryHandler.meltingRecipes, MELTING_UID);
        registry.addRecipeCategoryCraftingItem(new ItemStack(Meltery.MELTERY),MELTING_UID);
    }
}
