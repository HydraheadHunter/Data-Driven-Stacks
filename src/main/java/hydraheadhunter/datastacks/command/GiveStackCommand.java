package hydraheadhunter.datastacks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
		int i = itemStack.getMaxCount();
		if (count > MAX_STACKS) {
			source.sendError(Text.translatable("commands.give.failed.toomanyitems", i, itemStack.toHoverableText()));
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

			if (targets.size() == 1) {
				source.sendFeedback(
					() -> Text.translatable(
							"commands.give.success.single", count, itemStack.toHoverableText(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
						),
					true
				);
			} else {
				source.sendFeedback(() -> Text.translatable("commands.give.success.single", count, itemStack.toHoverableText(), targets.size()), true);
			}

			return targets.size();
		}
	}
	
	
}
