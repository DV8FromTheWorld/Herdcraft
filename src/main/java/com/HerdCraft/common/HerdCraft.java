package com.HerdCraft.common;

import com.HerdCraft.compat.etfuturum.RabbitHandler;
import com.HerdCraft.entity.handle.ChickenHandler;
import com.HerdCraft.entity.handle.CowHandler;
import com.HerdCraft.entity.handle.CreeperHandler;
import com.HerdCraft.entity.handle.EntityHandler;
import com.HerdCraft.entity.handle.HorseHandler;
import com.HerdCraft.entity.handle.PigHandler;
import com.HerdCraft.entity.handle.SheepHandler;
import com.HerdCraft.entity.handle.SkeletonHandler;
import com.HerdCraft.entity.handle.ZombieHandler;
import com.HerdCraft.entity.handle.ZombiePigmanHandler;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityCreature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = "herdCraft", name = "HerdCraft", version = "1.1", guiFactory = "com.HerdCraft.common.guiconfig.HerdConfigGUIFactory")
public class HerdCraft
{
    @Instance(value = "herdCraft")
    public static HerdCraft herdCraftInst;
    public static Configuration config;

    private List<EntityHandler> handlers = new ArrayList<EntityHandler>();

    private boolean MAGNET;
    private int MAGNET_RADIUS;

    public static HerdCollection herdCollectionObj;

    @EventHandler
    public void configure(FMLPreInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(this);

//        handlers.add(new CaveSpiderHandler()); //spider AI does not seem to work with the task system.
        handlers.add(new ChickenHandler());
        handlers.add(new CowHandler());
        handlers.add(new CreeperHandler());
        handlers.add(new HorseHandler());
        handlers.add(new PigHandler());
        handlers.add(new SheepHandler());
        handlers.add(new SkeletonHandler());
        handlers.add(new ZombieHandler());
        handlers.add(new ZombiePigmanHandler());

        if (Loader.isModLoaded("etfuturum"))
            handlers.add(new RabbitHandler());

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        refreshConfig();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs)
    {
        if (eventArgs.modID.equals("herdCraft"))
            refreshConfig();
    }

    private void refreshConfig()
    {
        Property curr;

        curr = config.get("Magnet", "Magnet", true);
        curr.comment = "Whether or not larger herds attract smaller ones.";
        MAGNET = curr.getBoolean(true);

        curr = config.get("Magnet", "Magnet Radius", 150);
        curr.comment = "Radius from which larger herds attract smaller ones.";
        MAGNET_RADIUS = curr.getInt();

        for (EntityHandler handler : handlers)
        {
            handler.refreshConfig(config);
        }

        if (config.hasChanged())
            config.save();
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        herdCollectionObj = new HerdCollection(MAGNET, MAGNET_RADIUS);
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(herdCollectionObj);

        for (EntityHandler handler : handlers)
        {
            MinecraftForge.EVENT_BUS.register(handler);
        }
    }
}
