package hydraheadhunter.datastacks.mixin;

import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static hydraheadhunter.datastacks.DataDrivenStacks.MAX_STACK_SIZE_CAP;

@Mixin(Inventory.class)
public interface Inventory_StackSizeMixin {
	
	@ModifyConstant( method = "getMaxCountPerStack", constant = @Constant(intValue = 99))
	private int changeMaxStackSizeLimit(int original) {
		return MAX_STACK_SIZE_CAP;
	}
	
}
