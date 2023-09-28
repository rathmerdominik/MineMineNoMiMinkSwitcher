package com.derhammerclock.minkswitch.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.derhammerclock.minkswitch.MinkSwitch;
import com.derhammerclock.minkswitch.config.CommonConfig;
import com.derhammerclock.minkswitch.entity.IMinkSwitcher;
import com.derhammerclock.minkswitch.entity.MinkSwitcherCapability;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
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
	private static final Logger LOGGER = LogManager.getLogger(MinkSwitch.PROJECT_ID);

	public MinkSwitchCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("mink_switch")
						.then(Commands.argument("mink_type", EnumArgument.enumArgument(MINK_TYPE.class))
								.executes(MinkSwitchCommand::switchMinkType)));
		dispatcher.register(
				Commands.literal("mink_switch_cui").executes(MinkSwitchCommand::minkSwitchCui));
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

	public static int switchMinkType(CommandContext<CommandSource> source) {
		MINK_TYPE minkType = source.getArgument("mink_type", MINK_TYPE.class);
		String stringMinkType = minkType.getMappedValue();

		try {
			ServerPlayerEntity player = source.getSource().getPlayerOrException();
			IEntityStats entityProps = EntityStatsCapability.get(player);
			IAbilityData abilityProps = AbilityDataCapability.get(player);
			IMinkSwitcher minkProps = MinkSwitcherCapability.get(player);

			// For whatever reason entityProps.isMink() does not work. Wynd pls fix
			if (!entityProps.isMink()) {
				source.getSource()
						.sendFailure(new StringTextComponent("You are not a mink! There is nothing to change."));
				return -1;
			}

			if (!CommonConfig.INSTANCE.getSwitchAgain() && minkProps.getMinkSwitched()) {
				source.getSource().sendFailure(
						new StringTextComponent("Switching Mink race a second time is disabled in the server config!"));
				return -1; 
			}

			entityProps.setSubRace(stringMinkType);
			minkProps.setMinkSwitched(true);

			WyNetwork.sendToAllTrackingAndSelf(new SSyncEntityStatsPacket(player.getId(), entityProps), player);
			WyNetwork.sendTo(new SSyncAbilityDataPacket(player.getId(), abilityProps), (ServerPlayerEntity) player);
		} catch (CommandSyntaxException e) {
			LOGGER.error(e.getMessage());
		}
		return 0;
	}
}
