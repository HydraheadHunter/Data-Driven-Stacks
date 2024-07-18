package hydraheadhunter.datastacks.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
/*
	@Shadow @Final public PlayerEntity player;
	@Shadow @Final private List<DefaultedList<ItemStack>> combinedInventory;
	
	@ModifyArg(method = "canStackAddMore",  at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getMaxCount(Lnet/minecraft/item/ItemStack;)I"), index = 0)
	private ItemStack addPlayerHolder_1 (ItemStack stack) {
		stack.setHolder(player);
		return stack;
	}
	
	@ModifyArg(method = "addStack(ILnet/minecraft/item/ItemStack;)I",  at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getMaxCount(Lnet/minecraft/item/ItemStack;)I"), index = 0)
	private ItemStack addPlayerHolder_2 (ItemStack stack) {
		stack.setHolder(player);
		return stack;
	}

	@Inject(method= "offer", at = @At("Head"))
	private void addPlayerHolder_3( ItemStack stack, boolean notifiesClient, CallbackInfo info){
		stack.setHolder(player);
	}

	@Inject(method= "getStack", at= @At("TAIL"), cancellable = true )
	private void injectGetStack( int slot, CallbackInfoReturnable cir, @Local List<ItemStack> list){
		if (list == null) return;
		ItemStack item = list.get(slot);
		
		if (item.isOf(Items.AIR)) return;
		
		item.setHolder(player);
		cir.setReturnValue(item);
	}
//*/

}

