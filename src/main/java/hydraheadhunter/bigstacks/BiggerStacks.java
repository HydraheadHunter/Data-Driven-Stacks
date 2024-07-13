package hydraheadhunter.bigstacks;

import net.fabricmc.api.ModInitializer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BiggerStacks implements ModInitializer {
	public static final String MOD_ID = "bigger_stacks";
	public static final int MAX_STACK_SIZE_CAP = 2048;
	public static final String EMPTY = "";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Increasing Maximum `max_stack_size`");
		
	}
	
	public static String join       ( String @NotNull ... strings){
		String toReturn = "";
		for ( String str : strings)
			if (!str.equals(EMPTY)) toReturn = str.equals(strings[0]) ? strings[0]:String.join(".", toReturn, str) ;
		return toReturn;
	}
	public static String joinSlash       ( String @NotNull ... strings){
		String toReturn = "";
		for ( String str : strings)
			if (!str.equals(EMPTY)) toReturn = str.equals(strings[0]) ? strings[0]:String.join("/", toReturn, str) ;
		return toReturn;
	}
	
}