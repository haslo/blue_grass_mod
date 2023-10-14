/*
 * Copyright © 2023 haslo <haslo@haslo.ch>
 *
 * This file is part of BlueGrass.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package ch.haslo.bluegrass;

import ch.haslo.bluegrass.models.UnbakedBlueGrassModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class BlueGrassClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("BlueGrassClient initializing");
        System.out.println("Registering ModelLoadingPlugin");
        ModelLoadingPlugin.register(pluginCtx -> pluginCtx.modifyModelOnLoad().register(ModelModifier.WRAP_PHASE, (model, context) -> {
            if (context.id() instanceof ModelIdentifier modelId) {
                if (modelId.getNamespace().equals("minecraft") && modelId.getPath().equals("grass_block") && modelId.getVariant().equals("snowy=false")) {
                    System.out.println("Model ID: " + modelId);
                    System.out.println("Inside non-snowy grass block logic");
                    return new UnbakedBlueGrassModel();
                }
            }
            return model;
        }));
        System.out.println("Registering ColorProviderRegistry");
        ColorProviderRegistry.BLOCK.register(new GrassColorProvider(), Blocks.GRASS_BLOCK);
        ColorProviderRegistry.BLOCK.register(new GrassColorProvider(), Blocks.GRASS);
        System.out.println("BlueGrassClient initialized");
    }

    private static class GrassColorProvider implements BlockColorProvider {
        @Override
        public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos blockPos, int tintIndex) {
            System.out.println("Getting color somewhere (sorry about the spam)");
            assert blockPos != null;
            World minecraftWorld = MinecraftClient.getInstance().world;
            assert minecraftWorld != null;
            int totalRed = 0;
            int totalGreen = 0;
            int totalBlue = 0;
            System.out.println("Getting color values");
            for (int xOffset = -1; xOffset <= 1; ++xOffset) {
                for (int zOffset = -1; zOffset <= 1; ++zOffset) {
                    RegistryEntry<Biome> registryEntry = minecraftWorld.getBiome(blockPos.add(xOffset, 0, zOffset));
                    Biome biome = registryEntry.value();
                    int grassColor = biome.getGrassColorAt(blockPos.getX() + xOffset, blockPos.getZ() + zOffset);
                    System.out.println("Grass Color: 0x" + Integer.toHexString(grassColor));
                    totalRed += (grassColor & 16711680) >> 16;
                    totalGreen += (grassColor & 65280) >> 8;
                    totalBlue += grassColor & 255;
                }
            }
            int color = 0xFF000000 | (totalRed / 9 & 255) << 16 | (totalGreen / 9 & 255) << 8 | totalBlue / 9 & 255;
            System.out.println("returning: 0x" + Integer.toHexString(color));
            return color;
        }
    }
}
