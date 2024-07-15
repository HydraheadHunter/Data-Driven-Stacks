package hydraheadhunter.datastacks.mixin;

import hydraheadhunter.datastacks.util.ModTags;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.block.Block;
import net.minecraft.component.*;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static hydraheadhunter.datastacks.DataDrivenStacks.SMALLER_STACK_FULL_SHULKERS;
import static hydraheadhunter.datastacks.DataDrivenStacks.MAX_STACK_SIZE_CAP;
import static java.lang.String.valueOf;

@Mixin(ItemStack.class)
public abstract class ItemStack_SizeMixin implements ComponentHolder, FabricItemStack {
	
	@Shadow public abstract ItemStack copyWithCount(int count);
	
	@ModifyArg(method = "method_57371", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;"), index = 1)
	private static int changeMaxStackSizeLimit(int original) {
		return MAX_STACK_SIZE_CAP;
	}
	
	
	@Inject(method = "getMaxCount", at = @At("HEAD"))
	private void updateMaxStackSizeWithTag(CallbackInfoReturnable<Integer> cir) {
		ItemStack thisAsStack = (ItemStack) (Object) this;
		
		Map.Entry<Integer, TagKey<Item>> entry;
		if( blockIsIn(thisAsStack, BlockTags.SHULKER_BOXES)){
			ContainerComponent contents = thisAsStack.getComponents().getOrDefault(DataComponentTypes.CONTAINER, null);
			ContainerComponent emptyContents= ContainerComponent.DEFAULT;
			entry = findEntry(thisAsStack, !(SMALLER_STACK_FULL_SHULKERS ^ emptyContents.equals(contents)) ); //xnor
		}
		else {
			entry = findEntry(thisAsStack, true);
		}
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
	
	private static boolean compareContainerContents(ItemStack stack1, ItemStack stack2) {
		ContainerComponent contents1 = stack1.getComponents().getOrDefault(DataComponentTypes.CONTAINER, null);
		ContainerComponent contents2 = stack2.getComponents().getOrDefault(DataComponentTypes.CONTAINER, null);
		return contents1.equals(contents2);
	}
	
	@Unique
	private Map.Entry<Integer, TagKey<Item>> findEntry(ItemStack stack, boolean findLast){
		List<Map.Entry<Integer, TagKey<Item>>> setFilteredStream =
		ModTags.Items.STACK_SIZES.entrySet().stream()
		.filter(entry -> itemIsIn(stack, entry.getValue())).toList();
		
		if (setFilteredStream.isEmpty()) return null;
		return findLast? setFilteredStream.getLast(): setFilteredStream.getFirst();
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
}
