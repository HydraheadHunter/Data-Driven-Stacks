package hydraheadhunter.bigstacks.mixin;

import hydraheadhunter.bigstacks.util.ModTags;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static hydraheadhunter.bigstacks.BiggerStacks.MAX_STACK_SIZE_CAP;

@Mixin(ItemStack.class)
public abstract class ItemStack_SizeMixin implements ComponentHolder, FabricItemStack {
	
	@ModifyArg(method = "method_57371",  at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;"), index = 1)
	private static int changeMaxStackSizeLimit(int original) {
		return MAX_STACK_SIZE_CAP;
	}
	
	@Inject(method="getMaxCount", at = @At("HEAD"))
	private void updateMaxStackSizeWithTag(CallbackInfoReturnable<Integer> cir){
		ItemStack thisAsStack = (ItemStack)(Object) this;
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_1   ) && StackSizeMustChange(thisAsStack,    1) ) ChangeStackSize(thisAsStack,    1);
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_2   ) && StackSizeMustChange(thisAsStack,    2) ) ChangeStackSize(thisAsStack,    2);
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_4   ) && StackSizeMustChange(thisAsStack,    4) ) ChangeStackSize(thisAsStack,    4);
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_8   ) && StackSizeMustChange(thisAsStack,    8) ) ChangeStackSize(thisAsStack,    8);
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_16  ) && StackSizeMustChange(thisAsStack,   16) ) ChangeStackSize(thisAsStack,   16);
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_32  ) && StackSizeMustChange(thisAsStack,   32) ) ChangeStackSize(thisAsStack,   32);
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_64  ) && StackSizeMustChange(thisAsStack,   64) ) ChangeStackSize(thisAsStack,   64);
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_128 ) && StackSizeMustChange(thisAsStack,  128) ) ChangeStackSize(thisAsStack,  128);
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_256 ) && StackSizeMustChange(thisAsStack,  256) ) ChangeStackSize(thisAsStack,  256);
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_512 ) && StackSizeMustChange(thisAsStack,  512) ) ChangeStackSize(thisAsStack,  512);
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_1024) && StackSizeMustChange(thisAsStack, 1024) ) ChangeStackSize(thisAsStack, 1024);
		if ( thisAsStack.isIn(ModTags.Items.IS_STACK_SIZE_2048) && StackSizeMustChange(thisAsStack, 2048) ) ChangeStackSize(thisAsStack, 2048);
		
	}
	
	@Unique
	private boolean StackSizeMustChange(ItemStack stack, int target){
		ComponentMap components = stack.getComponents();
		int max_stack_size_is = components.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, target);
		int max_stack_size_target = Math.min(target,MAX_STACK_SIZE_CAP);
		
		return StackSizeMayChange(stack, max_stack_size_target) && max_stack_size_is < max_stack_size_target;
	}
	
	@Unique
	private boolean StackSizeMayChange(ItemStack stack, int max_stack_size_target){
		ComponentMap components = stack.getComponents();
		int count = stack.getCount();
		if (count > max_stack_size_target) return false;
		if (components.contains(DataComponentTypes.CUSTOM_DATA)) return false;
		return true;
	}
	
	@Unique
	private void ChangeStackSize(ItemStack stack, int target){
			ComponentChanges.Builder componentChangesBuilder = ComponentChanges.builder();
			componentChangesBuilder.add(DataComponentTypes.MAX_STACK_SIZE, target);
			ComponentChanges componentChanges = componentChangesBuilder.build();
			
			stack.applyChanges(componentChanges);
	}
	
}
