package com.HerdCraft.entity.handle;

import com.HerdCraft.entity.ai.EntityAIHerdPanic;
import com.HerdCraft.entity.ai.EntityAIHerdRegroup;
import com.HerdCraft.entity.ai.EntityAIHerdStampede;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraftforge.common.config.Configuration;
import java.util.Iterator;

public class HorseHandler extends EntityHandler
{
    private boolean HORSES_HERD;
    private boolean HORSES_STAMPEDE;
    private int HORSE_MIN_BREED;
    private int HORSE_MAX_BREED;
    private int HORSE_BASE_TIME;
    private int HORSE_VAR_TIME;
    
    @Override
    public void refreshConfig(Configuration config)
    {
        HORSES_HERD = config.get("Horse", "Herds", true).getBoolean(true);
        HORSES_STAMPEDE = config.get("Horse", "Stampedes", true).getBoolean(true);
        HORSE_BASE_TIME = config.get("Horse", "Base Breed Time", 9000).getInt();
        HORSE_VAR_TIME = config.get("Horse", "Variable Breed Time", 3000).getInt();
        HORSE_MIN_BREED = config.get("Horse", "Min Horses", 6).getInt();
        HORSE_MAX_BREED = config.get("Horse", "Max Horses", 10).getInt();
    }

    @Override
    public boolean canHandle(EntityCreature entity)
    {
        return entity instanceof EntityHorse && HORSES_HERD;
    }

    @Override
    public void handleEntity(EntityCreature entity)
    {
        entity.tasks.addTask(5, new EntityAIHerdRegroup(entity, 1.0D,HORSE_MIN_BREED, HORSE_MAX_BREED, HORSE_BASE_TIME, HORSE_VAR_TIME));
        if (HORSES_STAMPEDE)
        {
            entity.tasks.addTask(1, new EntityAIHerdStampede(entity, 2.0D, HORSE_MIN_BREED, HORSE_MAX_BREED, HORSE_BASE_TIME, HORSE_VAR_TIME));
        }
        else
        {
            entity.tasks.addTask(1, new EntityAIHerdPanic(entity, 2.0D, HORSE_MIN_BREED, HORSE_MAX_BREED, HORSE_BASE_TIME, HORSE_VAR_TIME));
        }

        Iterator i = entity.tasks.taskEntries.iterator();
        EntityAITasks.EntityAITaskEntry aiTask;
        while (i.hasNext())
        {
            try
            {
                aiTask = (EntityAITasks.EntityAITaskEntry)i.next();
            }
            catch (java.util.ConcurrentModificationException e)
            {
                System.out.println("Iterator Concurrency Exception: HerdCraft... continuing");
                break;
            }
            finally
            { }
            if (aiTask.action instanceof EntityAIPanic) {
                i.remove();
            }
        }
    }
}
