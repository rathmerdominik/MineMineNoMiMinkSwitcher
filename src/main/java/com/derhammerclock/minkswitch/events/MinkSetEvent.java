package com.derhammerclock.minkswitch.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.derhammerclock.minkswitch.MinkSwitch;
import com.derhammerclock.minkswitch.entity.IMinkSwitcher;
import com.derhammerclock.minkswitch.entity.MinkSwitcherCapability;
import com.derhammerclock.minkswitch.entity.MinkSwitcherProvider;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.pixelatedw.mineminenomi.api.events.SetPlayerDetailsEvent;

@Mod.EventBusSubscriber(modid = MinkSwitch.PROJECT_ID)
public class MinkSetEvent {
	public static final Logger LOGGER = LogManager.getLogger(MinkSwitch.PROJECT_ID);

	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() == null)
			return;

		if (event.getObject() instanceof PlayerEntity) {
			event.addCapability(new ResourceLocation(MinkSwitch.PROJECT_ID, "mink_switcher"),
					new MinkSwitcherProvider());
		}
	}

	@SubscribeEvent
	public static void onPlayerDeath(PlayerEvent.Clone event) {
		INBT nbt = new CompoundNBT();
		IMinkSwitcher oldQuestData = MinkSwitcherCapability.get(event.getOriginal());
		nbt = MinkSwitcherCapability.INSTANCE.writeNBT(oldQuestData, null);
		IMinkSwitcher newQuestData = MinkSwitcherCapability.get(event.getPlayer());
		MinkSwitcherCapability.INSTANCE.readNBT(newQuestData, null, nbt);
	}

	@SubscribeEvent
	@SuppressWarnings("null")
	public static void onPlayerDetailsSet(SetPlayerDetailsEvent event) {
		if (event.getEntityStats().isMink()) {
			if (event.getEntity().getServer() != null) {
				event.getEntity().getServer().getCommands().performCommand(event.getPlayer().createCommandSourceStack(),
						"/mink_switch");
			}
		}
	}
}