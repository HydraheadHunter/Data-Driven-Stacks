package hydraheadhunter.datastacks.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HandledScreen.class)
public class HandedScreenMixin {
	/*@Unique
	Logger LOGGER = DataDrivenStacks.LOGGER;
	
	@Final @Shadow	protected Set<Slot> cursorDragSlots;
	
	@Inject(method="calculateOffset", at=@At("HEAD"))
	private void injectDrawSlot(CallbackInfo ci){
		LOGGER.info("CALCULATE_OFFSET_INJECTED");
		Object[] slots = cursorDragSlots.toArray();
		for (Object obj : slots){
			Slot slot= (Slot) obj;
			LOGGER.info( slot.toString());
		}
	}
	@ModifyVariable(method="calculateOffset", at= @At("STORE"), ordinal = 0)
	private int override_I(int ii, @Local(ordinal=0) ItemStack cursorStack, @Local(ordinal=1) ItemStack slotStack,  @Local(ordinal = 0) Slot slot){
		//LOGGER.info( "Slot#:"+slot.getIndex());
		//LOGGER.info( valueOf("I: "+ii));
		//Entity slotOwner = slot.inventory instanceof PlayerInventory ? ((PlayerInventory)slot.inventory).player:null;
		//int dummyMax= createDummyStack(stack,slotOwner).getMaxCount();
		return ii;
	}
	@ModifyVariable(method="calculateOffset", at= @At("STORE"), ordinal = 1)
	private int override_K(int kk, @Local(ordinal=0) ItemStack cursorStack, @Local(ordinal=1) ItemStack slotStack,  @Local(ordinal = 0) Slot slot){
		//LOGGER.info( valueOf("K: " +kk));
		//Entity slotOwner = slot.inventory instanceof PlayerInventory ? ((PlayerInventory)slot.inventory).player:null;
		//int dummyMax= createDummyStack(stack,slotOwner).getMaxCount();
		return kk;
	}
	
	@ModifyVariable(method="calculateOffset", at= @At("STORE"), ordinal = 2)
	private int override_J(int jj, @Local(ordinal=0) ItemStack cursorStack, @Local(ordinal=1) ItemStack slotStack,  @Local(ordinal = 0) Slot slot){
		//LOGGER.info( valueOf("J: "+jj));
		//Entity slotOwner = slot.inventory instanceof PlayerInventory ? ((PlayerInventory)slot.inventory).player:null;
		//int dummyMax= createDummyStack(stack,slotOwner).getMaxCount();
		return jj;
	}
	*/
}
