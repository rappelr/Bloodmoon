package com.rappelr.bloodmoon.hook;

import com.rappelr.bloodmoon.Bloodmoon;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import lombok.val;

public class MythicMobsCondition extends SkillCondition implements IEntityCondition {
	
	public static final String NAME = "BLOODMOON";
	
	public MythicMobsCondition(MythicLineConfig config) {
		super(config.getLine());
	}

	@Override
	public boolean check(AbstractEntity entity) {
		val world = Bloodmoon.getInstance().getWorldManager().by(entity.getWorld().getName());
		
		return world == null ? false : world.getClock().isBloodmoon();
	}

}
