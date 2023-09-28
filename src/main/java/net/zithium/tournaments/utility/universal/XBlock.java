/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.utility.universal;

import net.zithium.library.version.XMaterial;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.*;

/**
 * <b>XBlock</b> - MaterialData/BlockData Support<br>
 * BlockState (Old): https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockState.html
 * BlockData (New): https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/BlockData.html
 * MaterialData (Old): https://hub.spigotmc.org/javadocs/spigot/org/bukkit/material/MaterialData.html
 * <p>
 * All the parameters are non-null except the ones marked as nullable.
 * This class doesn't and shouldn't support materials that are {@link Material#isLegacy()}.
 *
 * @author Crypto Morin
 * @version 2.2.0
 * @see Block
 * @see BlockState
 * @see MaterialData
 * @see XMaterial
 */

public final class XBlock {

    public static boolean isCrop(Block block) {
        if(XMaterial.supports(13)) {
            return block.getBlockData() instanceof org.bukkit.block.data.Ageable;
        }

        BlockState state = block.getState();
        MaterialData data = state.getData();
        return data instanceof Crops;
    }

    public static boolean isFullyGrown(Block block) {
        if (XMaterial.supports(13)) {
            org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable) block.getBlockData();
            return ageable.getAge() == ageable.getMaximumAge();
        }

        BlockState state = block.getState();
        MaterialData data = state.getData();
        return (((Crops) data).getState() == CropState.RIPE);
    }
}