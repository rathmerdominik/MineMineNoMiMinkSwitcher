package net.hammerclock.minkswitch.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.hammerclock.minkswitch.MinkSwitch;
import net.hammerclock.minkswitch.config.CommonConfig;
import net.hammerclock.minkswitch.entity.IMinkSwitcher;
import net.hammerclock.minkswitch.entity.MinkSwitcherCapability;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.server.command.EnumArgument;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;
import xyz.pixelatedw.mineminenomi.data.entity.entitystats.EntityStatsCapability;
import xyz.pixelatedw.mineminenomi.data.entity.entitystats.IEntityStats;
import xyz.pixelatedw.mineminenomi.packets.server.SSyncAbilityDataPacket;
import xyz.pixelatedw.mineminenomi.packets.server.SSyncEntityStatsPacket;
import xyz.pixelatedw.mineminenomi.wypi.WyNetwork;

public class MinkSwitchCommand {
	public static final Logger LOGGER = LogManager.getLogger(MinkSwitch.PROJECT_ID);

	public MinkSwitchCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("mink_switch")
						.executes(MinkSwitchCommand::minkSwitchCui)
						.then(Commands.literal("i_am_not_a_furry_but_i_am_gay")
								.requires(context -> {
									// little easter egg ;} dont be mad
									return context.hasPermission(4);
								})
								.executes(MinkSwitchCommand::iAmNotAFurry))
						.then(Commands.argument("mink_type", EnumArgument.enumArgument(MINK_TYPE.class))
								.executes(MinkSwitchCommand::switchMinkType)
								.then(Commands.argument("player", EntityArgument.player())
										.executes(MinkSwitchCommand::switchMinkTypeWithPlayer))));
	}

	private enum MINK_TYPE {
		DOG("mink_dog"), // Brown
		BUNNY("mink_bunny"), // LIGHT PURPLE
		LION("mink_lion"); // Orange

		private final String mappedValue;

		MINK_TYPE(String mappedValue) {
			this.mappedValue = mappedValue;
		}

		public String getMappedValue() {
			return mappedValue;
		}
	}

	public static int iAmNotAFurry(CommandContext<CommandSource> source) {
		LOGGER.fatal("You are a furry and you are indeed gay!");

		return -1;
	}

	public static int minkSwitchCui(CommandContext<CommandSource> source) {
		StringTextComponent switchCui = new StringTextComponent("Select your Mink Type: ");
		TranslationTextComponent dogClick = new TranslationTextComponent("[Dog] ");
		TranslationTextComponent bunnyClick = new TranslationTextComponent("[Bunny] ");
		TranslationTextComponent lionClick = new TranslationTextComponent("[Lion] ");

		// TODO This is absolute cancer. Can i simplify this? Needs research
		dogClick.setStyle(
				Style.EMPTY.withClickEvent(
						new ClickEvent(
								ClickEvent.Action.RUN_COMMAND,
								String.format("/mink_switch DOG")))
						.withColor(TextFormatting.BLUE));
		bunnyClick.setStyle(
				Style.EMPTY.withClickEvent(
						new ClickEvent(
								ClickEvent.Action.RUN_COMMAND,
								String.format("/mink_switch BUNNY")))
						.withColor(TextFormatting.LIGHT_PURPLE));
		lionClick.setStyle(
				Style.EMPTY.withClickEvent(
						new ClickEvent(
								ClickEvent.Action.RUN_COMMAND,
								String.format("/mink_switch LION")))
						.withColor(TextFormatting.GOLD));

		switchCui.append(dogClick).append(bunnyClick).append(lionClick);
		source.getSource().sendSuccess(switchCui, true);

		return 0;
	}

	public static int switchMinkType(CommandContext<CommandSource> source) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getSource().getPlayerOrException();

		return switchMink(source, player);
	}

	public static int switchMinkTypeWithPlayer(CommandContext<CommandSource> source) throws CommandSyntaxException {
		ServerPlayerEntity player = EntityArgument.getPlayer(source, "player");

		if (!player.hasPermissions(2)) {
			source.getSource()
					.sendFailure(new StringTextComponent(
							"You do not have permission to execute this command with a player argument!"));
			return -1;
		}

		return switchMink(source, player);
	}

	public static int switchMink(CommandContext<CommandSource> source, ServerPlayerEntity player) {
		MINK_TYPE minkType = source.getArgument("mink_type", MINK_TYPE.class);
		String stringMinkType = minkType.getMappedValue();

		IEntityStats entityProps = EntityStatsCapability.get(player);
		IAbilityData abilityProps = AbilityDataCapability.get(player);
		IMinkSwitcher minkProps = MinkSwitcherCapability.get(player);

		if (!entityProps.isMink()) {
			source.getSource()
					.sendFailure(new StringTextComponent("You are not a mink! There is nothing to change."));
			return -1;
		}

		if (!CommonConfig.INSTANCE.getSwitchAgain() && minkProps.getMinkSwitched() && !player.hasPermissions(2)) {
			source.getSource().sendFailure(
					new StringTextComponent("Switching Mink race a second time is disabled in the server config!"));
			return -1;
		}

		entityProps.setSubRace(stringMinkType);
		minkProps.setMinkSwitched(true);

		WyNetwork.sendToAllTrackingAndSelf(new SSyncEntityStatsPacket(player.getId(), entityProps), player);
		WyNetwork.sendTo(new SSyncAbilityDataPacket(player.getId(), abilityProps), (ServerPlayerEntity) player);

		return 0;
	}
}
