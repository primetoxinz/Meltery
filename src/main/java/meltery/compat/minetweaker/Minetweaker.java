package meltery.compat.minetweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import meltery.MelteryHandler;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.plugin.jei.SmeltingRecipeWrapper;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.blamejared.mtlib.helpers.InputHelper.toFluid;
import static com.blamejared.mtlib.helpers.InputHelper.toStack;

/**
 * Created by tyler on 6/1/17.
 */
@ZenClass("meltery.Meltery")
public class Minetweaker {
    public static void init() {
        MineTweakerAPI.registerClass(Minetweaker.class);
    }

    public static final String nameMelting = "Meltery - Melting";

    /**********************************************
     * TConstruct Melting Recipes
     **********************************************/

    // Adding a Meltery Recipe
    @ZenMethod
    public static void addMelting(ILiquidStack output, IIngredient input, int temp) {
        if(input == null || output == null) {
            LogHelper.logError(String.format("Required parameters missing for %s Recipe.", nameMelting));
            return;
        }

        List<MeltingRecipe> recipes = new LinkedList<>();

        for(IItemStack in : input.getItems()) {
            recipes.add(new MeltingRecipe(toStack(in), toFluid(output), temp));
        }

        if(!recipes.isEmpty()) {
            MineTweakerAPI.apply(new AddMelting(recipes));
        } else {
            LogHelper.logError(String.format("No %s recipes could be added for input %s.", nameMelting, input.toString()));
        }
    }

    private static class AddMelting extends BaseListAddition<MeltingRecipe> {

        public AddMelting(List<MeltingRecipe> recipes) {
            super(nameMelting, null, recipes);
        }

        @Override
        public void apply() {
            for(MeltingRecipe recipe : recipes) {
                TinkerRegistry.registerMelting(new slimeknights.tconstruct.library.smeltery.MeltingRecipe(RecipeMatch.of(recipe.input, recipe.fluid.amount), recipe.fluid, recipe.temp));
                successful.add(recipe);
                MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(new slimeknights.tconstruct.library.smeltery.MeltingRecipe(RecipeMatch.of(recipe.input, recipe.fluid.amount), recipe.fluid),getJEICategory(recipe));
            }
        }

        @Override
        public void undo() {
            for(MeltingRecipe recipe : successful) {
                MelteryHandler.meltingRecipes.remove(recipe);
                MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(new slimeknights.tconstruct.library.smeltery.MeltingRecipe(RecipeMatch.of(recipe.input, recipe.fluid.amount), recipe.fluid, recipe.temp), getJEICategory(recipe));
            }
        }

        @Override
        public String getRecipeInfo(MeltingRecipe recipe) {
            return LogHelper.getStackDescription(recipe.input);
        }

        @Override
        public String getJEICategory(MeltingRecipe recipe) {
            return "melting";
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing a Meltery Recipe
    @ZenMethod
    public static void removeMelting(IItemStack input) {
        List<slimeknights.tconstruct.library.smeltery.MeltingRecipe> recipes = new LinkedList<>();

        for(slimeknights.tconstruct.library.smeltery.MeltingRecipe meta : MelteryHandler.meltingRecipes) {
            NonNullList<ItemStack> items = NonNullList.create();
            items.addAll(input.getItems().stream().map(InputHelper::toStack).collect(Collectors.toList()));
            if(meta.input.matches(items) != null) {
                recipes.add(meta);
            }
        }

        if(!recipes.isEmpty()) {
            MineTweakerAPI.apply(new RemoveMelting(recipes));
        } else {
            LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", nameMelting, input.toString()));
        }
    }

    private static class RemoveMelting extends BaseListRemoval<slimeknights.tconstruct.library.smeltery.MeltingRecipe> {

        public RemoveMelting(List<slimeknights.tconstruct.library.smeltery.MeltingRecipe> recipes) {
            super(nameMelting, MelteryHandler.meltingRecipes, recipes);
        }

        @Override
        public void apply() {
            for(slimeknights.tconstruct.library.smeltery.MeltingRecipe recipe : recipes) {
                MelteryHandler.meltingRecipes.remove(recipe);
                successful.add(recipe);
                MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(new SmeltingRecipeWrapper(recipe),getJEICategory(recipe));
            }
        }

        @Override
        public void undo() {
            for(slimeknights.tconstruct.library.smeltery.MeltingRecipe recipe : successful) {
                TinkerRegistry.registerMelting(recipe);
                MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(new SmeltingRecipeWrapper(recipe), getJEICategory(recipe));
            }
        }

        @Override
        public String getRecipeInfo(slimeknights.tconstruct.library.smeltery.MeltingRecipe recipe) {
            return LogHelper.getStackDescription(recipe.getResult());
        }

        @Override
        public String getJEICategory(slimeknights.tconstruct.library.smeltery.MeltingRecipe recipe) {
            return "melting";
        }
    }

    protected static class MeltingRecipe {

        public final ItemStack input;
        public final FluidStack fluid;
        public final int temp;

        public MeltingRecipe(ItemStack input, FluidStack fluid, int temp) {
            this.input = input;
            this.fluid = fluid;
            this.temp = temp;
        }
    }

}

