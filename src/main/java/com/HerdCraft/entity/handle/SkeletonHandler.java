package com.HerdCraft.entity.handle;

import com.HerdCraft.entity.ai.EntityAIHerdArrowAttack;
import com.HerdCraft.entity.ai.EntityAIHerdRegroup;
import com.HerdCraft.entity.ai.EntityAIShyFrom;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import java.util.Iterator;
import java.util.Random;

public class SkeletonHandler extends EntityHandler
{
    private Random rand = new Random();

    private boolean SKELETONS_HERD;
    private float SKELETON_FAN_RANGE;
    private int SKELETON_CAUTION_ANGLE;
    private boolean SKELETON_FEAR_MELEE;
    
    @Override
    public void refreshConfig(Configuration config)
    {
        Property curr;
        
        curr = config.get("Skeleton", "Clack", true);
        curr.comment = "Whether or not skeletons participate in group behavior.";
        SKELETONS_HERD = curr.getBoolean(true);

        curr = config.get("Skeleton", "Fan Radius", 1.5F);
        curr.comment = "How far away they want to be from eachother when they're not busy killing you. Use 0 to disable.";
        SKELETON_FAN_RANGE = (float) curr.getDouble(1.5F);

        curr = config.get("Skeleton", "Caution Angle", 50);
        curr.comment = "Acceptable degree difference required to shoot at target. Use 0 to disable.";
        SKELETON_CAUTION_ANGLE = curr.getInt(50);

        curr = config.get("Skeleton", "Fear Melee", true);
        curr.comment = "Whether or not skeletons try to flee charging players.";
        SKELETON_FEAR_MELEE = curr.getBoolean(true);
    }

    @Override
    public boolean canHandle(EntityCreature entity)
    {
        return entity instanceof EntitySkeleton && SKELETONS_HERD;
    }

    @Override
    public void handleEntity(EntityCreature entity)
    {
        entity.tasks.addTask(5, new EntityAIHerdRegroup(entity, 1.0D, 0, 0, 0, 0));    //only EntityAnimals may breed.
        if (SKELETON_FAN_RANGE > 0)
        {
            entity.tasks.addTask(5, new EntityAIShyFrom(entity, EntitySkeleton.class, SKELETON_FAN_RANGE, 1.0D, 1.2D, 6));
        }

        Iterator i = entity.tasks.taskEntries.iterator();
        EntityAITasks.EntityAITaskEntry aiTask;
        boolean hadArrow = false;
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
            if (aiTask.action instanceof EntityAIArrowAttack) {
                i.remove();
                hadArrow = true;
            }
        }
        if (hadArrow)
        {
            entity.tasks.addTask(4, new EntityAIHerdArrowAttack((IRangedAttackMob) entity, 1.0D, 20, 60, 15.0F,SKELETON_CAUTION_ANGLE, rand.nextBoolean(), SKELETON_FEAR_MELEE));
            if(SKELETON_FEAR_MELEE)
            {
                entity.tasks.addTask(3, new EntityAIShyFrom(entity, EntityPlayer.class, 5.0F, 1.0, 1.2, 12));
            }
        }
    }
}
