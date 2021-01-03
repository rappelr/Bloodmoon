package com.rappelr.bloodmoon.effect;

import com.rappelr.bloodmoon.world.BloodmoonWorld;

import io.netty.util.internal.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AmbientEffect {
	
	private final BloodmoonWorld world;
	
	private int timer = 0;
	
	public void tick() {
		
		if(world.getSoundAmbient() != null)
			if(timer++ > world.getAmbientFrequency() * (1 + ThreadLocalRandom.current().nextFloat())) {
				world.getSoundAmbient()[ThreadLocalRandom.current().nextInt(world.getSoundAmbient().length)].playAll(world.getWorld());
				timer = 0;
			}
	}

}
