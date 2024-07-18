package hydraheadhunter.datastacks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Collection;

public class GiveStackCommand {
	public static final int MAX_STACKS = 100;
	
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
		dispatcher.register(
			CommandManager.literal("give")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess))
								.then(
									CommandManager.argument("count", IntegerArgumentType.integer(1,100))
									.then(CommandManager.literal("stack")
									.executes(
									context -> execute(
									context.getSource(),
									ItemStackArgumentType.getItemStackArgument(context, "item"),
									EntityArgumentType.getPlayers(context, "targets"),
									IntegerArgumentType.getInteger(context, "count")
									)
										)
									)
								)
						)
					)
				);
				
	}

	private static int execute(ServerCommandSource source, ItemStackArgument item, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
		ItemStack itemStack = item.createStack(1, false);
		if (targets.size()>0) itemStack.setHolder((Entity)targets.toArray()[0]);
		int i = itemStack.getMaxCount();
		
		if (count > MAX_STACKS) {
			source.sendError(Text.translatable("commands.give.failed.toomanyitems.stacks", 100, itemStack.toHoverableText()));
			return 0;
		} else {
			for (ServerPlayerEntity serverPlayerEntity : targets) {
				int k = count*itemStack.getMaxCount();

				while (k > 0) {
					int l = Math.min(i, k);
					k -= l;
					ItemStack itemStack2 = item.createStack(l, false);
					boolean bl = serverPlayerEntity.getInventory().insertStack(itemStack2);
					if (bl && itemStack2.isEmpty()) {
						ItemEntity itemEntity = serverPlayerEntity.dropItem(itemStack, false);
						if (itemEntity != null) {
							itemEntity.setDespawnImmediately();
						}

						serverPlayerEntity.getWorld()
							.playSound(
								null,
								serverPlayerEntity.getX(),
								serverPlayerEntity.getY(),
								serverPlayerEntity.getZ(),
								SoundEvents.ENTITY_ITEM_PICKUP,
								SoundCategory.PLAYERS,
								0.2F,
								((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
							);
						serverPlayerEntity.currentScreenHandler.sendContentUpdates();
					} else {
						ItemEntity itemEntity = serverPlayerEntity.dropItem(itemStack2, false);
						if (itemEntity != null) {
							itemEntity.resetPickupDelay();
							itemEntity.setOwner(serverPlayerEntity.getUuid());
						}
					}
				}
			}
			
			source.sendFeedback( () -> GenerateFeedback(targets.size(), count, itemStack, targets), true );
			return targets.size();
		}
	}
	
	private static MutableText GenerateFeedback(int size, int count, ItemStack stack, Collection<ServerPlayerEntity> targets  ){
		int switchValue=  (count==1?0:1)+ (size==1?0:2) ;
		return switch ( switchValue ){
			case 0 -> Text.translatable( "commands.give.success.single.stack"   ,        stack.toHoverableText(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()	);
			case 1 -> Text.translatable( "commands.give.success.single.stacks"  , count, stack.toHoverableText(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()	);
			case 2 -> Text.translatable( "commands.give.success.multiple.stack" ,        stack.toHoverableText(), size													);
			case 3 -> Text.translatable( "commands.give.success.multiple.stacks", count, stack.toHoverableText(), size													);
			default -> null; //This is just to satisfy the compiler. It's unreachable.
		};
	}
	
}
