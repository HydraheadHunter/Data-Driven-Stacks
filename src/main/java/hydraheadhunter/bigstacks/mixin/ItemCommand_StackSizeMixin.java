package hydraheadhunter.bigstacks.mixin;

import net.minecraft.server.command.ItemCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static hydraheadhunter.bigstacks.BiggerStacks.MAX_STACK_SIZE_CAP;

@Mixin(ItemCommand.class)
public abstract class ItemCommand_StackSizeMixin {
	
	
	@ModifyConstant( method = "register", constant = @Constant(intValue = 99))
	private static int changeMaxStackSizeLimit(int original) {
		return MAX_STACK_SIZE_CAP;
	}
	
}
