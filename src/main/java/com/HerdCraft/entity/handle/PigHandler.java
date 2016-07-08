package com.HerdCraft.entity.handle;

import com.HerdCraft.entity.ai.EntityAIHerdPanic;
import com.HerdCraft.entity.ai.EntityAIHerdRegroup;
import com.HerdCraft.entity.ai.EntityAIHerdStampede;
import com.HerdCraft.entity.ai.EntityAIHerdTempt;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Items;
import net.minecraftforge.common.config.Configuration;
import java.util.Iterator;

public class PigHandler extends EntityHandler
{
    private boolean PIGS_HERD;
    private boolean PIGS_STAMPEDE;
    private int PIG_MIN_BREED;
    private int PIG_MAX_BREED;
    private int PIG_BASE_TIME;
    private int PIG_VAR_TIME;

    @Override
    public void refreshConfig(Configuration config)
    {
        PIGS_HERD = config.get("Pig", "Herds", true).getBoolean(true);
        PIGS_STAMPEDE = config.get("Pig", "Stampedes", false).getBoolean(false);
        PIG_BASE_TIME = config.get("Pig", "Base Breed Time", 3000).getInt();
        PIG_VAR_TIME = config.get("Pig", "Variable Breed Time", 1500).getInt();
        PIG_MIN_BREED = config.get("Pig", "Min Pigs", 6).getInt();
        PIG_MAX_BREED = config.get("Pig", "Max Pigs", 12).getInt();
    }

    @Override
    public boolean canHandle(EntityCreature entity)
    {
        return entity instanceof EntityPig && PIGS_HERD;
    }

    @Override
    public void handleEntity(EntityCreature entity)
    {
        entity.tasks.addTask(5, new EntityAIHerdRegroup(entity, 1.0D,PIG_MIN_BREED, PIG_MAX_BREED, PIG_BASE_TIME, PIG_VAR_TIME));
        entity.tasks.addTask(3, new EntityAIHerdTempt(entity, 1.2D, Items.carrot_on_a_stick, false, PIG_MIN_BREED, PIG_MAX_BREED, PIG_BASE_TIME, PIG_VAR_TIME));
        entity.tasks.addTask(3, new EntityAIHerdTempt(entity, 1.2D, Items.carrot, false, PIG_MIN_BREED, PIG_MAX_BREED, PIG_BASE_TIME, PIG_VAR_TIME));
        if (PIGS_STAMPEDE)
        {
            entity.tasks.addTask(1, new EntityAIHerdStampede(entity, 1.25D, PIG_MIN_BREED, PIG_MAX_BREED, PIG_BASE_TIME, PIG_VAR_TIME));
        }
        else
        {
            entity.tasks.addTask(1, new EntityAIHerdPanic(entity, 1.25D, PIG_MIN_BREED, PIG_MAX_BREED, PIG_BASE_TIME, PIG_VAR_TIME));
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
            if (aiTask.action instanceof EntityAITempt || aiTask.action instanceof EntityAIPanic) {
                i.remove();
            }
        }
    }
}
