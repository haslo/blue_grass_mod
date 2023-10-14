/*
 * Copyright © 2023 haslo <haslo@haslo.ch>
 *
 * This file is part of BlueGrass.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package ch.haslo.bluegrass;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

class BlueGrassColorProvider implements BlockColorProvider {
    @Override
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos blockPos, int tintIndex) {
        // System.out.println("Getting color somewhere (sorry about the spam)");
        assert blockPos != null;
        World minecraftWorld = MinecraftClient.getInstance().world;
        assert minecraftWorld != null;
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        // System.out.println("Getting color values");
        for (int xOffset = -1; xOffset <= 1; ++xOffset) {
            for (int zOffset = -1; zOffset <= 1; ++zOffset) {
                RegistryEntry<Biome> registryEntry = minecraftWorld.getBiome(blockPos.add(xOffset, 0, zOffset));
                Biome biome = registryEntry.value();
                int grassColor = biome.getGrassColorAt(blockPos.getX() + xOffset, blockPos.getZ() + zOffset);
                // System.out.println("Grass Color: 0x" + Integer.toHexString(grassColor));
                totalRed += (grassColor & 16711680) >> 16;
                totalGreen += (grassColor & 65280) >> 8;
                totalBlue += grassColor & 255;
            }
        }
        int averageRed = totalRed / 9;
        int averageGreen = totalGreen / 9;
        int averageBlue = totalBlue / 9;
        int scaledRed = Math.min(255, (int) (averageRed / 1.5));
        int scaledBlue = Math.min(255, (int) (averageGreen * 1.5));
        int scaledGreen = Math.min(255, (int) (averageBlue / 1.5));
        // System.out.println("Color values RGB average: " +
        //         averageRed + "," +
        //         averageGreen + "," +
        //         averageBlue + "" +
        //         ", RGB scaled: " +
        //         scaledRed + "," +
        //         scaledGreen + "," +
        //         scaledBlue
        // );
        int color = 0xFF000000 | scaledRed << 16 | scaledGreen << 8 | scaledBlue;
        // System.out.println("returning: 0x" + Integer.toHexString(color));
        return color;
    }
}
