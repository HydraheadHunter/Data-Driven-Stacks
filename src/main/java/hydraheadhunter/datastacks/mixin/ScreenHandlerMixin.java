package hydraheadhunter.datastacks.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
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
	
	@Shadow public abstract ItemStack getCursorStack();
	
	
	/** Changes the behavior for click-and-drag item placement
	 * For each slot being affected by a click-and-drag action,
	 * overrides the result stack size, called `n`, with a math.min() call
	 * comparing n against a dummyStack of the item in that slot.
	 */
	
	@ModifyVariable(method = "internalOnSlotClick", at = @At("STORE"), ordinal = 5)
	private int modifyItemStack2(int stackMaxCount, @Local Slot slot, @Local(ordinal=0) ItemStack stack ){
		Entity player = slot.inventory instanceof PlayerInventory ? ((PlayerInventory) slot.inventory).player:null;
		ItemStack dummyStack= createDummyStack(stack,player);
		stack.setHolder(player);
		return Math.min( stackMaxCount, dummyStack.getMaxCount() );
		//return stackMaxCount;
	}
	
	/** Changes the behavior of click item placement.
	 * Checks if stack size distinction for player/non-player. If no, returns to vanilla behavior.
	 * Checks if stack is smaller than max stack for target slot. If so, updates holder and returns to vanilla behavior.
	 * Checks if overloaded stack can be combined into slot stack. If no, cancels.
	 * 	If so, checks if slot is empty or non-empty.
	 * 	then adds to slot stack accordingly and cancels.
	 */
	@Inject(method="internalOnSlotClick", at=@At(value="INVOKE",target="Lnet/minecraft/screen/ScreenHandler;getCursorStack()Lnet/minecraft/item/ItemStack;"),
	slice= @Slice( from= @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"),
				to= @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;onPickupSlotClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/ClickType;)V")),
	cancellable=true)
	private void changeOverloadedPickUpBehavior(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
		ItemStack cursorStack= this.getCursorStack();
		ClickType clickType= button==0 ? ClickType.LEFT:ClickType.RIGHT;
		
		if(slotIndex<0) { return;	}
		
		Slot targetSlot= this.slots.get(slotIndex);
		ItemStack slotStack= targetSlot.getStack();
		
		
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
			int amount = clickType == ClickType.LEFT? slotRoom:1;
			slotStack.increment( amount);
			cursorStack.decrement(amount);
			slot.markDirty();
			info.cancel();
		}
		else {
			
			ItemStack dummyStack = createDummyStack(cursorStack, isPlayerInventory?player:null);
			int slotMax = dummyStack.getMaxCount();
			int amount = clickType == ClickType.LEFT? slotMax:1;
			slot.setStack( dummyStack.copyWithCount( amount ));
			cursorStack.decrement(amount);
			slot.markDirty();
			info.cancel();
		}
		
	}
	
	/** Boolean check for player/non-player stack-size distinction. */
	@Unique
	private boolean stackSizesDifferent(ItemStack stack, Entity player) {
		if ( stack.isEmpty())  return false;
		ItemStack dummyStackInventory= createDummyStack(stack, null);
		ItemStack dummyStackPlayer= createDummyStack(stack, player);
		
		return dummyStackInventory.getMaxCount() != dummyStackPlayer.getMaxCount() ;
	}
	
	/** Boolean check for `count > max_stack_size` in player/non-player distinguished stacks*/
	@Unique
	private boolean checkStackOverloaded( ItemStack stack, Entity player, boolean isPlayerInventory){
		ItemStack targetInventoryDummyStack = createDummyStack(stack, isPlayerInventory?player:null);
		return stack.getCount() > targetInventoryDummyStack.getMaxCount();
	}
	
	
	/** Changes behavior for shift-click item movement.
	 * Checks if stack has player/non-player stack-size distinction. If no returns to vanilla behavior
	 * Checks if the stack <= target slots max_stack_size. If so, set's holder and returns to vanilla behavior.
	 * If not returned early, calls its' recursive helper function to override the vanilla behavior and return value.
	 */
	@Inject(method="insertItem",at=@At("HEAD"),cancellable = true)
	protected void OverrideInsertItemIfNecessary(ItemStack stack, int startIndex, int endIndex, boolean fromLast, CallbackInfoReturnable<Boolean> cir){
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
	
	/** OverridedInserItemIfNecessary's recursive helper function
	 * Attempts to insert all the items in the source stack, one target-inventory stack's-worth at a time.
	 * Decrements e source Stack by the amount accepted.
	 * Returns when the source stack either
	 * 		• Becomes empty
	 * 		• Doesn't decrement( implying the target inventory has filled up)
	 * Otherwise, it recurses.
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
	 * Returns a boolean on whether the stack argument was decremented.
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

