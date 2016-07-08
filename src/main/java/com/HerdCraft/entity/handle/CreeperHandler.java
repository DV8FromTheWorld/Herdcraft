package com.HerdCraft.entity.handle;

import com.HerdCraft.entity.ai.EntityAIShyFrom;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CreeperHandler extends EntityHandler
{
    private float CREEPERS_FAN_RANGE;

    @Override
    public void refreshConfig(Configuration config)
    {
        Property curr;

        curr = config.get("Creeper", "Fan Radius", 6.0F);
        curr.comment = "How far away they want to be from eachother. Use 0 to disable.";
        CREEPERS_FAN_RANGE = (float) curr.getDouble(6.0F);
    }

    @Override
    public boolean canHandle(EntityCreature entity)
    {
        return entity instanceof EntityCreeper;
    }

    @Override
    public void handleEntity(EntityCreature entity)
    {
        //does not interrupt wandering, but is more likely if the conditions are met.
        if (CREEPERS_FAN_RANGE > 0)
        {
            entity.tasks.addTask(5, new EntityAIShyFrom(entity, EntityCreeper.class, CREEPERS_FAN_RANGE, 1.0D, 1.2D, 16));
        }
    }
}
