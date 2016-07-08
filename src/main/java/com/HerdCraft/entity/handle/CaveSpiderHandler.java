package com.HerdCraft.entity.handle;

import com.HerdCraft.entity.ai.EntityAIHerdRegroup;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraftforge.common.config.Configuration;

public class CaveSpiderHandler extends EntityHandler
{
    private boolean CAVE_SPIDER_HORDE;

    @Override
    public void refreshConfig(Configuration config)
    {
        CAVE_SPIDER_HORDE = config.get("Cave Spider", "Hordes", true).getBoolean(true);
    }

    @Override
    public boolean canHandle(EntityCreature entity)
    {
        return entity instanceof EntityCaveSpider && CAVE_SPIDER_HORDE;
    }

    @Override
    public void handleEntity(EntityCreature entity)
    {
        //only EntityAnimals may breed.
        entity.tasks.addTask(5, new EntityAIHerdRegroup(entity, 0.23F, 0, 0, 0, 0));
    }
}
