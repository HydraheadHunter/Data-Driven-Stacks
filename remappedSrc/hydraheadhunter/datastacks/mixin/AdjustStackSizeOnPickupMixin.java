package hydraheadhunter.datastacks.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static hydraheadhunter.datastacks.util.common.createDummyStack;

/** The purpose of this class is adjust the max sourceStack size of items as they are picked up
 * Taking the configured max sourceStack size of that item in that entities inventory into account,
 * so that when taking from an overly-large sourceStack, items are split appropriately into multiple slots.
 * EG: If wheat_seeds are configured to sourceStack to 128 in general, but only to 8 in a villager inventory (8 slots),
 * Throwing a full sourceStack of 256 wheat_seeds at the villagers feet will result in the villager picking up
 * 8 stacks of 8 wheat_seeds (64 seeds in total) and leaving the rest on the ground.
 * This is the class responsible for that behavior, in any InventoryOwner class entity.
 */
@Mixin(InventoryOwner.class)
public interface AdjustStackSizeOnPickupMixin {
	
	
	/** Redirects the call of canInsert(ItemStack) to canInsertDummy(...),
	 * which takes a dummy version of the sourceStack as an argument to
	 * ensure the entity checking if it can pick something up is
	 * using the correct max sourceStack size.
	 */ @Redirect(method="pickUpItem", at= @At(value = "INVOKE", target = "Lnet/minecraft/inventory/SimpleInventory;canInsert(Lnet/minecraft/item/ItemStack;)Z"))
	private static boolean redirectCanInsert(SimpleInventory inventory, ItemStack stack, @Local(argsOnly = true) MobEntity entity){
		return canInsertDummy( entity, inventory, createDummyStack(stack, entity) );
	}
	
	/** Redirects the call of addStack(ItemStack) through some logic.
	 * Checks if a dummyStack in the target inventory has the same max sourceStack size as the itemEntity being picked up.
	 * If yes, returns to the vanilla behavior.
	 * If no, calls a recursive function to add stacks one target's max sourceStack size at a time.
	 */	@Redirect(method="pickUpItem", at= @At(value = "INVOKE", target = "Lnet/minecraft/inventory/SimpleInventory;addStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
	private static ItemStack checkStackSizes_IfDifferent_Recurse(SimpleInventory inventory, ItemStack stack, @Local(argsOnly = true) MobEntity entity){
		ItemStack dummyStack= createDummyStack(stack, entity);
		int dummyMax= dummyStack.getMaxCount();
		int stackMax= stack.getMaxCount();
		
		return dummyMax==stackMax ? inventory.addStack(stack) : recurse(entity, inventory,stack, dummyStack, 0);
	}
	
	/** Recursive logic to adding a sourceStack to an inventory where dummyMax != stackMax.
	 * The smaller of the dummyMax and sourceStack.count gets assigned to count to add.
	 * A sourceStack of that size attempts to add itself to the inventory.
	 * Anything that was successfully added gets decremented from the source sourceStack.
	 * If the source stack has been emptied or the inventory has been filled, the sourceStack is returned,
	 * Otherwise, the function recurses.
	 */	@Unique
	private static ItemStack recurse(MobEntity entity, SimpleInventory inventory, ItemStack sourceStack, ItemStack dummyStack, int recursionCount){
		//Use a dummy sourceStack to determine the max_stack_size for item if it were in the target inventory
		int countToAdd= Math.min( sourceStack.getCount(), dummyStack.getMaxCount() );
		//Create a full sourceStack of that size belonging to that entity.
		ItemStack stackToAdd   = sourceStack.copyWithCount(countToAdd);
		stackToAdd.setHolder(entity);
		stackToAdd.getMaxCount();
		
		//Attempt to add it to the inventory
		ItemStack stackReturned= inventory.addStack(stackToAdd);
		
		// Adjust the source sourceStack by the amount successfully added to the inventory.
		sourceStack.decrement(countToAdd - stackReturned.getCount());
		
		//Check if the inventory is now full.
		boolean canInsert  = canInsertDummy(entity, inventory, dummyStack);

		//If satisfied (inventory full, sourceStack empty, or recursed 100 times) return. Otherwise recurse.
		return ( !canInsert || sourceStack.isEmpty() || recursionCount>100 ) ? sourceStack : recurse(entity, inventory, sourceStack, dummyStack, recursionCount+1);
	}
	
	/** Next to Identical to vanilla canInsert(ItemStack) except it assigns each stack it checks it's holder through setHolder(entity).
	 * */ @Unique
	private static boolean canInsertDummy(MobEntity entity, SimpleInventory inventory, ItemStack dummyStack){
		boolean toReturn= false;
		for( ItemStack stack: inventory.heldStacks){
			stack.setHolder(entity);
			if ( stack.isEmpty()) { toReturn=true; break;}
			if ( ItemStack.areItemsAndComponentsEqual(stack, dummyStack) && stack.getCount()< stack.getMaxCount() ) {
				toReturn = true;
				break;
			}
		}
		return toReturn;
	}
	
	
}
