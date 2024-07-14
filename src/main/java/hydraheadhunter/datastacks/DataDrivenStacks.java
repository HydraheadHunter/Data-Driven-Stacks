package hydraheadhunter.datastacks;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataDrivenStacks implements ModInitializer {
	public static final String MOD_ID = "data_stacks";
	public static final int MAX_STACK_SIZE_CAP = 2048;
	public static final boolean DEBUGGING = false;
	
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Increasing `max_stack_size` cap.");
		LOGGER.info("Implementing Data-Driven max_stack_size");
		
	}

	
}