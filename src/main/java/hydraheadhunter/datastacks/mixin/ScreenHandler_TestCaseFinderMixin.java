package hydraheadhunter.datastacks.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static hydraheadhunter.datastacks.DataDrivenStacks.MAX_STACK_SIZE_CAP;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandler_TestCaseFinderMixin {
	
//	@Inject(method = "insertItem",  at = @At(value = "HEAD"))
//	private void findTes(ItemStack stack, int startIndex, int endIndex, boolean fromLast, CallbackInfoReturnable<Boolean> cir) {
//		System.out.println("Triggered");
//	}
	
}
