package hydraheadhunter.datastacks.mixin.override_cap;

import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static hydraheadhunter.datastacks.DataDrivenStacks.MAX_STACK_SIZE_CAP;

/**Overrides the hard 99 cap on max_stack_size in one of 4 places*/
@Mixin(Inventory.class)
public interface InventoryMixin {
	
	@ModifyConstant( method = "getMaxCountPerStack", constant = @Constant(intValue = 99))
	private int changeMaxStackSizeLimit(int original) {
		return MAX_STACK_SIZE_CAP;
	}
	
}
