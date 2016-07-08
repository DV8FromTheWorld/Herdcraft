package com.HerdCraft.entity.handle;

import com.HerdCraft.entity.ai.EntityAIHerdAttackOnCollide;
import com.HerdCraft.entity.ai.EntityAIHerdRegroup;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import java.util.Iterator;
import java.util.Random;

public class ZombieHandler extends EntityHandler
{
    private Random rand = new Random();

    private boolean ZOMBIES_HORDE;
    private boolean EXPLICIT_ZOMBIE_HORDING;
    private int ZOMBIE_HORDE_BASE_PROB;
    private int ZOMBIE_HORDE_FULLMOON_PROB;
    private int ZOMBIE_HORDE_NEWMOON_PROB;
    private int ZOMBIE_HORDE_RAINING_PROB;
    private int ZOMBIE_HORDE_COLD_PROB;
    private int ZOMBIE_HORDE_WARM_PROB;
    private int ZOMBIE_HORDE_HOT_PROB;
    private int ZOMBIE_HORDE_DRY_PROB;
    private int ZOMBIE_HORDE_DAMP_PROB;
    private int ZOMBIE_HORDE_WET_PROB;
    private int ZOMBIE_HORDE_FLAT_PROB;
    private int ZOMBIE_HORDE_HILLY_PROB;
    private int ZOMBIE_HORDE_JAGGED_PROB;
    private int ZOMBIE_HORDE_UNDERGROUND_PROB;
    private int ZOMBIE_HORDE_MIN;
    private int ZOMBIE_HORDE_MAX;
    
    @Override
    public void refreshConfig(Configuration config)
    {
        Property curr;
        
        curr = config.get("Zombie", "Hordes", true);
        curr.comment = "Whether or not zombies participate in hording behavior.\nFalse means they will not form groups, but may still spawn in large groups.\nIf You do not want hordes to spawn, it is best to set Max Zombies < Min Zombies.";
        ZOMBIES_HORDE = curr.getBoolean(true);

        curr = config.get("Zombie", "Explicitly Zombie", true);
        curr.comment = "Whether or not various types of zombies group with vanilla zombies. Only works if they extend the zombie class.";
        EXPLICIT_ZOMBIE_HORDING = curr.getBoolean(true);

        curr = config.get("Zombie", "Base Horde Probability", 20);
        curr.comment = "Basic probability of Zombie hordes.\nA value of 100 is a 1% chance per spawned zombie.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_BASE_PROB = curr.getInt();

        curr = config.get("Zombie", "Full Moon Horde Probability", -200);
        curr.comment = "Probability modifier of Zombie hordes occuring on a full moon.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_FULLMOON_PROB = curr.getInt();

        curr = config.get("Zombie", "New Moon Horde Probability", 200);
        curr.comment = "Probability modifier of Zombie hordes occuring on a new moon.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_NEWMOON_PROB = curr.getInt();

        curr = config.get("Zombie", "Raining Horde Probability", 100);
        curr.comment = "Probability modifier of Zombie hordes occuring in the rain.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_RAINING_PROB = curr.getInt();

        curr = config.get("Zombie", "Cold Horde Probability", -5);
        curr.comment = "Probability modifier of Zombie hordes occuring in cold biomes.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_COLD_PROB = curr.getInt();

        curr = config.get("Zombie", "Warm Horde Probability", 0);
        curr.comment = "Probability modifier of Zombie hordes occuring in middle temperature biomes.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_WARM_PROB = curr.getInt();

        curr = config.get("Zombie", "Hot Horde Probability", 10);
        curr.comment = "Probability modifier of Zombie hordes occuring in hot biomes.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_HOT_PROB = curr.getInt();

        curr = config.get("Zombie", "Dry Horde Probability", -5);
        curr.comment = "Probability modifier of Zombie hordes occuring in dry biomes.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_DRY_PROB = curr.getInt();

        curr = config.get("Zombie", "Damp Horde Probability", 10);
        curr.comment = "Probability modifier of Zombie hordes occuring in middle moisture biomes.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_DAMP_PROB = curr.getInt();

        curr = config.get("Zombie", "Wet Horde Probability", 20);
        curr.comment = "Probability modifier of Zombie hordes occuring in wet biomes.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_WET_PROB = curr.getInt();

        curr = config.get("Zombie", "Flat Terrain Horde Probability", 15);
        curr.comment = "Probability modifier of Zombie hordes occuring on flatter terrain.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_FLAT_PROB = curr.getInt();

        curr = config.get("Zombie", "Normal Terrain Horde Probability", 5);
        curr.comment = "Probability modifier of Zombie hordes occuring on typicaly rough terrain.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_HILLY_PROB = curr.getInt();

        curr = config.get("Zombie", "Jagged Terrain Horde Probability", -20);
        curr.comment = "Probability modifier of Zombie hordes occuring on jagged terrain.\nThis value is added with all other relevant spawn values.";
        ZOMBIE_HORDE_JAGGED_PROB = curr.getInt();

        curr = config.get("Zombie", "Underground Horde Probability", -10);
        curr.comment = "Probability modifier of Zombie hordes occuring underground.\nThis is only added to the base rate.\nNo others apply underground";
        ZOMBIE_HORDE_UNDERGROUND_PROB = curr.getInt();

        curr = config.get("Zombie", "Max Zombies", 15);
        curr.comment = "Extra zombies to be spawned in a horde. If Max < Min, no hordes.";
        ZOMBIE_HORDE_MAX = curr.getInt();
        ZOMBIE_HORDE_MIN = config.get("Zombie", "Min Zombies", 10).getInt();
    }

