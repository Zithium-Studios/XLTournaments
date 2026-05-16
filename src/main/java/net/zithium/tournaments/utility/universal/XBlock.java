/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.utility.universal;

import com.cryptomorin.xseries.reflection.XReflection;
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
        if (XReflection.supports(1, 13)) {
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
        if (XReflection.supports(1, 13) && block.getBlockData() instanceof org.bukkit.block.data.Ageable) {
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
}