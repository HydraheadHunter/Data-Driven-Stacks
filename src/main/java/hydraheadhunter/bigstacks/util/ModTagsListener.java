package hydraheadhunter.bigstacks.util;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;


import java.util.function.Predicate;
import java.util.stream.Stream;

import static hydraheadhunter.bigstacks.BiggerStacks.MOD_ID;

public class ModTagsListener implements SimpleSynchronousResourceReloadListener {
	public  final String path;
	private final Predicate<Identifier> predicate = new Predicate<Identifier>() {
		@Override
		public boolean test(Identifier identifier) {
			return true;
		}
	};
	
	public ModTagsListener( String path) {
		this.path = path;
	}
	
	@Override
	public Identifier getFabricId() {
		return Identifier.of(MOD_ID, path);
	}
	
	@Override
	public void reload(ResourceManager manager) {
		int test= 1;
		Stream<Resource> streamedResources= manager.getAllResources( Identifier.of(MOD_ID,path)).stream();
		streamedResources.count();
		System.out.println();

	};
}
