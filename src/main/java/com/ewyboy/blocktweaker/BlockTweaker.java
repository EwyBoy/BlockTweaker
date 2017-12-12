package com.ewyboy.blocktweaker;

import com.ewyboy.blocktweaker.common.ConfigLoader;
import com.ewyboy.blocktweaker.common.Reference;
import com.ewyboy.blocktweaker.proxy.CommonProxy;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.ewyboy.blocktweaker.common.Reference.INFO.*;
import static com.ewyboy.blocktweaker.common.Reference.PATH.*;

@Mod(modid = MODID, name = NAME, version = VERSION, guiFactory = GUI_PATH)
public class BlockTweaker {

    @Mod.Instance(MODID)
    public static BlockTweaker instance;

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy;

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if(eventArgs.getModID().equals(Reference.INFO.MODID)) ConfigLoader.syncConfig();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
