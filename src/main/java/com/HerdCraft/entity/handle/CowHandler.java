package com.HerdCraft.entity.handle;

import com.HerdCraft.entity.ai.EntityAIHerdPanic;
import com.HerdCraft.entity.ai.EntityAIHerdRegroup;
import com.HerdCraft.entity.ai.EntityAIHerdStampede;
import com.HerdCraft.entity.ai.EntityAIHerdTempt;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import java.util.Iterator;

public class CowHandler extends EntityHandler
{
    private boolean COWS_HERD;
    private boolean COWS_STAMPEDE;
    private int COW_MIN_BREED;
    private int COW_MAX_BREED;
    private int COW_BASE_TIME;
    private int COW_VAR_TIME;

    @Override
    public void refreshConfig(Configuration config)
    {
        Property curr;
        curr = config.get("Cow", "Base Breed Time", 4000);
        curr.comment = "Shortest time for cows to breed from herd";
        COW_BASE_TIME = curr.getInt();

        curr = config.get("Cow", "Variable Breed Time", 3000);
        curr.comment = "Extra time added to base time for longest time between breeds.\nNote: Animals will not breed faster than feeding would allow.";
        COW_VAR_TIME = curr.getInt();

        curr = config.get("Cow", "Max Cows", 10);
        curr.comment = "Number of cows required for automatic breeding.";
        COW_MAX_BREED = curr.getInt();
        COW_MIN_BREED = config.get("Cow", "Min Cows", 6).getInt();

        curr = config.get("Cow", "Herds", true);
        curr.comment = "Whether or not cows participate in herding behaviors.";
        COWS_HERD = curr.getBoolean(true);

        curr = config.get("Cow", "Stampedes", true);
        curr.comment = "Whether or not cows stampede rather than panic";
        COWS_STAMPEDE = curr.getBoolean(true);
    }

    @Override
    public boolean canHandle(EntityCreature entity)
    {
        return entity instanceof EntityCow && COWS_HERD;
    }

    @Override
    public void handleEntity(EntityCreature entity)
    {
        //System.out.println("COW!");
        entity.tasks.addTask(5, new EntityAIHerdRegroup(entity, 1.0D,COW_MIN_BREED, COW_MAX_BREED, COW_BASE_TIME, COW_VAR_TIME));
        if (COWS_STAMPEDE)
        {
            entity.tasks.addTask(1, new EntityAIHerdStampede(entity, 2.0D, COW_MIN_BREED, COW_MAX_BREED, COW_BASE_TIME, COW_VAR_TIME));
        }
        else
        {
            entity.tasks.addTask(1, new EntityAIHerdPanic(entity, 2.0D, COW_MIN_BREED, COW_MAX_BREED, COW_BASE_TIME, COW_VAR_TIME));
        }
        entity.tasks.addTask(3, new EntityAIHerdTempt(entity, 1.25D, Items.wheat, false, COW_MIN_BREED, COW_MAX_BREED, COW_BASE_TIME, COW_VAR_TIME));

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
