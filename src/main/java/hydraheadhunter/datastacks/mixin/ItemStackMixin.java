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
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static hydraheadhunter.datastacks.DataDrivenStacks.*;
import static java.lang.String.valueOf;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder, FabricItemStack, ItemStackMixinInterface {
	
	@Shadow public abstract ItemStack copyWithCount(int count);
	@Shadow public abstract boolean isEmpty();
	
	@Shadow public abstract @Nullable Entity getHolder();
	
	public EntityType<?> holderType;
	public List<Map.Entry<Integer, TagKey<Item>>> setFilteredStream = null;
	
	
	@Inject(method = "setHolder", at= @At("HEAD"), cancellable = true)
	private void setHolderType( Entity entity, CallbackInfo info ) {
		if (entity == null ) {holderType = null; return; }
		holderType = entity.getType();
		
		if ( entity instanceof MobEntity ) {
			//LOGGER.info("SET MOB HOLDER");
			info.cancel();
		}
		else if ( holderType.equals(EntityType.PLAYER) ) {
		//	LOGGER.info("SET PLAYER HOLDER");
			info.cancel();
		}
		else {
			//LOGGER.info("DIFFERENT TYPE");
		}
	}
	
	//TODO: CACHE ISIN INFO TO CUT DOWN SEARCH TIME
	@Inject(method = "getMaxCount", at = @At("HEAD"))
	private void overrideMaxCountWithData(CallbackInfoReturnable<Integer> cir) {
		if (this.isEmpty()) return;
		ItemStack thisAsStack = (ItemStack) (Object) this;
		
		
		Map.Entry<Integer, TagKey<Item>> entry = null;
		boolean findGreatestValue = true;
		if( blockIsIn(thisAsStack, BlockTags.SHULKER_BOXES)){
			ContainerComponent contents = thisAsStack.getComponents().getOrDefault(DataComponentTypes.CONTAINER, null);
			ContainerComponent emptyContents= ContainerComponent.DEFAULT;
			findGreatestValue = !(SMALLER_STACK_FULL_SHULKERS ^ emptyContents.equals(contents)) ; //xnor
		}
		else if (holderType != null) {
			if (holderType.equals(EntityType.VILLAGER )) {
				findGreatestValue = ! itemIsIn(thisAsStack, ModTags.Items.VILLAGER_LESS);
			}
			if (holderType.equals(EntityType.PLAYER)) {
				if ( itemIsIn(thisAsStack, ModTags.Items.PLAYER_MORE) ) {
					findGreatestValue = true;
				}
				else {
					findGreatestValue = !itemIsIn(thisAsStack, ModTags.Items.PLAYER_LESS);
				}
			}//*/
		}
		else {
			findGreatestValue = ! (
				itemIsIn(thisAsStack, ModTags.Items.VILLAGER_MORE)  ||
				itemIsIn(thisAsStack, ModTags.Items.PLAYER_MORE  )
			);
		}
		
		entry= findEntry(thisAsStack, findGreatestValue);
		if (entry == null) return;
		
		ChangeMaxStackSize(thisAsStack, entry.getKey());
	}
	
	@Inject(method = "areItemsAndComponentsEqual", at = @At("HEAD"), cancellable = true)
	private static void testIfMethodBeingInjected(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> cir) {
	/*	if (stack.isOf(Items.WHITE_SHULKER_BOX)  && otherStack.isOf(Items.WHITE_SHULKER_BOX) ){
			if (stack.getCount() != otherStack.getCount() ) {
				System.out.println(valueOf(stack.getCount()) + ", " + valueOf(otherStack.getCount()));
				ContainerComponent contents1 =      stack.getComponents().getOrDefault(DataComponentTypes.CONTAINER, null);
				ContainerComponent contents2 = otherStack.getComponents().getOrDefault(DataComponentTypes.CONTAINER, null);
				if (contents1.equals(contents2)) return;
				int test1 = 0;
			}
		}
	*/
	}
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
			
			
			cir.setReturnValue(stack.isEmpty() && otherStack.isEmpty() ? true : Objects.equals(dummy1, dummy2));
			
		}
	}
	
	@Inject(method="copy", at=@At("TAIL") )
	private ItemStack copyAdditionalInformation(CallbackInfoReturnable<ItemStack> cir){
		ItemStack newStack= cir.getReturnValue();
		ItemStackMixin mixed= (ItemStackMixin) (Object) newStack;
		mixed.holderType = this.holderType;
		mixed.setFilteredStream= this.setFilteredStream;
		newStack= (ItemStack) (Object) mixed;
		return newStack;
	}
	
	private static boolean compareContainerContents(ItemStack stack1, ItemStack stack2) {
		ContainerComponent contents1 = stack1.getComponents().getOrDefault(DataComponentTypes.CONTAINER, null);
		ContainerComponent contents2 = stack2.getComponents().getOrDefault(DataComponentTypes.CONTAINER, null);
		return contents1.equals(contents2);
	}
	
	
	@Unique
	private Map.Entry<Integer, TagKey<Item>> findEntry(ItemStack stack, boolean findGreatestValue){
		if (setFilteredStream == null) {
			setFilteredStream =
			ModTags.Items.STACK_SIZES.entrySet().stream()
			.filter(entry -> itemIsIn(stack, entry.getValue())).toList();
		}
		
		if (setFilteredStream.isEmpty()) return null;
		List<Map.Entry<Integer, TagKey<Item>>> localfilteredStream= setFilteredStream;
		return findGreatestValue ? localfilteredStream.getLast(): localfilteredStream.getFirst();
	}
	
	@Unique
	private void ChangeMaxStackSize(ItemStack stack, int target) {
		if (MaxStackSizeMayChange(stack, target) && MaxStackSizeNeedsChanged(stack, target))
			stack.set(DataComponentTypes.MAX_STACK_SIZE, target);
	}
	
	@Unique
	private boolean MaxStackSizeMayChange(ItemStack stack, int max_stack_size_target) {
		ComponentMap components = stack.getComponents();
		int count = stack.getCount();
		if (count > max_stack_size_target) return false;
		if (components.contains(DataComponentTypes.CUSTOM_DATA)) return false;
		return true;
	}
	
	@Unique
	private boolean MaxStackSizeNeedsChanged(ItemStack stack, int target) {
		int max_stack_size_is = getUnalteredMaxCount(stack);
		int max_stack_size_target = Math.min(target, MAX_STACK_SIZE_CAP);
		return max_stack_size_is != max_stack_size_target;
	}
	
	@Unique
	private static int getUnalteredMaxCount(ItemStack stack) {
		return stack.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1);
	}
	
	@Unique
	private static boolean itemIsIn( ItemStack stack, TagKey<Item> tag) {
		return stack.isIn(tag);
	}
	@Unique
	private static boolean blockIsIn(ItemStack stack, TagKey<Block> tag) {
		return stack.getItem() instanceof BlockItem && (((BlockItem) stack.getItem()).getBlock().getDefaultState()).isIn(tag);
	}

	public void setInventoryType( Class<?> inventoryType){
	
	}
	
	
}
