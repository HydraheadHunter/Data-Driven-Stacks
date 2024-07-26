package hydraheadhunter.datastacks.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static hydraheadhunter.datastacks.util.common.createDummyStack;

/** Changes the behaviors of items that are shift-clicked betwixt different inventories.
 * Specifically makes sure that when items are moved betwixt two inventories,
 * they respect their max stack size in the target inventory,
 * if different from their max stack size in the source inventory.
*/
@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
	
	@Shadow public abstract Slot getSlot(int index);
	@Shadow @Final public DefaultedList<Slot> slots;
	
	@Shadow @Final private static Logger LOGGER;
	
	@Shadow public abstract ItemStack getCursorStack();
	
	/** Injects at the top of the insertItem method
	 *  If the source and target max stack sizes are the same,
	 *  or the source stack's count is less than the target inventory's max stack size,
	 *  the function returns to vanilla behavior.
	 *  Otherwise, it overrides the vanilla function's return
	 *  value with the result of it's recursive helper function.
	 */
	@Inject(method="insertItem",at=@At("HEAD"),cancellable = true)
	protected void checkIfOverrideNecessary(ItemStack stack, int startIndex, int endIndex, boolean fromLast, CallbackInfoReturnable<Boolean> cir){
		//Setup and housekeeping
		Slot firstSlot = this.getSlot(startIndex);
		PlayerEntity player= (firstSlot.inventory instanceof PlayerInventory)? ((PlayerInventory) firstSlot.inventory).player:null;
		ItemStack dummyStack= createDummyStack(stack,player);
		int stackSize =      stack.getMaxCount();
		int dummySize = dummyStack.getMaxCount();
		int stackSizeDifference = stackSize-dummySize;
		stack.setHolder(player);
		
		//Check if return early
		if 	(stackSizeDifference <= 0 	) 	{ 					return; }
		if	(stack.getCount() <= dummySize) 	{ stack.getMaxCount();	return; }
		
		//If still going, override insertItem's return.
		cir.setReturnValue(recursiveInsertStack(stack, startIndex, endIndex, fromLast, dummySize, player));
		
	}

	@Inject(method="internalOnSlotClick", at=@At("HEAD"))
	private void injectInternalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci){
		//LOGGER.info("injecting InternalOnSlotClick");
	}
	
	@ModifyVariable(method= "internalOnSlotClick", at=@At("STORE"), name="n")
	private int modifyItemStack2(int n, @Local(name="slot2") Slot slot, @Local(name="itemStack2") ItemStack stack ){
		Entity player = slot.inventory instanceof PlayerInventory ? ((PlayerInventory) slot.inventory).player:null;
		ItemStack dummyStack= createDummyStack(stack,player);
		
		return Math.min( n, dummyStack.getMaxCount() );
	}
	
	@Inject(method="internalOnSlotClick", at=@At(value="INVOKE",target="Lnet/minecraft/screen/ScreenHandler;getCursorStack()Lnet/minecraft/item/ItemStack;"),
	slice= @Slice( from= @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"),
				to= @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;onPickupSlotClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/ClickType;)V")),
	cancellable=true)
	private void changeOverloadedPickUpBehavior(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info ) {
		Slot targetSlot= this.slots.get(slotIndex);
		ItemStack slotStack= targetSlot.getStack();
		ItemStack cursorStack= this.getCursorStack();
		
		if ( ! stackSizesDifferent(cursorStack, player) ) return ;
		Slot slot = this.slots.get(slotIndex);
		boolean isPlayerInventory;
		if ( true ) {
			PlayerEntity player2 = (slot.inventory instanceof PlayerInventory)? ((PlayerInventory) slot.inventory).player:null;
			//If player and player2 aren't the same, and player2 isn't null, return early in a panic because I don't know how to how you'd get that case but I ain't want to deal with it
			if (!player.equals(player2) && player2 != null) return;
			isPlayerInventory= player2!=null;
		}
		
		boolean isCursorOverloaded= checkStackOverloaded(cursorStack, player, isPlayerInventory);
		
		if( !isCursorOverloaded){
			cursorStack.setHolder( isPlayerInventory? player:null);
			cursorStack.getMaxCount();
		}
		else if ( !slotStack.isEmpty() && (!ItemStack.areItemsAndComponentsEqual(cursorStack,slotStack) || slotStack.getMaxCount()-slotStack.getCount()<=0)){ info.cancel();	}
		else if (!slotStack.isEmpty()){
			int slotMax = slotStack.getMaxCount();
			int slotCount= slotStack.getCount();
			int slotRoom= slotMax-slotCount;
			slotStack.increment(slotRoom);
			cursorStack.decrement(slotRoom);
			slot.markDirty();
			info.cancel();
		}
		else {
			ItemStack dummyStack = createDummyStack(cursorStack, isPlayerInventory?player:null);
			int slotMax = dummyStack.getMaxCount();
			slot.setStack( dummyStack.copyWithCount( slotMax ));
			cursorStack.decrement(slotMax);
			slot.markDirty();
			info.cancel();
		}
		
	}
	
	@Unique
	private boolean stackSizesDifferent(ItemStack stack, Entity player) {
		if ( stack.isEmpty())  return false;
		ItemStack dummyStackInventory= createDummyStack(stack, null);
		ItemStack dummyStackPlayer= createDummyStack(stack, player);
		
		return dummyStackInventory.getMaxCount() != dummyStackPlayer.getMaxCount() ;
	}
	
	@Unique
	private boolean checkStackOverloaded( ItemStack stack, Entity player, boolean isPlayerInventory){
		ItemStack targetInventoryDummyStack = createDummyStack(stack, isPlayerInventory?player:null);
		return stack.getCount() > targetInventoryDummyStack.getMaxCount();
	}
	
	/** A recursive function which takes the uses the target inventory's max stack size
	 * to add items one target inventory's stack worth at a time.
	 * decrementing the source Stack by the amount accepted.
	 * Returns when the stack didn't decriment (which implies the inventory has become filled up).
	 * or the stack is empty. Otherwise, it recurses.
	 */ @Unique
	private boolean recursiveInsertStack(ItemStack stack, int startIndex, int endIndex, boolean fromLast, int dummySize, @Nullable PlayerEntity player) {
		ItemStack targetMaxStack= stack.copyWithCount(  Math.min(dummySize,stack.getCount())  );
		targetMaxStack.setHolder(player);
		targetMaxStack.getMaxCount();
		
		if (vanillaStackableCode(targetMaxStack, startIndex, endIndex, fromLast)){
			stack.decrement(  dummySize-targetMaxStack.getCount()  );
			if ( ! stack.isEmpty() )
				recursiveInsertStack(stack,startIndex,endIndex, fromLast, dummySize, player);
			return true;
		}
		return false;
	}

	/**The vanilla code for inserting items.
	 */ @Unique
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
	//*/
	
	
	
}

