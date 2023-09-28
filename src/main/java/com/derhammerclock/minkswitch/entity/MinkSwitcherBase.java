package com.derhammerclock.minkswitch.entity;

public class MinkSwitcherBase implements IMinkSwitcher {
	private Boolean minkSwitched = false;

	@Override
	public Boolean getMinkSwitched() {
		return minkSwitched;
	}

	@Override
	public void setMinkSwitched(Boolean minkSwitched) {
		this.minkSwitched = minkSwitched;
	}
}
