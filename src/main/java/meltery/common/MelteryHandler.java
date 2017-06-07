package meltery.common;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.shared.TinkerFluids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by tyler on 6/1/17.
 */
public class MelteryHandler {
    public static List<MelteryRecipe> meltingRecipes = new ArrayList<>();

    public static void init() {
        for(Material material: TinkerRegistry.getAllMaterials()) {
            if(material.hasFluid()) {
                registerOredictMelting(material.getFluid(), StringUtils.capitalize(material.identifier));
            }
        }
        registerOredictMelting(TinkerFluids.gold,"Gold");
        registerMelting(Items.CLAY_BALL, TinkerFluids.clay, Material.VALUE_Ingot);
        registerMelting(Blocks.CLAY, TinkerFluids.clay, Material.VALUE_BrickBlock);
    }

    public static void registerOredictMelting(Fluid fluid, String ore) {
        ImmutableSet.Builder<Pair<List<ItemStack>, Integer>> builder = ImmutableSet.builder();
        Pair<List<ItemStack>, Integer> nuggetOre = Pair.of(OreDictionary.getOres("nugget" + ore), Material.VALUE_Nugget);
        Pair<List<ItemStack>, Integer> ingotOre = Pair.of(OreDictionary.getOres("ingot" + ore), Material.VALUE_Ingot);
        Pair<List<ItemStack>, Integer> dustOre = Pair.of(OreDictionary.getOres("dust" + ore), Material.VALUE_Ingot);

        builder.add(nuggetOre, ingotOre, dustOre);
        Set<Pair<List<ItemStack>, Integer>> knownOres = builder.build();
        // register oredicts
        for(Pair<List<ItemStack>, Integer> pair : knownOres) {
            if(!pair.getLeft().isEmpty())
                registerMelting(new MelteryRecipe(RecipeMatch.of(pair.getLeft(), pair.getRight()), fluid));
        }
    }

    /**
     * Registers this item with all its metadatas to melt into amount of the given fluid.
     */
    public static void registerMelting(Item item, Fluid fluid, int amount) {
        ItemStack stack = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
        registerMelting(new MelteryRecipe(new RecipeMatch.Item(stack, 1, amount), fluid));
    }

    /**
     * Registers this block with all its metadatas to melt into amount of the given fluid.
     */
    public static void registerMelting(Block block, Fluid fluid, int amount) {
        ItemStack stack = new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE);
        registerMelting(new MelteryRecipe(new RecipeMatch.Item(stack, 1, amount), fluid));
    }

    /**
     * Registers this itemstack NBT-SENSITIVE to melt into amount of the given fluid.
     */
    public static void registerMelting(ItemStack stack, Fluid fluid, int amount) {
        registerMelting(new MelteryRecipe(new RecipeMatch.ItemCombination(amount, stack), fluid));
    }

    public static void registerMelting(String oredict, Fluid fluid, int amount) {
        registerMelting(new MelteryRecipe(new RecipeMatch.Oredict(oredict, 1, amount), fluid));
    }

    public static void registerMelting(MelteryRecipe recipe) {
        meltingRecipes.add(recipe);
//        FMLLog.info("[Meltery] Registering Melting Recipe %s,%s", recipe.input.getInputs(), recipe.getResult().getUnlocalizedName());
    }

    public static MelteryRecipe getMelteryRecipe(ItemStack stack) {
        for (MelteryRecipe recipe : meltingRecipes) {
            if (recipe.matches(stack)) {
                return recipe;
            }
        }
        return null;
    }


    public static NonNullList<ItemStack> getOreNames(String prefix) {
        NonNullList<ItemStack> list = NonNullList.create();
        list.addAll(Arrays.stream(OreDictionary.getOreNames()).filter(n -> n.startsWith(prefix)).flatMap(n -> OreDictionary.getOres(n).stream()).collect(Collectors.toList()));
        return list;
    }
}
