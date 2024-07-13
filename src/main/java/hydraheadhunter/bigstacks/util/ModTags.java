package hydraheadhunter.bigstacks.util;


import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;


import static hydraheadhunter.bigstacks.BiggerStacks.MOD_ID;

public class ModTags {
	public static class Items {
		public static final TagKey<Item> IS_STACK_SIZE_1		= createTag("stack_size_1"	);
		public static final TagKey<Item> IS_STACK_SIZE_2		= createTag("stack_size_2"	);
		public static final TagKey<Item> IS_STACK_SIZE_4		= createTag("stack_size_4"	);
		public static final TagKey<Item> IS_STACK_SIZE_8		= createTag("stack_size_8"	);
		public static final TagKey<Item> IS_STACK_SIZE_16		= createTag("stack_size_16"	);
		public static final TagKey<Item> IS_STACK_SIZE_32		= createTag("stack_size_32"	);
		public static final TagKey<Item> IS_STACK_SIZE_64		= createTag("stack_size_64"	);
		public static final TagKey<Item> IS_STACK_SIZE_128	= createTag("stack_size_128"	);
		public static final TagKey<Item> IS_STACK_SIZE_256	= createTag("stack_size_256"	);
		public static final TagKey<Item> IS_STACK_SIZE_512	= createTag("stack_size_512"	);
		public static final TagKey<Item> IS_STACK_SIZE_1024	= createTag("stack_size_1024"	);
		public static final TagKey<Item> IS_STACK_SIZE_2048	= createTag("stack_size_2048"	);
		
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
