package hydraheadhunter.datastacks.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class common {
	
	public static ItemStack createDummyStack(ItemStack stack, @Nullable Entity entity){
		ItemStack dummyStack= stack.copyWithCount(1);
		dummyStack.setHolder(entity);
		dummyStack.getMaxCount();
		return dummyStack;
	}
	
}
