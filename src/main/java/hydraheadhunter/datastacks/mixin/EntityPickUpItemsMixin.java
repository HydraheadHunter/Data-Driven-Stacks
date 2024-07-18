package hydraheadhunter.datastacks.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static hydraheadhunter.datastacks.util.common.createDummyStack;


@Mixin(InventoryOwner.class)
public interface EntityPickUpItemsMixin {
	//This class injects into the inventory owner class where the owner picks up items.
	//It makes it so that picking up 'overstacked items' gets processed in smaller batches.
	
	//Redirects the CanInsert to a mixin version of the method that checks a dummy item with the correct max_stack_size.
	@Redirect(method="pickUpItem", at= @At(value = "INVOKE", target = "Lnet/minecraft/inventory/SimpleInventory;canInsert(Lnet/minecraft/item/ItemStack;)Z"))
	private static boolean redirectCanInsert(SimpleInventory inventory, ItemStack stack, @Local MobEntity entity){
		return canInsert( entity, inventory, createDummyStack(stack, entity) );
	}
	
	//When an item would be picked up, this checks if the picked up stack and a dummystack 'in the target inventory' would have the same max stack size
	// If the do, it adds the stack normally
	// If they don't, it hands functionality over to it recurses.
	@Redirect(method="pickUpItem", at= @At(value = "INVOKE", target = "Lnet/minecraft/inventory/SimpleInventory;addStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
	private static ItemStack checkStackSizes_IfDifferent_Recurse(SimpleInventory inventory, ItemStack stack, @Local MobEntity entity){
		ItemStack dummyStack= createDummyStack(stack, entity);
		int dummyMax= dummyStack.getMaxCount();
		int stackMax= stack.getMaxCount();
		
		return dummyMax==stackMax ? inventory.addStack(stack) : recurse(entity, inventory,stack, 0);
	}
	
	//Uses a dummystack to determine the max stack size of the item in the target inventory.
	//  Takes one target inventory max_stack_size-worth from the source stack and adds it to the target inventory. Then recurses.
	//  Returns once the inventory has become full, the source stack has become empty, or recursed 100 times times.
	private static ItemStack recurse(MobEntity entity, SimpleInventory inventory, ItemStack stack, int recursionCount){
		//Use a dummy stack to determine the max_stack_size for item if it were in the target inventory
		ItemStack dummyStack= createDummyStack(stack, entity);
		int countToAdd= dummyStack.getMaxCount();
		
		//Create a full stack of that size belonging to that entity.
		ItemStack stackToAdd   = stack.copyWithCount(countToAdd);
		stackToAdd.setHolder(entity);
		stackToAdd.getMaxCount();
		
		//Attempt to add it to the inventory
		ItemStack stackReturned= inventory.addStack(stackToAdd);
		
		// Adjust the source stack by the amount successfully added to the inventory.
		stack.decrement(countToAdd - stackReturned.getCount());
		
		//Check if the inventory is now full.
		boolean canInsert  = canInsert(entity, inventory, dummyStack);

		//If satisfied (inventory full, stack empty, or recursed 100 times) return. Otherwise recurse.
		return ( !canInsert || stack.isEmpty() || recursionCount>100 ) ? stack: recurse(entity, inventory, stack, recursionCount+1);
	}
	
	//Creates a copy of the stack except its' holder is the entity argument, it's count is one, and it's max stack size is updated.

	private static boolean canInsert(MobEntity entity, SimpleInventory inventory, ItemStack sourceStack){
		boolean toReturn= false;
		for( ItemStack stack: inventory.heldStacks){
			stack.setHolder(entity);
			if ( stack.isEmpty()) { toReturn=true; break;}
			if ( ItemStack.areItemsAndComponentsEqual(stack, sourceStack) && stack.getCount()< stack.getMaxCount() ) {
				toReturn = true;
				break;
			}
		}
		return toReturn;
	}
	
	
}
