/*
 * Copyright © 2023 haslo <haslo@haslo.ch>
 *
 * This file is part of BlueGrass.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package ch.haslo.bluegrass.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class UnbakedBlueGrassModel implements UnbakedModel {
    private static final Logger LOGGER = LogManager.getLogger();
    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
        // No-op, as we don't have parent models.
    }

    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        LOGGER.info("Baking grass block now");
        Sprite topSprite = getSprite(textureGetter, "textures/atlas/blocks.png", "minecraft", "block/grass_block_top");
        Sprite sideSprite = getSprite(textureGetter, "textures/atlas/blocks.png", "bluegrass", "block/grass_block_side");
        Sprite bottomSprite = getSprite(textureGetter, "textures/atlas/blocks.png", "minecraft", "block/dirt");
        return new BakedBlueGrassModel(new Sprite[]{topSprite, sideSprite, bottomSprite});
    }

    private Sprite getSprite(Function<SpriteIdentifier, Sprite> textureGetter, String atlas, String namespace, String texture) {
        SpriteIdentifier identifier = new SpriteIdentifier(
                new Identifier("minecraft", atlas),
                new Identifier(namespace, texture)
        );
        Sprite sprite = textureGetter.apply(identifier);
        debugSprite(sprite);
        return sprite;
    }

    void debugSprite(Sprite sprite) {
        if (sprite == null) {
            LOGGER.warn("Sprite is null");
            return;
        }
        SpriteContents contents = sprite.getContents();
        if (contents == null) {
            LOGGER.warn("Contents are null");
        } else {
            LOGGER.info("Contents are " + contents);
        }
    }

    void emitQuadsForDirections(QuadEmitter emitter, Sprite topSprite, Sprite sideSprite, Sprite bottomSprite) {
        for (Direction direction : Direction.values()) {
            emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            Sprite spriteToUse;
            int color = 0xFFFFFFFF;
            if (direction == Direction.UP) {
                spriteToUse = topSprite;
                color = 0xFF0000FF;
            } else if (direction == Direction.DOWN) {
                spriteToUse = bottomSprite;
            } else {
                spriteToUse = sideSprite;
            }
            emitter.spriteBake(spriteToUse, QuadEmitter.BAKE_LOCK_UV);
            for (int i = 0; i < 4; i++) {
                emitter.color(i, color);
            }
            emitter.emit();
        }
    }
}