    @Override
    public boolean canHandle(EntityCreature entity)
    {
        return entity instanceof EntityZombie && !(entity instanceof EntityPigZombie) && ZOMBIES_HORDE;
    }

    @Override
    public void handleEntity(EntityCreature entity)
    {
        entity.tasks.addTask(5, new EntityAIHerdRegroup(entity, 1.0D, 0, 0, 0, 0,(EXPLICIT_ZOMBIE_HORDING?EntityZombie.class:null)));	//only EntityAnimals may breed.
        entity.tasks.addTask(2, new EntityAIHerdAttackOnCollide(entity, EntityPlayer.class, 1.0D, false,(EXPLICIT_ZOMBIE_HORDING?EntityZombie.class:null)));
        entity.tasks.addTask(3, new EntityAIHerdAttackOnCollide(entity, EntityVillager.class, 1.0D, true,(EXPLICIT_ZOMBIE_HORDING?EntityZombie.class:null)));

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
            if (aiTask.action instanceof EntityAIAttackOnCollide) {
                i.remove();
            }
        }
    }

    @SubscribeEvent
    public void onLivingSpawnEvent(LivingSpawnEvent e)
    {
        if (e.entity.getClass() != EntityZombie.class) return;
        EntityZombie animal = (EntityZombie)e.entity;
        double currProb = (ZOMBIE_HORDE_BASE_PROB + hordeAdjustments(animal)) / 10000D;

        if (animal.getCanSpawnHere()
                && ZOMBIE_HORDE_MAX - ZOMBIE_HORDE_MIN + 1 > 0
                && rand.nextDouble() < currProb)
        {
            spawnZombieHordeOn(animal, animal.worldObj);
        }
    }

    private int hordeAdjustments(EntityZombie entity) {
        int change = 0;
        World world = entity.worldObj;

        int y;
        for(y = (int)entity.posY;y < world.getActualHeight() && world.isAirBlock((int)entity.posX, y, (int)entity.posZ);y++);//stops at first non-air block
        boolean aboveGround = y == world.getActualHeight();
        if (aboveGround)
        {// Above ground
            int moonPhase = (int)(world.getCurrentMoonPhaseFactor() * 4);
            change += moonPhase == 4 ? ZOMBIE_HORDE_FULLMOON_PROB: moonPhase == 0 ? ZOMBIE_HORDE_NEWMOON_PROB : 0;


            if (world.isRaining()){
                change += ZOMBIE_HORDE_RAINING_PROB;
            }


            BiomeGenBase biome = world.getBiomeGenForCoords((int)entity.posX, (int)entity.posZ);

            switch(biome.getTempCategory())
            {
                case COLD:
                    change += ZOMBIE_HORDE_COLD_PROB;
                    break;
                case MEDIUM:
                    change += ZOMBIE_HORDE_WARM_PROB;
                    break;
                case WARM:
                    change += ZOMBIE_HORDE_HOT_PROB;
                    break;
            }

            float rain = biome.rainfall;
            if (rain < 0.2F)
            {
                change += ZOMBIE_HORDE_DRY_PROB;
            }
            else if (rain < 0.8)
            {
                change += ZOMBIE_HORDE_DAMP_PROB;
            }
            else
            {
                change += ZOMBIE_HORDE_WET_PROB;
            }


            float hills = biome.heightVariation;
            if (hills < 0.2)
            {
                change += ZOMBIE_HORDE_FLAT_PROB;
            }
            else if (hills < .5)
            {
                change += ZOMBIE_HORDE_HILLY_PROB;
            }
            else
            {
                change += ZOMBIE_HORDE_JAGGED_PROB;
            }

        }
        else
        {
            change += ZOMBIE_HORDE_UNDERGROUND_PROB;
        }

        return change;
    }

    private void spawnZombieHordeOn(EntityZombie target, World targetWorld)
    {
        int numToSpawn = 0;
        if (ZOMBIE_HORDE_MAX - ZOMBIE_HORDE_MIN + 1 > 0)
        {
            numToSpawn = ZOMBIE_HORDE_MIN + rand.nextInt(ZOMBIE_HORDE_MAX - ZOMBIE_HORDE_MIN + 1);
        }

        for(int i = 0; i < numToSpawn; i++)
        {
            EntityZombie curr = new EntityZombie(targetWorld);
            curr.setPosition(target.posX, target.posY, target.posZ);
            targetWorld.spawnEntityInWorld(curr);
        }
    }
}
