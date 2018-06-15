package com.ewyboy.blocktweaker.common;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by EwyBoy
 */
public class ConfigLoader {

    public static Configuration config;
    private static final Logger LOGGER = LogManager.getLogger(Reference.INFO.NAME);
    private static String MAINCAT_GOLBAL = "Global Tweaks";
    private static String MAINCAT_SPECIFIC = "Specific Tweaks";
    private static String SUBCAT_HARDNESS = "hardness";
    private static String SUBCAT_RESISTANCE = "resistance";
    private static String SUBCAT_HARVEST = "harvesting";
    private static String SUBCAT_LIGHT = "light";
    private static String SUBCAT_FLUID = "fluid";

    private static String getFormattedBlockName(Block block) {
        try {
            return block.getLocalizedName();
        } catch (Exception e) {
            return "[Unknown Block]";
        }
    }

    private static String getResourceDomain(Block block) {
        try {
            return block.getRegistryName().getResourceDomain();
        } catch (NullPointerException e) {
            return "";
        }
    }

    private static String getGlobalCategory(@Nullable String subCategory) {
        try {
            return subCategory == null ? Configuration.CATEGORY_GENERAL + "." + MAINCAT_GOLBAL : Configuration.CATEGORY_GENERAL + "." + MAINCAT_GOLBAL + "." + subCategory;
        } catch (NullPointerException e) {
            return "";
        }
    }

    private static String getSpecificCategory(@Nullable Block block) {
        try {
            return block == null ? Configuration.CATEGORY_GENERAL + "." + MAINCAT_SPECIFIC : Configuration.CATEGORY_GENERAL + "." + MAINCAT_SPECIFIC + "." + getResourceDomain(block) + "." + getFormattedBlockName(block);
        } catch (NullPointerException e) {
            return "";
        }
    }

    private static String getSpecificCategory(Block block, String subCategory) {
        try {
            return Configuration.CATEGORY_GENERAL + "." + MAINCAT_SPECIFIC + "." + getResourceDomain(block) + "." + getFormattedBlockName(block) + "." + subCategory;
        } catch (NullPointerException e) {
            return "";
        }
    }

    private static void initCategories(Block block) {
        config.setCategoryComment(getGlobalCategory(null), "Contains Global Settings That Applies To ALL Blocks");
        config.setCategoryComment(getSpecificCategory(null), "Contains Specific Settings That Applies To Specific Blocks");
        config.setCategoryComment(getGlobalCategory(SUBCAT_HARDNESS), "Contains Global Hardness Tweaks");
        config.setCategoryComment(getSpecificCategory(block), "Contains Properties For " + getFormattedBlockName(block));
        config.setCategoryComment(Configuration.CATEGORY_GENERAL + "." + MAINCAT_SPECIFIC + "." + getResourceDomain(block), "List Of All Blocks From " + getResourceDomain(block));
    }

    private static float getBlockDefaultHardness(Block block) {
        try {
            return block.getBlockHardness(block.getDefaultState(), null, null);
        } catch (NullPointerException e) {
            return 1.0f;
        }
    }

    private static float getBlockDefaultResistance(Block block) {
        try {
            return block.getExplosionResistance(null, null, null, null);
        } catch (NullPointerException e) {
            return 1.0f;
        }
    }

    private static float getBlockDefaultBrightness(Block block) {
        try {
            return block.getLightValue(block.getDefaultState());
        } catch (NullPointerException e) {
            return 0.0f;
        }
    }

