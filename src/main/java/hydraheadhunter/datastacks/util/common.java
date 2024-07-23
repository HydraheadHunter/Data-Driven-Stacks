package hydraheadhunter.datastacks.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**Common methods shared by multiple classes*/
public class common {
	
	/** Makes a copy of the @param stack with count of 1 and the @nullable @param entity as its holder.
	 * Returns that copy.
	 */
	public static ItemStack createDummyStack(ItemStack stack, @Nullable Entity entity){
		ItemStack dummyStack= stack.copyWithCount(1);
		dummyStack.setHolder(entity);
		dummyStack.getMaxCount();
		return dummyStack;
	}
	
}
