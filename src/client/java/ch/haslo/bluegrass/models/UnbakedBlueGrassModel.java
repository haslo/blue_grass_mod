package ch.haslo.bluegrass.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
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
        SpriteIdentifier spriteIdentifier = new SpriteIdentifier(new Identifier("minecraft", "textures/atlas/blocks.png"), new Identifier("bluegrass", "block/grass_block_side"));
        Sprite sprite = textureGetter.apply(spriteIdentifier);
        return new BakedBlueGrassModel(sprite);
    }
}
