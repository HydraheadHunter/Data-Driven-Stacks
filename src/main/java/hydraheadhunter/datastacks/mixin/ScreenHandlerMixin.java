package hydraheadhunter.datastacks.mixin;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static hydraheadhunter.datastacks.util.common.createDummyStack;
import static java.lang.String.valueOf;


@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
	/*@Shadow DefaultedList<Slot> slots;
	@Shadow @Final private static Logger LOGGER;
	
	private static final String PLAYER_INVENTORY_STRING 		= "class net.minecraft.entity.player.PlayerInventory"			;
	private static final String CRAFTING_INVENTORY        		= "class net.minecraft.inventory.CraftingInventory"			;
	private static final String CRAFTING_RESULT_INVENTORY 		= "class net.minecraft.inventory.CraftingResultInventory"		;
	private static final String DOUBLE_INVENTORY		 		= "class net.minecraft.inventory.DoubleInventory"				;
	private static final String CHEST_BLOCK_INVENTORY 		= "class net.minecraft.block.entity.ChestBlockEntity"			;
	private static final String DISPENSOR_INVENTORY 			= "class net.minecraft.block.entity.DispenserBlockEntity"		;
	private static final String DROPPER_INVENTORY 			= "class net.minecraft.block.entity.DropperBlockEntity"		;
	private static final String CRAFTER_INVENTORY 			= "class net.minecraft.block.entity.CrafterBlockEntity"		;
	private static final String HOPPER_INVENTORY 			= "class net.minecraft.block.entity.HopperBlockEntity"			;
	private static final String MC_HOPPER_INVENTORY 			= "class net.minecraft.entity.vehicle.HopperMinecartEntity"		;
	private static final String MC_CHEST_INVENTORY 			= "class net.minecraft.entity.vehicle.ChestMinecartEntity"		;
	private static final String CARTOGRAPHY_INVENTORY 		= "class net.minecraft.screen.CartographyTableScreenHandler"	;
	private static final String CHEST_BOAT_INVENTORY 			= "class net.minecraft.entity.vehicle.ChestBoatEntity"			;
	private static final String LOOM_INVENTORY 				= "class net.minecraft.screen.LoomScreenHandler"				;
	private static final String GRINDSTONE_INVENTORY 			= "class net.minecraft.screen.GrindstoneScreenHandler"			;
	private static final String SMITHING_TABLE_INVENTORY 		= "class net.minecraft.screen.ForgingScreenHandler"			;
	private static final String SHULKER_INVENTORY 			= "class net.minecraft.block.entity.ShulkerBoxBlockEntity"		;
	private static final String ENDERCHEST_INVENTORY 			= "class net.minecraft.inventory.EnderChestInventory"			;
	private static final String FURNACE_INVENTORY 			= "class net.minecraft.block.entity.FurnaceBlockEntity"		;
	private static final String BLAST_FURNACE_INVENTORY		= "class net.minecraft.block.entity.BlastFurnaceBlockEntity"	;
	private static final String SMOKER_INVENTORY 			= "class net.minecraft.block.entity.SmokerBlockEntity"			;
	private static final String BREWING_STAND_INVENTORY 		= "class net.minecraft.block.entity.BrewingStandBlockEntity"	;
	private static final String BEACON_INVENTORY 			= "class net.minecraft.screen.BeaconScreenHandler"			;
	private static final String STONECUTTER_INVENTORY 		= "class net.minecraft.screen.StonecutterScreenHandler"		;
	private static final String LECTERN_INVENTORY 			= "class net.minecraft.block.entity.LecternBlockEntity"		;
	private static final String ENCHANTER_INVENTORY 			= "class net.minecraft.screen.EnchantmentScreenHandler"		;
	
	
	@ModifyVariable(method="sendContentUpdates", at= @At("STORE"), ordinal= 0)
	private ItemStack injectSendContentUpdates(ItemStack itemStack, @Local(ordinal=0) int i){
		Slot slot= slots.get(i);
		String classString = String.valueOf(slot.inventory.getClass());
		
		classString= classString.substring(0, classString.lastIndexOf('$') >0 ? classString.lastIndexOf('$'):classString.length() );
		Entity entityToSet;
		
		switch(classString){
			case PLAYER_INVENTORY_STRING	: case ENDERCHEST_INVENTORY		: 	/*LOGGER.info("PlayerInventory");  / 	break;
			case CRAFTING_INVENTORY		: case CRAFTING_RESULT_INVENTORY	:	/*LOGGER.info("CraftingInventory") /	break;
			
			case MC_CHEST_INVENTORY		: case CHEST_BOAT_INVENTORY		:
			case DOUBLE_INVENTORY		: case CHEST_BLOCK_INVENTORY		:
			case SHULKER_INVENTORY:										LOGGER.info("CHEST INVENTORY"			); break;
			
			case DISPENSOR_INVENTORY		: case DROPPER_INVENTORY			:	LOGGER.info("DISPENSOR INVENTORY"		); break;
			case CRAFTER_INVENTORY		: 								LOGGER.info("CRAFTER INVENTORY"		); break;
			case HOPPER_INVENTORY		: case MC_HOPPER_INVENTORY		:	LOGGER.info("HOPPER INVENTORY"		); break;
			case LOOM_INVENTORY			: 								LOGGER.info("LOOM INVENTORY"			); break;
			case GRINDSTONE_INVENTORY	: 								LOGGER.info("GRINDSTONE INVENTORY"		); break;
			case CARTOGRAPHY_INVENTORY	: 								LOGGER.info("CARTOGRAPHY INVENTORY"	); break;
			case SMITHING_TABLE_INVENTORY	: 								LOGGER.info("SMITHING TABLE INVENTORY"	); break;
			
			case FURNACE_INVENTORY 		: case BLAST_FURNACE_INVENTORY	:
			case SMOKER_INVENTORY 		: 								LOGGER.info("FURNACE INVENTORY"		); break;
			case BREWING_STAND_INVENTORY	: 								LOGGER.info("BREWING INVENTORY"		); break;
			case BEACON_INVENTORY 		: 								LOGGER.info("BEACON INVENTORY"		); break;
			case STONECUTTER_INVENTORY	: 								LOGGER.info("STONECUTTER INVENTORY"	); break;
			case LECTERN_INVENTORY		: 								LOGGER.info("LECTERN INVENTORY"		); break;
			
			default: LOGGER.info( "Undefined Class String: " + classString);
		}
		

//		((iItemStackMixin) itemStack).setInventoryType( slot.inventory.getClass() );

		return itemStack;
	}
	*/
	
	@Shadow public abstract Slot getSlot(int index);
	
	@Shadow @Final private static Logger LOGGER;
	
	@Shadow @Final public DefaultedList<Slot> slots;
	
	@Inject(method="insertItem",at=@At("HEAD"),cancellable = true)
	protected void injectInsertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast, CallbackInfoReturnable cir){
 		//LOGGER.info("inject insertItem");
		Slot firstSlot = this.getSlot(startIndex);
		LOGGER.info( firstSlot.inventory.getClass().toString() );
		
		// Inventory -> Player Inventory
		if (firstSlot.inventory instanceof PlayerInventory) {
			PlayerEntity player = (((PlayerInventory) firstSlot.inventory).player);
			ItemStack dummyStack= createDummyStack(stack,player);
			int stackSize =      stack.getMaxCount();
			int dummySize = dummyStack.getMaxCount();
			int stackSizeDifference = stackSize-dummySize;
			
			stack.setHolder(player);
			
			// If Player Inventory Stacks Equal or More: return to vanilla treatment;
			if 			(stackSizeDifference <= 0 ) 		{ 					return; }
			
			// Player Inventory Stacks Less but the stack's current count fits in the new size.
			if		 	(stack.getCount() <= dummySize) 	{ stack.getMaxCount();	return; }
			
			// Player Inventory Stacks Less AND the stack is larger than the Player inventory's stack size.
			else {
				//LOGGER.info("Source Is Larger Than Target Allows: " + stack.getItem().getTranslationKey());
				cir.setReturnValue(recurseStackable(stack, startIndex, endIndex, fromLast,dummySize, player));
			}
		}
		
		//Player Inventory -> Inventory
		else {
			ItemStack dummyStack= createDummyStack(stack, null);
			int stackSize =      stack.getMaxCount();
			int dummySize = dummyStack.getMaxCount();
			int stackSizeDifference = stackSize-dummySize;
			stack.setHolder(null);
			
			// If Inventory Stacks Equal or More: return to vanilla treatment;
			if 			(stackSizeDifference <= 0 ) 		{ 					return; }
			
			// If Inventory Stacks Less but the stack's current count fits in the new size, correct stacksize and return to vanila treatment.
			if		 	(stack.getCount() <= dummySize) 	{ stack.getMaxCount();	return; }
			
			// Inventory Stacks Less AND the stack is larger than the Inventory's stack size.
			else {
				//LOGGER.info("Source Is Larger Than Target Allows: " + stack.getItem().getTranslationKey());
				
				//If stackable in the player inventory
				cir.setReturnValue(recurseStackable(stack, startIndex, endIndex, fromLast,dummySize, null));
				
			}
			LOGGER.info( "Nulled Out" );
		}
		
	}
	
	private boolean recurseUnstackable(ItemStack stack, int startIndex, int endIndex, boolean fromLast, @Nullable PlayerEntity player) {
		return recurseStackable(stack, startIndex, endIndex, fromLast, 1, player);
	}
	
	private boolean recurseStackable(ItemStack stack, int startIndex, int endIndex, boolean fromLast, int dummySize, @Nullable PlayerEntity player) {
		ItemStack targetMaxStack= stack.copyWithCount(  Math.min(dummySize,stack.getCount())  );
		targetMaxStack.setHolder(player);
		targetMaxStack.getMaxCount();
		
		if (vanillaStackableCode(targetMaxStack, startIndex, endIndex, fromLast)){
			stack.decrement(  dummySize-targetMaxStack.getCount()  );
			if ( ! stack.isEmpty() )
				recurseStackable(stack,startIndex,endIndex, fromLast, dummySize, player);
			return true;
		}
		return false;
	}
	
	private boolean vanillaStackableCode (ItemStack stack, int startIndex, int endIndex, boolean fromLast){
		boolean toReturn= false;
		
		int i =  (fromLast)? endIndex-1: startIndex;
		
		
		if (stack.isStackable()) {
			while (!stack.isEmpty() && (fromLast ? i >= startIndex : i < endIndex)) {
				Slot slot = this.slots.get(i);
				ItemStack itemStack = slot.getStack();
				
				if (!itemStack.isEmpty() && ItemStack.areItemsAndComponentsEqual(stack, itemStack)) {
					int j = itemStack.getCount() + stack.getCount();
					int k = slot.getMaxItemCount(itemStack);
					if (j <= k) {
						stack.setCount(0);
						itemStack.setCount(j);
						slot.markDirty();
						toReturn = true;
					}
					else if (itemStack.getCount() < k) {
						stack.decrement(k - itemStack.getCount());
						itemStack.setCount(k);
						slot.markDirty();
						toReturn = true;
					}
				}
				
				if (fromLast) {
					i--;
				} else {
					i++;
				}
			}
		}
		
		if (!stack.isEmpty()) {
			if (fromLast) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}
			
			while (fromLast ? i >= startIndex : i < endIndex) {
				Slot slotx = this.slots.get(i);
				ItemStack itemStackx = slotx.getStack();
				if (itemStackx.isEmpty() && slotx.canInsert(stack)) {
					int j = slotx.getMaxItemCount(stack);
					slotx.setStack(stack.split(Math.min(stack.getCount(), j)));
					slotx.markDirty();
					toReturn = true;
					break;
				}
				
				if (fromLast) {
					i--;
				} else {
					i++;
				}
			}
		}
		
		
		
		
		
		
		
		return toReturn;
	}
	
}

