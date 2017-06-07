package meltery.common;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

/**
 * Created by tyler on 6/2/17.
 */
public class MelteryRecipe extends MeltingRecipe {

    public MelteryRecipe(RecipeMatch input, Fluid output) {
        super(input, output);
    }

    public MelteryRecipe(RecipeMatch input, FluidStack output) {
        super(input, output);
    }

    public MelteryRecipe(RecipeMatch input, Fluid output, int temperature) {
        super(input, output, temperature);
    }

    public MelteryRecipe(RecipeMatch input, FluidStack output, int temperature) {
        super(input, output, temperature);
    }
}
