package hydraheadhunter.datastacks.mixin;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
	
	/** Injects at the top of the insertItem method
	 *  If the source and target max stack sizes are the same,
	 *  or the source stack's count is less than the target inventory's max stack size,
	 *  the function returns to vaniila behavior.
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

