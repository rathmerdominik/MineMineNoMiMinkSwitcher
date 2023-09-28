package com.derhammerclock.minkswitch.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	public static final Path CONFIG_PATH = Paths.get("config", "minkswitch-common.toml");
	public static final CommonConfig INSTANCE;
	public static final ForgeConfigSpec CONFIG;
	private ForgeConfigSpec.BooleanValue switchAgain;

	static {
		Pair<CommonConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);

		CONFIG = pair.getRight();
		INSTANCE = pair.getLeft();

		CommentedFileConfig file = CommentedFileConfig.builder(CONFIG_PATH).sync().autoreload()
				.writingMode(WritingMode.REPLACE).build();

		file.load();
		file.save();

		CONFIG.setConfig(file);
	}
   
	public CommonConfig(ForgeConfigSpec.Builder builder) {
		this.switchAgain = builder.comment("Defines if a player can switch his mink race again after he switched once.")
				.define("Allow switching again", false);
	}

	public Boolean getSwitchAgain() {
		return this.switchAgain.get();
	}
}