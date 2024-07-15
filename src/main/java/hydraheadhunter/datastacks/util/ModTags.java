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

public class ModTags {
	public static class Items {
		public static final Map<Integer, TagKey<Item>> STACK_SIZES;
		
		static {
			Map<Integer, TagKey<Item>> tmpMap = new HashMap<>();
			for(int ii = 1;  ii <= MAX_STACK_SIZE_CAP; ii+=1) {
				int stackSize = /*1 <<*/ ii;
				String tagName = "max_stack_size_" + stackSize;
				tmpMap.put(stackSize, createTag(tagName));
				addReloadListener(tagName);
			}
			STACK_SIZES = Collections.unmodifiableMap(tmpMap);
		}
		
		public static TagKey<Item> createTag(String name){
			return TagKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
			
		}
		
		static {
			addReloadListeners();
		}
		
		public static void addReloadListeners(){
			addReloadListener("stack_size_1"   );
			addReloadListener("stack_size_2"   );
			addReloadListener("stack_size_4"   );
			addReloadListener("stack_size_8"   );
			addReloadListener("stack_size_16"  );
			addReloadListener("stack_size_32"  );
			addReloadListener("stack_size_64"  );
			addReloadListener("stack_size_128" );
			addReloadListener("stack_size_256" );
			addReloadListener("stack_size_512" );
			addReloadListener("stack_size_1024");
			addReloadListener("stack_size_2048");
		}
		private static void addReloadListener(String path){
			ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener( new ModTagsListener( path ));
		}
		
	}
}
