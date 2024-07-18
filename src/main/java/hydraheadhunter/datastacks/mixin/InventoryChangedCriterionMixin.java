package hydraheadhunter.datastacks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ItemCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static hydraheadhunter.datastacks.DataDrivenStacks.LOGGER;
import static hydraheadhunter.datastacks.DataDrivenStacks.MAX_STACK_SIZE_CAP;

@Mixin(InventoryChangedCriterion.class)
public abstract class InventoryChangedCriterionMixin {
	/*
	@Inject(method="trigger", at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I"))
	private void injectTrigger(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack, CallbackInfo info, @Local(ordinal=1) ItemStack itemStack) {
		itemStack.setHolder(inventory.player);
		//LOGGER.info("injectTrigger");
	}*/

}
