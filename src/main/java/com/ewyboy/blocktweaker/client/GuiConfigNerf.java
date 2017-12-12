package com.ewyboy.blocktweaker.client;

import com.ewyboy.blocktweaker.common.ConfigLoader;
import com.ewyboy.blocktweaker.common.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

/**
 * Created by EwyBoy
 */
public class GuiConfigNerf extends GuiConfig {

    public GuiConfigNerf(GuiScreen parentScreen) {
        super(
                parentScreen,
                new ConfigElement(ConfigLoader.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                Reference.INFO.MODID,
                false,
                false,
                Reference.INFO.NAME + " Config"
        );
    }
}
