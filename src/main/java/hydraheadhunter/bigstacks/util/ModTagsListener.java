package hydraheadhunter.bigstacks.util;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;


import java.io.InputStream;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static hydraheadhunter.bigstacks.BiggerStacks.MOD_ID;
import static hydraheadhunter.bigstacks.BiggerStacks.LOGGER;

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
		for(Identifier id : manager.findResources(MOD_ID, predicate).keySet()) {
			try(InputStream stream = (InputStream) manager.getResource(id).stream()) {
				System.out.println(stream.read());
				// Consume the stream however you want, medium, rare, or well done.
			} catch(Exception e) {
				LOGGER.error("Error occurred while loading resource json" + id.toString(), e);
			}
		}
		test=2;
	};
}
