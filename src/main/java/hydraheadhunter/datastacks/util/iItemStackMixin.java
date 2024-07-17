package hydraheadhunter.datastacks.util;

import com.sun.jdi.InterfaceType;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static hydraheadhunter.datastacks.DataDrivenStacks.MAX_STACK_SIZE_CAP;
import static hydraheadhunter.datastacks.DataDrivenStacks.MOD_ID;

public interface iItemStackMixin  {
	public void setInventoryType( Class<?> inventoryType);

}