    private static void tweakBrightness(Block block) {
        try {
            if (getBlockDefaultBrightness(block) > 0) {
                block.setLightLevel(
                        config.getFloat(
                                "Set Light Value", getSpecificCategory(block, SUBCAT_LIGHT),
                                getBlockDefaultBrightness(block) / 15.0f, 0.0f, 1.0f,
                                "Overrides the brightness of the light emitted with this value."
                        )
                );
                config.setCategoryComment(getSpecificCategory(block, SUBCAT_LIGHT), "Tweak lightning properties for " + getFormattedBlockName(block));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to tweak brightness for " + getFormattedBlockName(block), e);
        }
    }

    private static void tweakHarness(Block block) {
        try {
            if (getBlockDefaultHardness(block) >= 0.0f) {
                block.setHardness(
                        config.getFloat(
                                "Hardness", getSpecificCategory(block, SUBCAT_HARDNESS),
                                getBlockDefaultHardness(block), 0.0f, Float.MAX_VALUE,
                                "Overrides the hardness value with this value."
                        )
                );
                config.setCategoryComment(getSpecificCategory(block, SUBCAT_HARDNESS), "Tweak hardness properties for " + getFormattedBlockName(block));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to tweak hardness for " + getFormattedBlockName(block), e);
        }
    }

    private static void tweakResistance(Block block) {
        try {
            if (getBlockDefaultResistance(block) >= 0.0f) {
                block.setResistance(
                        config.getFloat(
                                "Resistance", getSpecificCategory(block, SUBCAT_RESISTANCE),
                                getBlockDefaultResistance(block), 0.0f, Float.MAX_VALUE,
                                "Overrides the resistance value with this value."
                        )
                );
                config.setCategoryComment(getSpecificCategory(block, SUBCAT_RESISTANCE), "Tweak resistance properties for " + getFormattedBlockName(block));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to tweak resistance for " + getFormattedBlockName(block), e);
        }
    }

    private static void setUnbreakable(Block block) {
        boolean setBlockUnbreakable;
        boolean isBlockUnbreakable = false;

        try {
            if (getBlockDefaultHardness(block) < 0.0f) {
                isBlockUnbreakable = true;
            }
            setBlockUnbreakable = config.getBoolean(
                    "Set Unbreakable",  getSpecificCategory(block, SUBCAT_HARDNESS),
                    isBlockUnbreakable, "Overrides the hardness value with unbreakable hardness."
            );
            if (setBlockUnbreakable) {
                block.setBlockUnbreakable();
                config.setCategoryComment(getSpecificCategory(block, SUBCAT_HARDNESS), "Tweak hardness properties for " + getFormattedBlockName(block));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to make " + getFormattedBlockName(block) + " unbreakable", e);
        }
    }

    private static void tweakHarvestLevel(Block block) {
        try {
            if (block.getHarvestTool(block.getDefaultState()) != null) {
                block.setHarvestLevel(
                        config.getString(
                                "Harvest Tool", getSpecificCategory(block, SUBCAT_HARVEST),
                                block.getHarvestTool(block.getDefaultState()),
                                "Overrides the harvest tool with this tool.\n" +
                                         "[shovel] [pickaxe] [axe] [sword]"
                        ),
                        config.getInt(
                                "Harvest Level", getSpecificCategory(block, SUBCAT_HARVEST),
                                block.getHarvestLevel(block.getDefaultState()), 0, 16,
                                "Overrides the harvest level value with this value.\n" +
                                        "Wood:    0\n" +
                                        "Stone:   1\n" +
                                        "Iron:    2\n" +
                                        "Diamond: 3\n"
                        )
                );
                config.setCategoryComment(getSpecificCategory(block, SUBCAT_HARVEST), "Tweak harvesting properties for " + getFormattedBlockName(block));
            }
        } catch (Exception e) {
            LOGGER.error("Fail to tweak " + getFormattedBlockName(block) + "'s harvest level or effective tool", e);
        }
    }

    private static void fluidTweaker(Block block) {
        try {
            if (block instanceof BlockFluidClassic) {
                Fluid fluid = ((BlockFluidClassic) block).getFluid();
                try {
                    fluid.setDensity(
                            config.getInt("Density", getSpecificCategory(block, SUBCAT_FLUID),
                                    fluid.getDensity(), Integer.MIN_VALUE, Integer.MAX_VALUE,
                                    "Overrides the density value with this value.\n" +
                                             "Density of the fluid - completely arbitrary;\n" +
                                             "negative density indicates that the fluid is lighter than air.\n" +
                                             "Default value is approximately the real-life density of water in kg/m^3.\n"
                            )
                    );
                } catch (Exception e) {
                    LOGGER.error("Failed to tweak the density value for " + fluid.getName(), e);
                }
                try {
                    fluid.setLuminosity(
                            config.getInt("Luminosity", getSpecificCategory(block, SUBCAT_FLUID),
                                    fluid.getLuminosity(), 0, Integer.MAX_VALUE,
                                    "Overrides the luminosity value with this value.\n" +
                                             "The light level emitted by this fluid.\n" +
                                             "Default value is 0, as most fluids do not actively emit light.\n"
                            )
                    );
                } catch (Exception e) {
                    LOGGER.error("Failed to tweak the luminosity value for " + fluid.getName(), e);
                }
                try {
                    fluid.setViscosity(
                            config.getInt("Viscosity", getSpecificCategory(block, SUBCAT_FLUID),
                                    fluid.getLuminosity(), 0, Integer.MAX_VALUE,
                                    "Overrides the viscosity value with this value.\n" +
                                             "Viscosity (thickness) of the fluid - completely arbitrary;\n" +
                                             "Negative values are not permissible.\n" +
                                             "Default value is approximately the real-life density of water in m/s^2 (x10^-3).\n" +
                                             "Higher viscosity means that a fluid flows more slowly, like molasses.\n" +
                                             "Lower viscosity means that a fluid flows more quickly, like helium.\n"
                            )
                    );
                } catch (Exception e) {
                    LOGGER.error("Failed to tweak the viscosity value for " + fluid.getName(), e);
                }
                try {
                    fluid.setTemperature(
                            config.getInt("Temperature", getSpecificCategory(block, SUBCAT_FLUID),
                                    fluid.getTemperature(), 0, Integer.MAX_VALUE,
                                    "Overrides the temperature value with this value.\n" +
                                             "Temperature of the fluid - completely arbitrary;\n" +
                                             "higher temperature indicates that the fluid is hotter than air.\n" +
                                             "Default value is approximately the real-life room temperature of water in degrees Kelvin.\n"
                            )
                    );
                } catch (Exception e) {
                    LOGGER.error("Failed to tweak the temperature value for " + fluid.getName(), e);
                }
                config.setCategoryComment(getSpecificCategory(block, SUBCAT_FLUID), "Tweak fluid properties for " + getFormattedBlockName(block));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void globalHardnessTweak(Block block) {
        try {
            if(getBlockDefaultHardness(block) >= 1.0) {
                block.setHardness(getBlockDefaultHardness(block) * config.getFloat(
                        "Hardness Multiplier", getGlobalCategory(SUBCAT_HARDNESS),
                        getBlockDefaultHardness(block), 1.0f, Float.MAX_VALUE,
                        "Multiplies the base hardness value for the block with this value."
                        )
                );
                config.setCategoryComment(getGlobalCategory(SUBCAT_HARDNESS), "Tweak the hardness properties for all blocks");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to tweak hardness for " + getFormattedBlockName(block), e);
        }
    }

    private static void globalResistanceTweak(Block block) {
        try {
            if(getBlockDefaultResistance(block) >= 1.0) {
                block.setResistance(getBlockDefaultResistance(block) * config.getFloat(
                        "Resistance Multiplier", getGlobalCategory(SUBCAT_RESISTANCE),
                        getBlockDefaultResistance(block), 1.0f, Float.MAX_VALUE,
                        "Multiplies the base resistance value for the block with this value."
                        )
                );
                config.setCategoryComment(getGlobalCategory(SUBCAT_RESISTANCE), "Tweak the resistance properties for all blocks");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to tweak resistance for " + getFormattedBlockName(block), e);
        }
    }

    private static void globalUnbreakableTweak(Block block) {
        boolean setBlockUnbreakable;
        boolean isBlockUnbreakable = false;

        try {
            if (getBlockDefaultHardness(block) < 0.0f) {
                isBlockUnbreakable = true;
            }
            setBlockUnbreakable = config.getBoolean(
                    "Set All Unbreakable",  getGlobalCategory(SUBCAT_HARDNESS),
                    false, "Overrides the hardness value with unbreakable hardness."
            );
            if (setBlockUnbreakable && !isBlockUnbreakable) {
                block.setBlockUnbreakable();
                config.setCategoryComment(getGlobalCategory(SUBCAT_HARDNESS), "Makes all blocks unbreakable.");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to make " + getFormattedBlockName(block) + " unbreakable", e);
        }
    }

    public static void syncConfig() {
        config.load();
        List blockList = ForgeRegistries.BLOCKS.getValues();
        for (Object aBlockList : blockList) {
            Block block = (Block) aBlockList;
            tweakHarness(block);
            tweakResistance(block);
            globalHardnessTweak(block);
            globalResistanceTweak(block);
            setUnbreakable(block);
            globalUnbreakableTweak(block);
            tweakBrightness(block);
            tweakHarvestLevel(block);
            fluidTweaker(block);
            initCategories(block);
        }
        if (config.hasChanged()) {
            config.save();
        }
    }
}
