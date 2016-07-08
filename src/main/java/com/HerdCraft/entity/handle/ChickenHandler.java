package com.HerdCraft.entity.handle;

import com.HerdCraft.entity.ai.EntityAIHerdPanic;
import com.HerdCraft.entity.ai.EntityAIHerdRegroup;
import com.HerdCraft.entity.ai.EntityAIHerdStampede;
import com.HerdCraft.entity.ai.EntityAIHerdTempt;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraftforge.common.config.Configuration;
import java.util.Iterator;

public class ChickenHandler extends EntityHandler
{
    private boolean CHICKENS_HERD;
    private boolean CHICKENS_STAMPEDE;
    private int CHICKEN_MIN_BREED;
    private int CHICKEN_MAX_BREED;
    private int CHICKEN_BASE_TIME;
    private int CHICKEN_VAR_TIME;

    @Override
    public void refreshConfig(Configuration config)
    {
        CHICKENS_HERD = config.get("Chicken", "Herds", true).getBoolean(true);
        CHICKENS_STAMPEDE = config.get("Chicken", "Stampedes", false).getBoolean(false);
        CHICKEN_BASE_TIME = config.get("Chicken", "Base Breed Time", 3500).getInt();
        CHICKEN_VAR_TIME = config.get("Chicken", "Variable Breed Time", 2500).getInt();
        CHICKEN_MIN_BREED = config.get("Chicken", "Min Chickens", 6).getInt();
        CHICKEN_MAX_BREED = config.get("Chicken", "Max Chickens", 10).getInt();
    }

    @Override
    public boolean canHandle(EntityCreature entity)
    {
        return entity instanceof EntityChicken && CHICKENS_HERD;
    }

    @Override
    public void handleEntity(EntityCreature entity)
    {
        entity.tasks.addTask(5, new EntityAIHerdRegroup(entity, 1.0D,CHICKEN_MIN_BREED, CHICKEN_MAX_BREED, CHICKEN_BASE_TIME, CHICKEN_VAR_TIME));
        if (CHICKENS_STAMPEDE)
        {
            entity.tasks.addTask(1, new EntityAIHerdStampede(entity, 1.4D, CHICKEN_MIN_BREED, CHICKEN_MAX_BREED, CHICKEN_BASE_TIME, CHICKEN_VAR_TIME));
        }
        else
        {
            entity.tasks.addTask(1, new EntityAIHerdPanic(entity, 1.4D, CHICKEN_MIN_BREED, CHICKEN_MAX_BREED, CHICKEN_BASE_TIME, CHICKEN_VAR_TIME));
        }
        entity.tasks.addTask(3, new EntityAIHerdTempt(entity, 1.0D, Items.wheat_seeds, false, CHICKEN_MIN_BREED, CHICKEN_MAX_BREED, CHICKEN_BASE_TIME, CHICKEN_VAR_TIME));

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
