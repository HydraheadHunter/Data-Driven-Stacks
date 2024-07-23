package hydraheadhunter.datastacks.util;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;


import java.io.InputStream;
import java.util.function.Predicate;

import static hydraheadhunter.datastacks.DataDrivenStacks.*;

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
		for(Identifier id : manager.findResources(MOD_ID, predicate).keySet()) {
			try(InputStream stream = (InputStream) manager.getResource(id).stream()) {
				int streamOut = stream.read();
			} catch(Exception e) {
				LOGGER.error("Error occurred while loading resource json" + id.toString(), e);
			}
		}
	};
	
	}
