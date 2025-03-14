package hydraheadhunter.datastacks.mixin;

import hydraheadhunter.datastacks.DataDrivenStacks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Slot.class)
public abstract class SlotMixin {
	/*
	@Final @Shadow	public Inventory inventory;
	@Unique
	Logger LOGGER= DataDrivenStacks.LOGGER;
	
	@Inject(method="getMaxItemCount(Lnet/minecraft/item/ItemStack;)I", at=@At("HEAD"))
	private void injectGetMaxItemCount(ItemStack stack, CallbackInfoReturnable<Integer> cir){
		Entity slotOwner = inventory instanceof PlayerInventory ? ((PlayerInventory) inventory).player:null;
		stack.setHolder(slotOwner);
		stack.getMaxCount();
		LOGGER.info("DEBUG " + (inventory instanceof PlayerInventory?"PLAYER":"NULL") );
	}*/
}



	


