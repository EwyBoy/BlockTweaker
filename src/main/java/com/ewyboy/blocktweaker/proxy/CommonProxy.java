package com.ewyboy.blocktweaker.proxy;

import com.ewyboy.blocktweaker.common.ConfigLoader;
import com.ewyboy.blocktweaker.BlockTweaker;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by EwyBoy
 */
public class CommonProxy  {

    public Side getSide() {
        return Side.SERVER;
    }

    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(BlockTweaker.instance);
        ConfigLoader.config = new Configuration(event.getSuggestedConfigurationFile());
    }

    public void init(FMLInitializationEvent event) { }

    public void postInit(FMLPostInitializationEvent event) {
        ConfigLoader.syncConfig();
    }

}
