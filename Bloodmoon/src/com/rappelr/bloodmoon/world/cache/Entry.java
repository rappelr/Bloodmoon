package com.rappelr.bloodmoon.world.cache;

import java.io.Serializable;

import com.rappelr.bloodmoon.world.BloodmoonWorld;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@SuppressWarnings("serial")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Entry implements Serializable {
	
	private static final String SPLITTER = "/";
	
	@Getter private final boolean isBloodmoon;
	
	@Getter private final int lastBloodmoon;
	
	@Getter private final long lastNight;
	
	static Entry of(String string) {
		val split = string.split(SPLITTER);
		return new Entry(split[0].equalsIgnoreCase("t"), Integer.parseInt(split[1]), Long.parseLong(split[2]));
	}
	
	static Entry of(BloodmoonWorld world) {
		return new Entry(false, world.getInterval(), 24000);
	}
	
	@Override
	public String toString() {
		return (isBloodmoon ? "t" : "f") + SPLITTER + lastBloodmoon + SPLITTER + lastNight;
	}
}
