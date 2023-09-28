package com.derhammerclock.minkswitch.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.derhammerclock.minkswitch.MinkSwitch;
import com.derhammerclock.minkswitch.entity.MinkSwitcherCapability;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = MinkSwitch.PROJECT_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MinkSwitchSetup {
	public static final Logger LOGGER = LogManager.getLogger(MinkSwitch.PROJECT_ID);

	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		MinkSwitcherCapability.register();
	}
}
