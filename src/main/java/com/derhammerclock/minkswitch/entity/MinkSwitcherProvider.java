package com.derhammerclock.minkswitch.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class MinkSwitcherProvider implements ICapabilitySerializable<CompoundNBT> {
	private IMinkSwitcher instance = MinkSwitcherCapability.INSTANCE.getDefaultInstance();

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return MinkSwitcherCapability.INSTANCE.orEmpty(cap, LazyOptional.of(() -> instance));
	}

	@Override
	public CompoundNBT serializeNBT() {
		return (CompoundNBT) MinkSwitcherCapability.INSTANCE.getStorage().writeNBT(MinkSwitcherCapability.INSTANCE,
				instance, null);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		MinkSwitcherCapability.INSTANCE.getStorage().readNBT(MinkSwitcherCapability.INSTANCE, instance, null, nbt);
	}

}