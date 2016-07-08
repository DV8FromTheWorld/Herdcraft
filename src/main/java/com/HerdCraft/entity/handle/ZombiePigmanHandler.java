package com.HerdCraft.entity.handle;

import com.HerdCraft.entity.ai.EntityAIHerdRegroup;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ZombiePigmanHandler extends EntityHandler
{
    private boolean ZOMBIE_PIGMAN_HORDE;
    private boolean ZOMBIE_PIGMAN_HORDE_AS_ZOMBIE;

    @Override
    public void refreshConfig(Configuration config)
    {
        Property curr;

        ZOMBIE_PIGMAN_HORDE = config.get("Zombie Pigman", "Hordes", true).getBoolean(true);
        curr = config.get("Zombie Pigman", "Is A Zombie", true);
        curr.comment = "Whether or not Zombie Pigmen try to stay together with Zombies. This is forced true by \"Explicitly Zombie\"";
        ZOMBIE_PIGMAN_HORDE_AS_ZOMBIE = curr.getBoolean(true);
    }

    @Override
    public boolean canHandle(EntityCreature entity)
    {
        return entity instanceof EntityPigZombie && ZOMBIE_PIGMAN_HORDE;
    }

    @Override
    public void handleEntity(EntityCreature entity)
    {
        //only EntityAnimals may breed.
        entity.tasks.addTask(5, new EntityAIHerdRegroup(entity, 0.23F, 0, 0, 0, 0,(ZOMBIE_PIGMAN_HORDE_AS_ZOMBIE ? EntityZombie.class : null)));
    }
}
