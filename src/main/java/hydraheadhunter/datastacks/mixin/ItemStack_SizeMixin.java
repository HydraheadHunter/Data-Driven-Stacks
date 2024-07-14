package hydraheadhunter.datastacks.mixin;

import hydraheadhunter.datastacks.util.ModTags;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.component.*;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static hydraheadhunter.datastacks.DataDrivenStacks.MAX_STACK_SIZE_CAP;

@Mixin(ItemStack.class)
public abstract class ItemStack_SizeMixin implements ComponentHolder, FabricItemStack {
	
	@ModifyArg(method = "method_57371",  at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;"), index = 1)
	private static int changeMaxStackSizeLimit(int original) {
		return MAX_STACK_SIZE_CAP;
	}
	
	@Inject(method="getMaxCount", at = @At("HEAD"))
	private void updateMaxStackSizeWithTag(CallbackInfoReturnable<Integer> cir){
		ItemStack thisAsStack = (ItemStack)(Object) this;
		ModTags.Items.STACK_SIZES.entrySet().stream()
		.filter(entry -> thisAsStack.isIn(entry.getValue()))
		.findFirst()
		.map(Map.Entry::getKey)
		.ifPresentOrElse(stackSize -> ChangeStackSize(thisAsStack, stackSize), ItemStack_SizeMixin::dummy);
	
/*		if      ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_2048)) ChangeStackSize(thisAsStack, 2048 );
		else if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_1024)) ChangeStackSize(thisAsStack, 1024 );
		else if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_512 )) ChangeStackSize(thisAsStack,  512 );
		else if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_256 )) ChangeStackSize(thisAsStack,  256 );
		else if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_128 )) ChangeStackSize(thisAsStack,  128 );
		else if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_64  )) ChangeStackSize(thisAsStack,   64 );
		else if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_32  )) ChangeStackSize(thisAsStack,   32 );
		else if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_16  )) ChangeStackSize(thisAsStack,   16 );
		else if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_8   )) ChangeStackSize(thisAsStack,    8 );
		else if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_4   )) ChangeStackSize(thisAsStack,    4 );
		else if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_2   )) ChangeStackSize(thisAsStack,    2 );
		else if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_1   )) ChangeStackSize(thisAsStack,    1 );
*/
	}
	
	@Inject(method="areItemsAndComponentsEqual", at= @At("TAIL"), cancellable = true)
	private static void areItemAndComponentsBarMaxStackSizeEqual(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> cir){
		if (cir.getReturnValue()) cir.setReturnValue(true);
		
		int maxCountA = getUnalteredMaxCount(stack);
		int maxCountB = getUnalteredMaxCount(otherStack);
		if ( maxCountA != maxCountB) {
			boolean AgtB = maxCountA>maxCountB;
			ItemStack stack1= AgtB? stack:otherStack;
			ItemStack stack2= AgtB? otherStack:stack;
			int maxCount1= AgtB? maxCountA:maxCountB;
			int maxCount2= AgtB? maxCountB:maxCountA;
			
			if ( stack2.getCount()>= maxCount2){ cir.setReturnValue(false);}
			
			Set<ComponentType<?>> stackComponents1 = stack1.getComponents().getTypes();
			Set<ComponentType<?>> stackComponents2 = stack2.getComponents().getTypes();
			
			ArrayList<ComponentType<?>> dummy1 = new ArrayList<>();
			ArrayList<ComponentType<?>> dummy2 = new ArrayList<>();
			for (ComponentType<?> com : stackComponents1) { if (!com.equals(DataComponentTypes.MAX_STACK_SIZE)) dummy1.add(com); }
			for (ComponentType<?> com : stackComponents2) { if (!com.equals(DataComponentTypes.MAX_STACK_SIZE)) dummy2.add(com); }
			
			
			cir.setReturnValue(stack.isEmpty() && otherStack.isEmpty() ? true : Objects.equals(dummy1, dummy2));
			
		}
	}
	
	@Unique
	private void ChangeStackSize(ItemStack stack, int target){
		if ( MaxStackSizeMayChange(stack,target) && MaxStackSizeNeedsChanged(stack,target) )
			stack.set(DataComponentTypes.MAX_STACK_SIZE, target);
	}
	
	@Unique
	private boolean MaxStackSizeMayChange(ItemStack stack, int max_stack_size_target){
		ComponentMap components = stack.getComponents();
		int count = stack.getCount();
		if (count > max_stack_size_target) return false;
		if (components.contains(DataComponentTypes.CUSTOM_DATA)) return false;
		return true;
	}
	
	@Unique
	private boolean MaxStackSizeNeedsChanged(ItemStack stack, int target){
		int max_stack_size_is = getUnalteredMaxCount(stack);
		int max_stack_size_target = Math.min(target,MAX_STACK_SIZE_CAP);
		return max_stack_size_is != max_stack_size_target;
	}
	
	
	@Unique
	private static int getUnalteredMaxCount(ItemStack stack){
		return stack.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1);
	}
	
	@Unique
	private static void dummy(){}
}
