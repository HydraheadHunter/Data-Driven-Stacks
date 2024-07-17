package hydraheadhunter.datastacks.mixin;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin extends PassiveEntity {
	@Shadow SimpleInventory inventory;
	
	@Shadow @Final private static Logger LOGGER;
	
	protected MerchantEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
		super(entityType, world);
	}
	
	@Inject(method= "getInventory", at= @At("Head"))
	private void injectInventory(CallbackInfoReturnable cir){
		if (inventory != null) {
			for( ItemStack stack : inventory.heldStacks ) {
				if ( !stack.isEmpty() && stack.getHolder()==null ) {
					stack.setHolder((Entity) this);
				}
			}
		}
	}
}

