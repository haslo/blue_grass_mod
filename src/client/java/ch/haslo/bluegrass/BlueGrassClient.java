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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ModelIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlueGrassClient implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
        LOGGER.info("BlueGrassClient initializing");
        LOGGER.info("Registering ModelLoadingPlugin");
        ModelLoadingPlugin.register(pluginCtx -> pluginCtx.modifyModelOnLoad().register(ModelModifier.WRAP_PHASE, (model, context) -> {
            if (context.id() instanceof ModelIdentifier modelId) {
                if (modelId.getNamespace().equals("minecraft") && modelId.getPath().equals("grass_block") && modelId.getVariant().equals("snowy=false")) {
                    LOGGER.info("Model ID: " + modelId);
                    LOGGER.info("Inside non-snowy grass block logic");
                    return new UnbakedBlueGrassModel();
                }
            }
            return model;
        }));
        LOGGER.info("Registering ColorProviderRegistry");
        BlueGrassColorProvider blueGrassColorProvider = new BlueGrassColorProvider();
        Block[] blocksToRegister = {Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.TALL_GRASS};
        for (Block block : blocksToRegister) {
            ColorProviderRegistry.BLOCK.register(blueGrassColorProvider, block);
        }
        LOGGER.info("BlueGrassClient initialized");
    }
}
