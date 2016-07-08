package com.HerdCraft.compat.etfuturum;

import com.HerdCraft.entity.ai.EntityAIHerdPanic;
import com.HerdCraft.entity.ai.EntityAIHerdRegroup;
import com.HerdCraft.entity.ai.EntityAIHerdStampede;
import com.HerdCraft.entity.ai.EntityAIHerdTempt;
import com.HerdCraft.entity.handle.EntityHandler;
import ganymedes01.etfuturum.entities.EntityRabbit;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.init.Items;
import net.minecraftforge.common.config.Configuration;
import java.util.Iterator;

public class RabbitHandler extends EntityHandler
{
    private boolean RABBITS_HERD;
    private boolean RABBITS_STAMPEDE;
    private int RABBIT_MIN_BREED;
    private int RABBIT_MAX_BREED;
    private int RABBIT_BASE_TIME;
    private int RABBIT_VAR_TIME;
    
    @Override
    public void refreshConfig(Configuration config)
    {
        RABBITS_HERD = config.get("EtFuturum.Rabbit", "Herds", true).getBoolean(true);
        RABBITS_STAMPEDE = config.get("EtFuturum.Rabbit", "Stampedes", false).getBoolean(true);
        RABBIT_BASE_TIME = config.get("EtFuturum.Rabbit", "Base Breed Time", 3000).getInt();
        RABBIT_VAR_TIME = config.get("EtFuturum.Rabbit", "Variable Breed Time", 1500).getInt();
        RABBIT_MIN_BREED = config.get("EtFuturum.Rabbit", "Min Rabbits", 2).getInt();
        RABBIT_MAX_BREED = config.get("EtFuturum.Rabbit", "Max Rabbits", 12).getInt();
    }

    @Override
    public boolean canHandle(EntityCreature entity)
    {
        return entity instanceof EntityRabbit && RABBITS_HERD;
    }

    @Override
    public void handleEntity(EntityCreature entity)
    {
        entity.tasks.addTask(5, new EntityAIHerdRegroup(entity, 1.0D,RABBIT_MIN_BREED, RABBIT_MAX_BREED, RABBIT_BASE_TIME, RABBIT_VAR_TIME));
        if (RABBITS_STAMPEDE)
        {
            entity.tasks.addTask(1, new EntityAIHerdStampede(entity, 1.4D, RABBIT_MIN_BREED, RABBIT_MAX_BREED, RABBIT_BASE_TIME, RABBIT_VAR_TIME));
        }
        else
        {
            entity.tasks.addTask(1, new EntityAIHerdPanic(entity, 1.4D, RABBIT_MIN_BREED, RABBIT_MAX_BREED, RABBIT_BASE_TIME, RABBIT_VAR_TIME));
        }
        entity.tasks.addTask(3, new EntityAIHerdTempt(entity, 1.0D, Items.carrot, false, RABBIT_MIN_BREED, RABBIT_MAX_BREED, RABBIT_BASE_TIME, RABBIT_VAR_TIME));

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
            if (aiTask.action instanceof EntityAIPanic || aiTask.action instanceof EntityAITempt) {
                i.remove();
            }
        }
    }
}
