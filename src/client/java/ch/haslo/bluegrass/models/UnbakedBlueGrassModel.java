package ch.haslo.bluegrass.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class UnbakedBlueGrassModel implements UnbakedModel {
    private final Identifier textureId;

    public UnbakedBlueGrassModel(Identifier textureId) {
        this.textureId = textureId;
    }

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

        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        emitQuadsForDirections(emitter, topSprite, sideSprite, bottomSprite);

        Mesh mesh = builder.build();
        return new BakedBlueGrassModel(new Sprite[]{topSprite, sideSprite, bottomSprite}, mesh);
    }

    void emitQuadsForDirections(QuadEmitter emitter, Sprite topSprite, Sprite sideSprite, Sprite bottomSprite) {
        for (Direction direction : Direction.values()) {
            emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            Sprite spriteToUse;
            if (direction == Direction.UP) {
                spriteToUse = topSprite;
            } else if (direction == Direction.DOWN) {
                spriteToUse = bottomSprite;
            } else {
                spriteToUse = sideSprite;
            }
            emitter.spriteBake(0, spriteToUse, MutableQuadView.BAKE_LOCK_UV);
            emitter.spriteColor(0, -1, -1, -1, -1);
            emitter.emit();
        }
    }
}
