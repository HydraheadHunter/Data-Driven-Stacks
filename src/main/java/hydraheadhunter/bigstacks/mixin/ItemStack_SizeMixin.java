package hydraheadhunter.bigstacks.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static hydraheadhunter.bigstacks.BiggerStacks.MAX_STACK_SIZE_CAP;

@Mixin(ItemStack.class)
public abstract class ItemStack_SizeMixin {
	
	@ModifyArg(method = "method_57371",  at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;"), index = 1)
	private static int changeMaxStackSizeLimit(int original) {
		return MAX_STACK_SIZE_CAP;
	}
	
}
