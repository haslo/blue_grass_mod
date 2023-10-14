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
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class UnbakedBlueGrassModel implements UnbakedModel {
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
        System.out.println("Baking grass block now");
        SpriteIdentifier topIdentifier = new SpriteIdentifier(
                new Identifier("minecraft", "textures/atlas/blocks.png"),
                new Identifier("minecraft", "block/grass_block_top")
        );
        SpriteIdentifier sideIdentifier = new SpriteIdentifier(
                new Identifier("minecraft", "textures/atlas/blocks.png"),
                new Identifier("bluegrass", "block/grass_block_side")
        );
        SpriteIdentifier bottomIdentifier = new SpriteIdentifier(
                new Identifier("minecraft", "textures/atlas/blocks.png"),
                new Identifier("minecraft", "block/dirt")
        );

        Sprite topSprite = textureGetter.apply(topIdentifier);
        Sprite sideSprite = textureGetter.apply(sideIdentifier);
        Sprite bottomSprite = textureGetter.apply(bottomIdentifier);

        debugSprite(topSprite);
        debugSprite(sideSprite);
        debugSprite(bottomSprite);

        return new BakedBlueGrassModel(new Sprite[]{topSprite, sideSprite, bottomSprite});
    }

    void debugSprite(Sprite sprite) {
        if (sprite == null) {
            System.out.println("Sprite is null");
            return;
        }
        SpriteContents contents = sprite.getContents();
        if (contents == null) {
            System.out.println("Contents are null");
        } else {
            System.out.println("Contents are " + contents);
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
