package hydraheadhunter.datastacks.util;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
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

/**All the mod tags this mod introduces*/
public class ModTags {
	
	public static class Items {
		
		
		public static final TagKey<Item> VILLAGER_LESS = createTag("control/villagers_stack_less");
		public static final TagKey<Item> VILLAGER_MORE = createTag("control/villagers_stack_more");
		
		public static final TagKey<Item> PLAYER_LESS   = createTag("control/players_stack_less");
		public static final TagKey<Item> PLAYER_MORE   = createTag("control/players_stack_more");
		
		public static final TagKey<Item> MAX_STACK_SIZE_0= createTag("max_stack_size_0");
		public static final Map<Integer, TagKey<Item>> STACK_SIZES;
		
		static {
			Map<Integer, TagKey<Item>> tmpMap = new HashMap<>();
			for(int ii = 1;  ii <= MAX_STACK_SIZE_CAP; ii+=1) {
				int stackSize = ii; // 1 <<
				String tagName = "max_stack_size_" + stackSize;
				tmpMap.put(stackSize, createTag(tagName));
				addReloadListener(tagName);
			}
			STACK_SIZES = Collections.unmodifiableMap(tmpMap);
		}
		
		public static TagKey<Item> createTag(String name){ return TagKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));	}
		
		static {	addReloadListeners();
		}
		
		public static void addReloadListeners(){
			addReloadListener( "control/villagers_stack_less"	);
			addReloadListener( "control/villagers_stack_more"	);
			addReloadListener( "control/players_stack_less"	);
			addReloadListener( "control/players_stack_more"	);
			addReloadListener( "max_stack_size_0"	);
		}
		private static void addReloadListener(String path){
			ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener( new ModTagsListener( path ));
		}
		
	}
}
