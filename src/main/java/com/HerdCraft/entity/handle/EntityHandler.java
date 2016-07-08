package com.HerdCraft.entity.handle;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityCreature;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public abstract class EntityHandler
{
    public abstract void refreshConfig(Configuration config);

    public abstract boolean canHandle(EntityCreature entity);

    public abstract void handleEntity(EntityCreature entity);

    @SubscribeEvent
    public void onEntityJoinedWorld(EntityJoinWorldEvent event)
    {
        if (!(event.entity instanceof EntityCreature))
            return;

        EntityCreature entity = (EntityCreature) event.entity;
        if (canHandle(entity))
            handleEntity(entity);
    }
}
