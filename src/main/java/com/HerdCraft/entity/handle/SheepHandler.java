package com.HerdCraft.entity.handle;

import com.HerdCraft.entity.ai.EntityAIHerdPanic;
import com.HerdCraft.entity.ai.EntityAIHerdRegroup;
import com.HerdCraft.entity.ai.EntityAIHerdStampede;
import com.HerdCraft.entity.ai.EntityAIHerdTempt;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraftforge.common.config.Configuration;
import java.util.Iterator;

public class SheepHandler extends EntityHandler
{
    private boolean SHEEP_HERD;
    private boolean SHEEP_STAMPEDE;
    private int SHEEP_MIN_BREED;
    private int SHEEP_MAX_BREED;
    private int SHEEP_BASE_TIME;
    private int SHEEP_VAR_TIME;

    @Override
    public void refreshConfig(Configuration config)
    {
        SHEEP_HERD = config.get("Sheep", "Herds", true).getBoolean(true);
        SHEEP_STAMPEDE = config.get("Sheep", "Stampedes", true).getBoolean(true);
        SHEEP_BASE_TIME = config.get("Sheep", "Base Breed Time", 3500).getInt();
        SHEEP_VAR_TIME = config.get("Sheep", "Variable Breed Time", 2500).getInt();
        SHEEP_MIN_BREED = config.get("Sheep", "Min Sheep", 6).getInt();
        SHEEP_MAX_BREED = config.get("Sheep", "Max Sheep", 10).getInt();        
    }

    @Override
    public boolean canHandle(EntityCreature entity)
    {
        return entity instanceof EntitySheep && SHEEP_HERD;
    }

    @Override
    public void handleEntity(EntityCreature entity)
    {
        entity.tasks.addTask(5, new EntityAIHerdRegroup(entity, 1.0D,SHEEP_MIN_BREED, SHEEP_MAX_BREED, SHEEP_BASE_TIME, SHEEP_VAR_TIME));
        if (SHEEP_STAMPEDE)
        {
            entity.tasks.addTask(1, new EntityAIHerdStampede(entity, 1.25D, SHEEP_MIN_BREED, SHEEP_MAX_BREED, SHEEP_BASE_TIME, SHEEP_VAR_TIME));
        }
        else
        {
            entity.tasks.addTask(1, new EntityAIHerdPanic(entity, 1.25D, SHEEP_MIN_BREED, SHEEP_MAX_BREED, SHEEP_BASE_TIME, SHEEP_VAR_TIME));
        }
        entity.tasks.addTask(3, new EntityAIHerdTempt(entity, 1.1D, Items.wheat, false, SHEEP_MIN_BREED, SHEEP_MAX_BREED, SHEEP_BASE_TIME, SHEEP_VAR_TIME));

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
