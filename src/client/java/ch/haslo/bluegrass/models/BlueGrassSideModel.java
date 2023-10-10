package ch.haslo.bluegrass.models;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BlueGrassSideModel implements BakedModel {
    private final Sprite sprite;

    public BlueGrassSideModel(Function<Identifier, Sprite> spriteGetter) {
        Identifier identifier = new Identifier("bluegrass", "block/grass_block_side");
        this.sprite = spriteGetter.apply(identifier);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        BakedModel originalModel = MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier(new Identifier("minecraft", "grass_block"), ""));
        List<BakedQuad> quads = new ArrayList<>(originalModel.getQuads(state, face, random));
        if (face == null) {
            return originalModel.getQuads(state, face, random);
        }
        if (face == Direction.UP) {
            List<BakedQuad> newQuads = new ArrayList<>();
            for (BakedQuad quad : quads) {
                int[] vertexData = quad.getVertexData().clone();  // Clone to avoid modifying the original
                for (int i = 0; i < vertexData.length; i += 8) {  // 8 integers per vertex
                    int color = vertexData[i + 3];
                    int alpha = color >> 24 & 255;
                    int red = color >> 16 & 255;
                    int green = color >> 8 & 255;
                    int blue = color & 255;

                    // Multiply to apply blue tint
                    blue = Math.min(255, blue * 2);  // Multiply blue component by 2, capped at 255

                    int newColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    vertexData[i + 3] = newColor;
                }
                BakedQuad newQuad = new BakedQuad(vertexData, -1, quad.getFace(), quad.getSprite(), quad.hasShade());
                newQuads.add(newQuad);
            }
            quads.clear();
            quads.addAll(newQuads);
        } else if (face == Direction.NORTH || face == Direction.SOUTH || face == Direction.EAST || face == Direction.WEST) {
            int[] vertexData = new int[]{
                    // {x, y, z, color, u, v, light}
                    0, 0, 0, -1, 0, 16, 0,
                    16, 0, 0, -1, 16, 16, 0,
                    16, 16, 0, -1, 16, 0, 0,
                    0, 16, 0, -1, 0, 0, 0
            };
            BakedQuad customQuad = new BakedQuad(vertexData, -1, face, sprite, true);
            quads.clear();
            quads.add(customQuad);
        }
        // south, noop
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return sprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }
}
