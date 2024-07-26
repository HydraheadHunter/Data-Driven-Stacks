package hydraheadhunter.datastacks.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
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
		if( !cir.getReturnValueZ())
			return;
LOGGER.info("So true worstie");
	
		PlayerEntity player= this.inventory instanceof PlayerInventory? ((PlayerInventory) this.inventory).player:null;
LOGGER.info( player!= null? player.getName().toString():"null");
		
		ItemStack dummyStack= createDummyStack(stack,player);
		
		int dummyStackSize= dummyStack.getMaxCount();
		int stackSize= stack.getMaxCount();
LOGGER.info("d:"+dummyStackSize + ", c:"+ stackSize);
		
		if (stackSize == dummyStackSize) return;
		if (stack.getCount() < dummyStackSize) return;
LOGGER.info("illegal Stack");
		
		ItemStack slotStack= this.getStack();
		if (slotStack.isEmpty() || stack.isEmpty()) return;
		
		cir.setReturnValue(false);
	
	}//*/
	/*
	@ModifyArg(method="insertStack(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;",at=@At(value="INVOKE", target="Lnet/minecraft/screen/slot/Slot;getMaxItemCount(Lnet/minecraft/item/ItemStack;)I"), index= 0)
		private ItemStack injectInsertStack( ItemStack stack){
		PlayerEntity player= this.inventory instanceof PlayerInventory? ((PlayerInventory) this.inventory).player:null;
		ItemStack dummyStack= createDummyStack(stack,player);
		return dummyStack;
		return stack;
	}//*/
	
	@Inject(method="insertStack(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;", at=@At("HEAD"))
	private void injectInsertStack(ItemStack stack, int count, CallbackInfoReturnable<ItemStack> cir){
		LOGGER.info("Inject InsertStack");
	}
	/*
	@Redirect(method="insertStack(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;", at= @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;canInsert(Lnet/minecraft/item/ItemStack;)Z"))
	private boolean injectInsertStack(Slot instance, ItemStack stack, @Local(argsOnly = true) int count){
	/*	boolean vanilla_canInsert= instance.canInsert(stack);
		if (!vanilla_canInsert) return false;
	//LOGGER.info("So true worstie");
		
		PlayerEntity player= catchPlayer();
	//LOGGER.info( player!= null? player.getName().toString():"null");
		
		ItemStack dummyStack= createDummyStack(stack,player);
		
		int dummyStackSize= dummyStack.getMaxCount();
		int stackSize= stack.getMaxCount();
	//LOGGER.info("d:"+dummyStackSize + ", c:"+ stackSize);
		
		if (stackSize == dummyStackSize) return true;
		if (stack.getCount() <= dummyStackSize) return true;
	//LOGGER.info("illegal Stack");
		
		ItemStack slotStack= this.getStack();
		if (slotStack.isEmpty() || stack.isEmpty()) return true;
	//LOGGER.info("RETURNING FALSE");
	//LOGGER.info("");
		return false;
	}//*/
	
	@ModifyVariable(method="insertStack(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;", at=@At("STORE"), name="i")
	private int capIatDummyMaxStackSize( int i, @Local(name="stack") ItemStack stack) {
		PlayerEntity player= catchPlayer();
		if (i<=0){
			this.getStack().setHolder(player);
			int test1= this.getStack().getCount();
			int test2= this.getStack().getMaxCount();
			if( test1<test2){
				return Math.min(stack.getCount(), test2-test1);
			}
		}
		
		return Math.min( i, createDummyStack(stack,player).getMaxCount() );
	}
	
	@Inject(method="insertStack(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;", at=@At("TAIL"))
	private void injectTail(ItemStack stack, int count, CallbackInfoReturnable<ItemStack> cir){
	
		this.getStack().setHolder(catchPlayer());
		this.getStack().getMaxCount();
	}
	
	@Unique @Nullable
	private PlayerEntity catchPlayer(){
		return this.inventory instanceof PlayerInventory ? ((PlayerInventory) this.inventory).player:null;
	}
	
}



	


