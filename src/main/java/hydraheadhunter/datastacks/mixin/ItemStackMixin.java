package hydraheadhunter.datastacks.mixin;

import hydraheadhunter.datastacks.util.ItemStackMixinInterface;
import hydraheadhunter.datastacks.util.ModTags;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.block.Block;
import net.minecraft.component.*;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static hydraheadhunter.datastacks.DataDrivenStacks.*;


/** Mixes into the ItemStack class at a number of places to
 * facilitate overwriting default stack sizes with
 * data-driven stack sizes.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder, FabricItemStack, ItemStackMixinInterface {
	@Shadow public abstract boolean isEmpty();
	
	@Shadow @Final private static Logger LOGGER;
	
	
	@Unique public EntityType<?> holderType;
	@Unique public List<Map.Entry<Integer, TagKey<Item>>> itemIsInValues = null;
	
	/** Makes holding entities less resource intensive
	 * by only storing entity types to holderType for non- ItemEntity entities
	 */
	@Inject(method = "setHolder", at= @At("HEAD"), cancellable = true)
	private void setHolderType( Entity entity, CallbackInfo info ) {
		if (entity == null ) {holderType = null; return; }
		holderType = entity.getType();
		
		if      ( entity instanceof MobEntity ) 	{ info.cancel(); }
		else if ( entity instanceof PlayerEntity)	{ info.cancel(); }
	//	else 								{ LOGGER.info("DIFFERENT TYPE"); }
	}
	
	//TODO: CACHE ISIN INFO TO CUT DOWN SEARCH TIME (?)
	/**Overwrites max_stack_size data component using data
	 * before the original function can fetch and return it.
	 */
	@Inject(method = "getMaxCount", at = @At("HEAD"))
	private void overrideMaxCountWithData(CallbackInfoReturnable<Integer> cir) {
		//Early returns.
		if (this.isEmpty()) return;
		
		//Variables Set Up
		ItemStack thisAsStack = (ItemStack) (Object) this;
		Map.Entry<Integer, TagKey<Item>> entry;
		boolean findGreatestValue = true;
		
		// If in max stack size 0, empty it.
		if (thisAsStack.isIn(ModTags.Items.MAX_STACK_SIZE_0) && MaxStackSizeMayChange(thisAsStack, MAX_STACK_SIZE_CAP+1)) {
			thisAsStack.copyAndEmpty();
			return;
			
		}
		
		//Shulkerboxes stack differently whether full or empty
		if( blockIsIn(thisAsStack, BlockTags.SHULKER_BOXES)){
			ContainerComponent contents = thisAsStack.getComponents().getOrDefault(DataComponentTypes.CONTAINER, null);
			ContainerComponent emptyContents= ContainerComponent.DEFAULT;
			findGreatestValue = SMALLER_STACK_FULL_SHULKERS == emptyContents.equals(contents); //xnor
		}
		
		//Entity inventories stack more or less than in general.
		else if (holderType != null) {
			// Villagers
			if (holderType.equals(EntityType.VILLAGER ))
				if ( itemIsIn(thisAsStack, ModTags.Items.VILLAGER_MORE) )
					findGreatestValue=true;
				else
					findGreatestValue = ! itemIsIn(thisAsStack, ModTags.Items.VILLAGER_LESS);
			
			// Players
			if (holderType.equals(EntityType.PLAYER)) {
				if ( itemIsIn(thisAsStack, ModTags.Items.PLAYER_MORE) )
					findGreatestValue = true;
				else
					findGreatestValue = !itemIsIn(thisAsStack, ModTags.Items.PLAYER_LESS);

			}
		}
		
		//Non-entity inventories stack more or less than entity inventories.
		else {
			findGreatestValue = ! (
				itemIsIn(thisAsStack, ModTags.Items.VILLAGER_MORE)  ||
				itemIsIn(thisAsStack, ModTags.Items.PLAYER_MORE  )
			);
		}
		
		// use the boolean as decided above to find the correct entry.
		entry= findEntry(thisAsStack, findGreatestValue);
		
		//return early if no entry is found
		if (entry == null) return;
		
		// Attempt to change max stack size data component.
		ChangeMaxStackSize(thisAsStack, entry.getKey());
	}
	
	/** Causes different max stack sizes to not interfere with item stacking
	 */
	@Inject(method = "areItemsAndComponentsEqual", at = @At("TAIL"), cancellable = true)
	private static void areItemAndComponentsBarMaxStackSizeEqual(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue()) {cir.setReturnValue(true); return; }
		if ( blockIsIn(stack, BlockTags.SHULKER_BOXES) &&  ! compareContainerContents(stack, otherStack)) { cir.setReturnValue(false); return; }
		
		int maxCountA = getUnalteredMaxCount(stack);
		int maxCountB = getUnalteredMaxCount(otherStack);
		if (maxCountA != maxCountB) {
			boolean AgtB = maxCountA > maxCountB;
			ItemStack stack1 = AgtB ? stack : otherStack;
			ItemStack stack2 = AgtB ? otherStack : stack;
			int maxCount1 = AgtB ? maxCountA : maxCountB;
			int maxCount2 = AgtB ? maxCountB : maxCountA;
			
			if (stack2.getCount() >= maxCount2) { cir.setReturnValue(false); return; }
			
			Set<ComponentType<?>> stackComponents1 = stack1.getComponents().getTypes();
			Set<ComponentType<?>> stackComponents2 = stack2.getComponents().getTypes();
			
			ArrayList<ComponentType<?>> dummy1 = new ArrayList<>();
			ArrayList<ComponentType<?>> dummy2 = new ArrayList<>();
			
			for (ComponentType<?> com : stackComponents1) { if (!com.equals(DataComponentTypes.MAX_STACK_SIZE)) dummy1.add(com); }
			for (ComponentType<?> com : stackComponents2) { if (!com.equals(DataComponentTypes.MAX_STACK_SIZE)) dummy2.add(com); }
			
			
			cir.setReturnValue( ( stack.isEmpty()&&otherStack.isEmpty() ) || Objects.equals(dummy1, dummy2));
			
		}
	}
	
	/** Causes copies to inherit both the holder type and the itemIsInValues
	 */
	@Inject(method="copy", at=@At("TAIL"), cancellable = true )
	private void copyAdditionalInformation(CallbackInfoReturnable<ItemStack> cir){
		ItemStack newStack= cir.getReturnValue();
		ItemStackMixin mixed= (ItemStackMixin) (Object) newStack;
		mixed.holderType = this.holderType;
		mixed.itemIsInValues = this.itemIsInValues;
		newStack= (ItemStack) (Object) mixed;
		cir.setReturnValue(newStack);
	}
	
	/**Compares the contents of two shulker boxes*/
	@Unique
	private static boolean compareContainerContents(ItemStack stack1, ItemStack stack2) {
		ContainerComponent contents1 = stack1.getComponents().getOrDefault(DataComponentTypes.CONTAINER, null);
		ContainerComponent contents2 = stack2.getComponents().getOrDefault(DataComponentTypes.CONTAINER, null);
		return contents1.equals(contents2);
	}
	
	/** Finds all entries of an item in the max_stack_size_X tags ( 0<= X <=2048 )
	 * Iff itemIsInValues is null.
	 * Stores the found values in itemIsInValues
	 */
	@Unique
	private Map.Entry<Integer, TagKey<Item>> findEntry(ItemStack stack, boolean findGreatestValue){
		if (itemIsInValues == null) {
			itemIsInValues =
			ModTags.Items.STACK_SIZES.entrySet().stream()
			.filter(entry -> itemIsIn(stack, entry.getValue())).toList();
		}
		
		if (this==null || itemIsInValues.isEmpty()) return null;
		List<Map.Entry<Integer, TagKey<Item>>> localfilteredStream= itemIsInValues;
		return findGreatestValue ? localfilteredStream.getLast(): localfilteredStream.getFirst();
	}
	
	/** Checks if the max stack size may and must change then does so. */
	@Unique
	private void ChangeMaxStackSize(ItemStack stack, int target) {
		if (MaxStackSizeMayChange(stack, target) && MaxStackSizeNeedsChanged(stack, target))
			stack.set(DataComponentTypes.MAX_STACK_SIZE, target);
	}
	
	/** Max stack size may change if:
	 * The stack is not overloaded ( stack.count <= target).
	 * The stack doesn't have any custom data ( !!! custom_data={foo:bar} )
	*/
	@Unique
	private boolean MaxStackSizeMayChange(ItemStack stack, int max_stack_size_target) {
		ComponentMap components = stack.getComponents();
		int count = stack.getCount();
		if (count > max_stack_size_target) return false;
		if (components.contains(DataComponentTypes.CUSTOM_DATA)) return false;
		return true;
	}
	
	/** Max stack size must change if:
	 * The max stack size is not equal to the target.
	 */
	@Unique
	private boolean MaxStackSizeNeedsChanged(ItemStack stack, int target) {
		int max_stack_size_is      = getUnalteredMaxCount(stack);
		int max_stack_size_target  = Math.min(target, MAX_STACK_SIZE_CAP);
		return max_stack_size_is  != max_stack_size_target;
	}
	
	/** Returns the max_stack_size data component, as it currently is unaltered. */
	@Unique
	private static int getUnalteredMaxCount(ItemStack stack) {
		return stack.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1);
	}
	
	/** Check if items are in an item tag */
	@Unique
	private static boolean itemIsIn( ItemStack stack, TagKey<Item> tag) { return stack.isIn(tag); }
	
	/** Checks if items are in a block tag*/
	@Unique
	private static boolean blockIsIn(ItemStack stack, TagKey<Block> tag) {
		return stack.getItem() instanceof BlockItem && (((BlockItem) stack.getItem()).getBlock().getDefaultState()).isIn(tag);
	}
	
}
