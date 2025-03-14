package hydraheadhunter.datastacks.mixin;


import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
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
	
	//@Shadow @Final private static Logger LOGGER;
	
/* Testing every god damned function in this fucking class to see what is called where.
	@Inject(method="addPlayerHotbarSlots", at=@At("HEAD"))
	private void test1(Inventory playerInventory, int left, int y, CallbackInfo ci){ LOGGER.info(String.valueOf(1));	}
	//@Inject(method="addPlayerInventorySlots", at=@At("HEAD"))
	//private void test2(Inventory playerInventory, int left, int y, CallbackInfo ci){ LOGGER.info(String.valueOf(2));	}
	//@Inject(method="addPlayerSlots", at=@At("HEAD")) //NON-CANDIDATE
	//private void test3(Inventory playerInventory, int left, int y, CallbackInfo ci){ LOGGER.info(String.valueOf(3));	}
	@Inject(method="canUse(Lnet/minecraft/screen/ScreenHandlerContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/block/Block;)Z", at=@At("HEAD"))
	private static void test4(ScreenHandlerContext context, PlayerEntity player, Block block, CallbackInfoReturnable<Boolean> cir){ LOGGER.info(String.valueOf(4));	}
	@Inject(method="getType", at=@At("HEAD"))
	private void test5(CallbackInfoReturnable<ScreenHandlerType<?>> cir){ LOGGER.info(String.valueOf(5));	}
	@Inject(method="checkSize", at=@At("HEAD"))
	private static void test6(Inventory inventory, int expectedSize, CallbackInfo ci){ LOGGER.info(String.valueOf(6));	}
	@Inject(method="checkDataCount", at=@At("HEAD"))
	private static void test7(PropertyDelegate data, int expectedCount, CallbackInfo ci){ LOGGER.info(String.valueOf(7));	}
	@Inject(method="isValid", at=@At("HEAD"))
	private void test8(int slot, CallbackInfoReturnable<Boolean> cir){ LOGGER.info(String.valueOf(8));	}
	//@Inject(method="addSlot", at=@At("HEAD")) //NON-CANDIDATE
	//private void test9(Slot slot, CallbackInfoReturnable<Slot> cir){ LOGGER.info(String.valueOf(9));	}
	@Inject(method="addProperty", at=@At("HEAD"))
	private void test10(Property property, CallbackInfoReturnable<Property> cir){ LOGGER.info(String.valueOf(10));	}
	@Inject(method="addProperties", at=@At("HEAD"))
	private void test11(PropertyDelegate propertyDelegate, CallbackInfo ci){ LOGGER.info(String.valueOf(11));	}
	@Inject(method="addListener", at=@At("HEAD"))
	private void test12(ScreenHandlerListener listener, CallbackInfo ci){ LOGGER.info(String.valueOf(12));	}
	@Inject(method="syncState", at=@At("HEAD"))
	private void test12point5(CallbackInfo ci){ LOGGER.info(String.valueOf(12.5));	}
	@Inject(method="updateSyncHandler", at=@At("HEAD"))
	private void test13(ScreenHandlerSyncHandler handler, CallbackInfo ci){ LOGGER.info(String.valueOf(13));	}
	@Inject(method="removeListener", at=@At("HEAD"))
	private void test14(ScreenHandlerListener listener, CallbackInfo ci){ LOGGER.info(String.valueOf(14));	}
	@Inject(method="getStacks", at=@At("HEAD"))
	private void test15(CallbackInfoReturnable<DefaultedList<ItemStack>> cir){ LOGGER.info(String.valueOf(15));	}
	//@Inject(method="sendContentUpdates", at=@At("HEAD"))
	//private void test16(CallbackInfo ci){ LOGGER.info(String.valueOf(16));	}
	@Inject(method="updateToClient", at=@At("HEAD"))
	private void test17(CallbackInfo ci){ LOGGER.info(String.valueOf(17));	}
	@Inject(method="notifyPropertyUpdate", at=@At("HEAD"))
	private void test18(int index, int value, CallbackInfo ci){ LOGGER.info(String.valueOf(18));	}
	//@Inject(method="updateTrackedSlot", at=@At("HEAD")) //CANDIDATE
	//private void test19(int slot, ItemStack stack, Supplier<ItemStack> copySupplier, CallbackInfo ci){ LOGGER.info(String.valueOf(19));	}
	//@Inject(method="checkSlotUpdates", at=@At("HEAD")) //CANDIDATE
	//private void test20(int slot, ItemStack stack, Supplier<ItemStack> copySupplier, CallbackInfo ci){ LOGGER.info(String.valueOf(20));	}
	@Inject(method="checkPropertyUpdates", at=@At("HEAD"))
	private void test21(int id, int value, CallbackInfo ci){ LOGGER.info(String.valueOf(21));	}
	//@Inject(method="checkCursorStackUpdates", at=@At("HEAD"))
	//private void test22(CallbackInfo ci){ LOGGER.info(String.valueOf(22));	}
	@Inject(method="setPreviousTrackedSlot", at=@At("HEAD"))
	private void test23(int slot, ItemStack stack, CallbackInfo ci){ LOGGER.info(String.valueOf(23));	}
	@Inject(method="setPreviousTrackedSlotMutable", at=@At("HEAD"))
	private void test24(int slot, ItemStack stack, CallbackInfo ci){ LOGGER.info(String.valueOf(24));	}
	@Inject(method="setPreviousCursorStack", at=@At("HEAD"))
	private void test25(ItemStack stack, CallbackInfo ci){ LOGGER.info(String.valueOf(25));	}
	@Inject(method="onButtonClick", at=@At("HEAD"))
	private void test26(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir){ LOGGER.info(String.valueOf(26));	}
	@Inject(method="getSlot", at=@At("HEAD"))
	private void test27(int index, CallbackInfoReturnable<Slot> cir){ LOGGER.info(String.valueOf(27));	}
	//@Inject(method="quickMove", at=@At("HEAD")) Is because abstract?
	//private void test28(PlayerEntity par1, int par2, CallbackInfoReturnable<ItemStack> cir){ LOGGER.info(String.valueOf(28));	}
	@Inject(method="selectBundleStack", at=@At("HEAD"))
	private void test29(int slot, int selectedStack, CallbackInfo ci){ LOGGER.info(String.valueOf(29));	}
	@Inject(method="onSlotClick", at=@At("HEAD"))
	private void test30(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci){ LOGGER.info(String.valueOf(30));	}
	@Inject(method="internalOnSlotClick", at=@At("HEAD"))
	private void test31(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci){ LOGGER.info(String.valueOf(31));	}
	@Inject(method="handleSlotClick", at=@At("HEAD"))
	private void test32(PlayerEntity player, ClickType clickType, Slot slot, ItemStack stack, ItemStack cursorStack, CallbackInfoReturnable<Boolean> cir){ LOGGER.info(String.valueOf(32));	}
	@Inject(method="getCursorStackReference", at=@At("HEAD"))
	private void test33(CallbackInfoReturnable<StackReference> cir){ LOGGER.info(String.valueOf(33));	}
	@Inject(method="canInsertIntoSlot(Lnet/minecraft/item/ItemStack;Lnet/minecraft/screen/slot/Slot;)Z", at=@At("HEAD"))
	private void test34(ItemStack stack, Slot slot, CallbackInfoReturnable<Boolean> cir){ LOGGER.info(String.valueOf(34));	}
	@Inject(method="onClosed", at=@At("HEAD"))
	private void test35(PlayerEntity player, CallbackInfo ci){ LOGGER.info(String.valueOf(35));	}
	@Inject(method="offerOrDropStack", at=@At("HEAD"))
	private static void test36(PlayerEntity player, ItemStack stack, CallbackInfo ci){ LOGGER.info(String.valueOf(36));	}
	@Inject(method="dropInventory", at=@At("HEAD"))
	private void test37(PlayerEntity player, Inventory inventory, CallbackInfo ci){ LOGGER.info(String.valueOf(37));	}
	@Inject(method="onContentChanged", at=@At("HEAD"))
	private void test38(Inventory inventory, CallbackInfo ci){ LOGGER.info(String.valueOf(38));	}
	@Inject(method="setStackInSlot", at=@At("HEAD"))
	private void test39(int slot, int revision, ItemStack stack, CallbackInfo ci){ LOGGER.info(String.valueOf(39));	}
	@Inject(method="updateSlotStacks", at=@At("HEAD"))
	private void test40(int revision, List<ItemStack> stacks, ItemStack cursorStack, CallbackInfo ci){ LOGGER.info(String.valueOf(40));	}
	@Inject(method="setProperty", at=@At("HEAD"))
	private void test41(int id, int value, CallbackInfo ci){ LOGGER.info(String.valueOf(41));	}
	//@Inject(method="canUse(Lnet/minecraft/entity/player/PlayerEntity;)Z", at=@At("HEAD")) abstract
	//private void test42(PlayerEntity par1, CallbackInfoReturnable<Boolean> cir){ LOGGER.info(String.valueOf(42));	}
	@Inject(method="insertItem", at=@At("HEAD"))
	private void test43(ItemStack stack, int startIndex, int endIndex, boolean fromLast, CallbackInfoReturnable<Boolean> cir){ LOGGER.info(String.valueOf(43));	}
	@Inject(method="unpackQuickCraftButton", at=@At("HEAD"))
	private static void test44(int quickCraftData, CallbackInfoReturnable<Integer> cir){ LOGGER.info(String.valueOf(44));	}
	@Inject(method="unpackQuickCraftStage", at=@At("HEAD"))
	private static void test45(int quickCraftData, CallbackInfoReturnable<Integer> cir){ LOGGER.info(String.valueOf(45));	}
	@Inject(method="packQuickCraftData", at=@At("HEAD"))
	private static void test46(int quickCraftStage, int buttonId, CallbackInfoReturnable<Integer> cir){ LOGGER.info(String.valueOf(46));	}
	@Inject(method="shouldQuickCraftContinue", at=@At("HEAD"))
	private static void test47(int stage, PlayerEntity player, CallbackInfoReturnable<Boolean> cir){ LOGGER.info(String.valueOf(47));	}
	@Inject(method="endQuickCraft", at=@At("HEAD"))
	private void test48(CallbackInfo ci){ LOGGER.info(String.valueOf(48));	}
	//@Inject(method="canInsertItemIntoSlot", at=@At("HEAD"))
	//private static void test49(Slot slot, ItemStack stack, boolean allowOverflow, CallbackInfoReturnable<Boolean> cir){ LOGGER.info(String.valueOf(49));	}
	//@Inject(method="calculateStackSize", at=@At("HEAD"))
	//private static void test50(Set<Slot> slots, int mode, ItemStack stack, CallbackInfoReturnable<Integer> cir){ LOGGER.info(String.valueOf(50));	}
	
	
	//@Inject(method="canInsertIntoSlot(Lnet/minecraft/screen/slot/Slot;)Z", at=@At("HEAD"))
	//private void test51(Slot slot, CallbackInfoReturnable<Boolean> cir){ LOGGER.info(String.valueOf(51));	}
	@Inject(method="calculateComparatorOutput(Lnet/minecraft/block/entity/BlockEntity;)I", at=@At("HEAD"))
	private static void test52(BlockEntity entity, CallbackInfoReturnable<Integer> cir){ LOGGER.info(String.valueOf(52));	}
	@Inject(method="calculateComparatorOutput(Lnet/minecraft/inventory/Inventory;)I", at=@At("HEAD"))
	private static void test53(Inventory inventory, CallbackInfoReturnable<Integer> cir){ LOGGER.info(String.valueOf(53));	}
	@Inject(method="setCursorStack", at=@At("HEAD"))
	private void test54(ItemStack stack, CallbackInfo ci){ LOGGER.info(String.valueOf(54));	}
	//@Inject(method="getCursorStack", at=@At("HEAD"))
	//private void test55(CallbackInfoReturnable<ItemStack> cir){ LOGGER.info(String.valueOf(55));	}
	@Inject(method="disableSyncing", at=@At("HEAD"))
	private void test56(CallbackInfo ci){ LOGGER.info(String.valueOf(56));	}
	@Inject(method="enableSyncing", at=@At("HEAD"))
	private void test57(CallbackInfo ci){ LOGGER.info(String.valueOf(57));	}
	@Inject(method="copySharedSlots", at=@At("HEAD"))
	private void test58(ScreenHandler handler, CallbackInfo ci){ LOGGER.info(String.valueOf(58));	}
	@Inject(method="getSlotIndex", at=@At("HEAD"))
	private void test59(Inventory inventory, int index, CallbackInfoReturnable<OptionalInt> cir){ LOGGER.info(String.valueOf(59));	}
	//@Inject(method="getRevision", at=@At("HEAD"))
	//private void test60(CallbackInfoReturnable<Integer> cir){ LOGGER.info(String.valueOf(60));	}
	//@Inject(method="nextRevision", at=@At("HEAD"))
	//private void test61(CallbackInfoReturnable<Integer> cir){ LOGGER.info(String.valueOf(61));	}
*/
	
	
	

	
	
	/** Changes the final behavior for click-and-drag item placement
	 * For each slot being affected by a click-and-drag action,
	 * overrides the result stack size, called `n`, with a math.min() call
	 * comparing n against a dummyStack of the item in that slot.
	 */
	@ModifyVariable(method="internalOnSlotClick", at=@At("STORE"), ordinal=4)
	private int setInventorySensitiveStackSize(int mm, @Local(ordinal=0) Slot slot, @Local(ordinal = 0) ItemStack stack){
		Entity slotOwner = slot.inventory instanceof PlayerInventory ? ((PlayerInventory)slot.inventory).player:null;
		int dummyMax= createDummyStack(stack,slotOwner).getMaxCount();
		
		//LOGGER.info( slot.inventory instanceof PlayerInventory ? "Player":"Non-player" );
		//LOGGER.info( "Slot#"+ slot.id + " Stack Max"+ mm );
		//LOGGER.info( "StackOwner: "+ (slotOwner!=null? slotOwner.toString():"Null"));
		//LOGGER.info( "DummyMax: "+ dummyMax);
		
		return Math.min(mm,dummyMax);
	}
	/*
	@Inject(method="internalOnSlotClick", at=@At(value="INVOKE",target="Lnet/minecraft/screen/ScreenHandler;calculateStackSize(Ljava/util/Set;ILnet/minecraft/item/ItemStack;)I", shift= At.Shift.AFTER))
	private void setSlotStackSize(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, @Local(ordinal=0) Slot slot, @Local(ordinal=1) ItemStack stack, @Local(ordinal = 1) LocalRef<Integer> nn){
		LOGGER.info("DEBUG");
		LOGGER.info( slot.inventory instanceof PlayerInventory ? "PLAYER":"OTHER" );
		Entity slotOwner = slot.inventory instanceof PlayerInventory ? ((PlayerInventory) slot.inventory).player:null;
		stack.setHolder(slotOwner);
		nn= Math.min( nn, createDummyStack(stack,slotOwner).getMaxCount() );
		
	}
	
	@Inject(method="calculateStackSize", at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I"),cancellable = true)
	private static void setSlotStackSizeB(Set<Slot> slots, int mode, ItemStack stack, CallbackInfoReturnable<Integer> cir){
		Slot slot =((Slot)slots.toArray()[0]);
		Entity slotOwner = slot.inventory instanceof PlayerInventory ? ((PlayerInventory) slot.inventory).player:null;
		stack.setHolder(slotOwner);
		cir.setReturnValue( createDummyStack(stack,slotOwner).getMaxCount() );
	}
	*/
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

