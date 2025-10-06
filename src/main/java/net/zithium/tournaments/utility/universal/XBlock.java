/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.utility.universal;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.CropState;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

/*
  Based loosely off of XBlock (https://github.com/CryptoMorin/XSeries)
 */

@SuppressWarnings("deprecation") // Suppress deprecation warning for MaterialData and Crops
public final class XBlock {

    public static boolean isCrop(Block block) {
        if (XMaterial.supports(13)) {
            return block.getBlockData() instanceof org.bukkit.block.data.Ageable;
        }

        BlockState state = block.getState();
        MaterialData data = state.getData();
        return data instanceof Crops;
    }

    /**
     * Checks if a given block represents a fully grown crop based on its type and age.
     *
     * @param block The block to check for crop growth.
     * @return {@code true} if the block is a fully grown crop of a recognized type, {@code false} otherwise.
     */
    public static boolean isCropFullyGrown(Block block) {
        if (XMaterial.supports(13) && block.getBlockData() instanceof org.bukkit.block.data.Ageable) {
            org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable) block.getBlockData();
            String blockType = block.getType().toString();

            for (PlantAge plantAge : PlantAge.values()) {
                for (String plantType : plantAge.getPlantTypes()) {
                    if (blockType.equalsIgnoreCase(plantType)) {
                        return ageable.getAge() == plantAge.getMaxAge();
                    }
                }
            }
        } else {
            BlockState state = block.getState();
            MaterialData materialData = state.getData();
            return (((Crops) materialData).getState() == CropState.RIPE);
        }

        return false; // Block is not an Ageable crop or not fully grown
    }

    /*
    public static String getCropType(Block block) {
        XMaterial xMaterial = XMaterial.matchXMaterial(block.getType());

        if (xMaterial != null) {
            if (xMaterial == XMaterial.WHEAT) {
                byte data = block.getData(); // Get the block's data (growth stage)

                if (data >= 0 && data <= 7) {
                    return "WHEAT"; // Wheat has growth stages 0 to 7
                }
            } else if (xMaterial == XMaterial.CARROT) {
                byte data = block.getData(); // Get the block's data (growth stage)

                if (data >= 8 && data <= 15) {
                    return "CARROT"; // Carrots have growth stages 8 to 15
                }
            } else if (xMaterial == XMaterial.POTATO) {
                byte data = block.getData(); // Get the block's data (growth stage)

                if (data >= 0 && data <= 7) {
                    return "POTATO"; // Potatoes have growth stages 0 to 7
                }
            } else if (xMaterial == XMaterial.NETHER_WART) {
                byte data = block.getData(); // Get the block's data (growth stage)

                if (data >= 0 && data <= 2) {
                    return "NETHER_WART"; // Nether Wart has growth stages 0 to 2
                }
            } else if (xMaterial == XMaterial.COCOA_BEANS) {
                byte data = block.getData(); // Get the block's data (age)

                if (data >= 0 && data <= 8) {
                    return "COCOA"; // Cocoa has three growth stages (0 to 8)
                }
            }
        }

        // If it's none of the recognized crop types, return "UNKNOWN"
        return "UNKNOWN";
    }

     */

}