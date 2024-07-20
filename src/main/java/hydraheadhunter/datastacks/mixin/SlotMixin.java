package hydraheadhunter.datastacks.mixin;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static hydraheadhunter.datastacks.DataDrivenStacks.LOGGER;
import static hydraheadhunter.datastacks.util.common.createDummyStack;
import static java.lang.String.valueOf;

@Mixin(Slot.class)
public abstract class SlotMixin {
	
	@Shadow @Final public Inventory inventory;
	
	@Shadow public abstract ItemStack getStack();
	
	@Inject(method="canInsert", at= @At("TAIL"), cancellable = true)
		private void overrideCanInsertForOverFullStacks(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
		if( !cir.getReturnValueZ()) return;
		//LOGGER.info("So true worstie");
		
		PlayerEntity player= this.inventory instanceof PlayerInventory? ((PlayerInventory) this.inventory).player:null;
		ItemStack dummyStack= createDummyStack(stack,player);
		int dummyStackSize= dummyStack.getMaxCount();
		int stackSize= stack.getMaxCount();
		if (stackSize == dummyStackSize) return;
		if (stack.getCount() <= dummyStackSize) return;
		
		ItemStack slotStack= this.getStack();
		if (slotStack.isEmpty() || stack.isEmpty()) return;
		cir.setReturnValue(false);
	}
	
	@ModifyArg(method="insertStack(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;",at=@At(value="INVOKE", target="Lnet/minecraft/screen/slot/Slot;getMaxItemCount(Lnet/minecraft/item/ItemStack;)I"), index= 0)
		private ItemStack injectInsertStack( ItemStack stack){
		PlayerEntity player= this.inventory instanceof PlayerInventory? ((PlayerInventory) this.inventory).player:null;
		ItemStack dummyStack= createDummyStack(stack,player);
		return dummyStack;
	}//*/
	
	
}



	


