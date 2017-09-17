package meltery.compat.minetweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.helpers.StackHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import com.google.common.collect.Lists;
import meltery.Meltery;
import meltery.common.MelteryHandler;
import meltery.common.MelteryRecipe;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.util.RecipeMatch;
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

	@ZenMethod
	public static void addFuelBlock(IItemStack block) {
		if (!InputHelper.isABlock(block))
			MineTweakerAPI.logError(block + " Input MUST be a block!");
		ItemStack stack = InputHelper.toStack(block);
		ItemBlock itemBlock = (ItemBlock) stack.getItem();
		IBlockState state = itemBlock.getBlock().getStateFromMeta(stack.getMetadata());
		MineTweakerAPI.apply(new AddFuel(state));
	}

	public static class AddFuel extends BaseListAddition<IBlockState> {

		protected AddFuel(IBlockState input) {
			super("Meltery Fuel", Meltery.FUEL_SOURCE, Lists.newArrayList(input));
		}

		@Override
		protected String getRecipeInfo(IBlockState recipe) {
			return recipe.toString();
		}
	}

	/**********************************************
	 * TConstruct Melting Recipes
	 **********************************************/

	// Adding a Meltery Recipe
	@ZenMethod
	public static void addMelting(ILiquidStack output, IIngredient input, int temp) {
		if (input == null || output == null) {
			LogHelper.logError(String.format("Required parameters missing for %s Recipe.", nameMelting));
			return;
		}

		List<MelteryRecipe> recipes = new LinkedList<>();
		for (IItemStack in : input.getItems()) {
			recipes.add(new MelteryRecipe(new RecipeMatch.ItemCombination(output.getAmount(), toStack(in)), toFluid(output), temp));
		}

		if (!recipes.isEmpty()) {
			MineTweakerAPI.apply(new AddMelting(recipes));
		} else {
			LogHelper.logError(String.format("No %s recipes could be added for input %s.", nameMelting, input.toString()));
		}
	}

	private static class AddMelting extends BaseListAddition<MelteryRecipe> {

		public AddMelting(List<MelteryRecipe> recipes) {
			super(nameMelting, MelteryHandler.meltingRecipes, recipes);
		}

		@Override
		public String getRecipeInfo(MelteryRecipe recipe) {
			return LogHelper.getStackDescription(recipe.input.getInputs());
		}

		@Override
		public String getJEICategory(MelteryRecipe recipe) {
			return "melting";
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Removing a Meltery Recipe
	@ZenMethod
	public static void removeMelting(IItemStack input) {
		List<MelteryRecipe> recipes = new LinkedList<>();

		for (MelteryRecipe meta : MelteryHandler.meltingRecipes) {
			NonNullList<ItemStack> items = NonNullList.create();
			items.addAll(input.getItems().stream().map(InputHelper::toStack).collect(Collectors.toList()));
			if (meta.input.matches(items) != null) {
				recipes.add(meta);
			}
		}

		if (!recipes.isEmpty()) {
			MineTweakerAPI.apply(new RemoveMelting(recipes));
		} else {
			LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", nameMelting, input.toString()));
		}
	}

	private static class RemoveMelting extends BaseListRemoval<MelteryRecipe> {

		public RemoveMelting(List<MelteryRecipe> recipes) {
			super(nameMelting, MelteryHandler.meltingRecipes, recipes);
		}

		@Override
		public String getRecipeInfo(MelteryRecipe recipe) {
			return LogHelper.getStackDescription(recipe.getResult());
		}

		@Override
		public String getJEICategory(MelteryRecipe recipe) {
			return "melting";
		}
	}

}

