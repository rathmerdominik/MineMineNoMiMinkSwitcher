package net.hammerclock.minkswitch.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class MinkSwitcherCapability {
	@CapabilityInject(IMinkSwitcher.class)
	public static final Capability<IMinkSwitcher> INSTANCE = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IMinkSwitcher.class, new Capability.IStorage<IMinkSwitcher>() {

			@Override
			public INBT writeNBT(Capability<IMinkSwitcher> capability, IMinkSwitcher instance, Direction side) {
				CompoundNBT props = new CompoundNBT();

				props.putBoolean("mink_switched", instance.getMinkSwitched());

				return props;
			}

			@Override
			public void readNBT(Capability<IMinkSwitcher> capability, IMinkSwitcher instance, Direction side,
					INBT nbt) {
				CompoundNBT props = (CompoundNBT) nbt;

				instance.setMinkSwitched(props.getBoolean("mink_switched"));
			}

		}, () -> new MinkSwitcherBase());
	}

	public static IMinkSwitcher get(final PlayerEntity entity) {
		IMinkSwitcher props = entity.getCapability(INSTANCE, null).orElse(new MinkSwitcherBase());
		return props;
	}
}
