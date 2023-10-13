package net.hammerclock.minkswitch.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.hammerclock.minkswitch.MinkSwitch;
import net.hammerclock.minkswitch.entity.MinkSwitcherCapability;

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
