package hydraheadhunter.datastacks.mixin.override_cap;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static hydraheadhunter.datastacks.DataDrivenStacks.MAX_STACK_SIZE_CAP;

/**Overrides the hard 99 cap on max_stack_size in one of 4 places*/
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	
	@ModifyArg(method = "method_57371", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;"), index = 1)
	private static int changeMaxStackSizeLimit(int original) { return MAX_STACK_SIZE_CAP; }
	
}
