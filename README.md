#Meltery
Tinker's Construct addon that adds a block that can melt ingots,dusts and nuggets for use with casts.

This Block has no GUI; It is instead entirely world interaction.
To use the Block, it must first have lava under it to fuel it, then click or insert with a hopper (or other item transport).
This item will then be melted over time and can be extracted with any fluid pipe or faucet.
The Meltery also gives audio cues once it melts an item, or if it cannot because the tank is full.


recipes can be added to the Meltery via Minetweaker like so
``` meltery.Meltery.addMelting(ILiquidStack output, IIngredient input, int temp)```
or remove with
``` meltery.Meltery.removeMelting(IItemStack input) ```