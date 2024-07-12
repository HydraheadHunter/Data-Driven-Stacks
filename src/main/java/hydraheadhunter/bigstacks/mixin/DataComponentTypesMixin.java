package hydraheadhunter.bigstacks.mixin;

import net.minecraft.component.DataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static hydraheadhunter.bigstacks.BiggerStacks.MAX_STACK_SIZE_CAP;

@Mixin(DataComponentTypes.class)
public abstract class DataComponentTypesMixin {

	
	@ModifyArg(method = "method_58570",  at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;"), index = 1)
	private static int changeMaxStackSizeLimit(int original) {
		return MAX_STACK_SIZE_CAP;
	}
	/*
	@Accessor(value="MAX_STACK_SIZE")
	public static ComponentType<Integer> setMaxStackSizeMax(CallbackInfo info){
		
		return DataComponentTypesInvoker.invokeRegister("max_stack_size", (builder) -> {
			return builder.codec(Codecs.rangedInt(1, 99)).packetCodec(PacketCodecs.VAR_INT);
			
		});
		
	}*/
		/*(builder) ->{
		return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.ofVanilla("max_stack_size"), ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());};
		register("max_stack_size", (builder) -> builder.codec(Codecs.rangedInt(1, 99)).packetCodec(PacketCodecs.VAR_INT);
	}*/
	
	/* return Registry.register(Registries.DATA_COMPONENT_TYPE, id, ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build()); */
	/*
	public static final ComponentType<Integer> MAX_STACK_SIZE = register("max_stack_size", (builder) -> {
          return builder.codec(Codecs.rangedInt(1, 99)).packetCodec(PacketCodecs.VAR_INT);
          
     });register("max_stack_size", (builder) -> {
          return builder.codec(Codecs.rangedInt(1, 99)).packetCodec(PacketCodecs.VAR_INT);
          
     });*/
}
