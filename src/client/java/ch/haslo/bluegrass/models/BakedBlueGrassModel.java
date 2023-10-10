package ch.haslo.bluegrass.models;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BakedBlueGrassModel implements BakedModel {
    private final Sprite sprite;

    public BakedBlueGrassModel(Sprite sprite) {
        this.sprite = sprite;
        if (this.sprite == null) {
            System.out.println("Sprite is null");
        } else {
            System.out.println("Sprite loaded");
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        System.out.println("Getting quads for " + state + " / " + face + " / " + random);
        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().getBakedModelManager() == null) {
            System.out.println("MinecraftClient or BakedModelManager is null");
            return Collections.emptyList();
        }
        BakedModel originalModel = MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier(new Identifier("minecraft", "grass_block"), ""));
        if (originalModel == null) {
            System.out.println("originalModel is null");
            return Collections.emptyList();
        }
        List<BakedQuad> quads = new ArrayList<>(originalModel.getQuads(state, face, random));
        // if (!quads.isEmpty()) {
        //     System.out.println("Original vertex data length: " + quads.get(0).getVertexData().length);
        // }
        if (face == null || face == Direction.DOWN) {
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
                    blue = Math.min(255, blue * 2);  // Multiply blue component by 2, capped at 255
                    int newColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    vertexData[i + 3] = newColor;
                    // System.out.println("Blue tint applied to " + i);
                }
                BakedQuad newQuad = new BakedQuad(vertexData, -1, quad.getFace(), quad.getSprite(), quad.hasShade());
                newQuads.add(newQuad);
            }
            quads.clear();
            quads.addAll(newQuads);
        } else if (face == Direction.NORTH || face == Direction.SOUTH || face == Direction.EAST || face == Direction.WEST) {
            int[] vertexData = new int[]{
                    // {x, y, z, color, u, v, light, normal}
                    0, 0, 0, -1, 0, 16, 0, 0,
                    16, 0, 0, -1, 16, 16, 0, 0,
                    16, 16, 0, -1, 16, 0, 0, 0,
                    0, 16, 0, -1, 0, 0, 0, 0
            };
            BakedQuad customQuad = new BakedQuad(vertexData, -1, face, sprite, true);
            quads.clear();
            quads.add(customQuad);
        }
        if (quads.isEmpty()) {
            System.out.println("Custom vertex data is empty");
        }
        // else {
        //     System.out.println("Custom vertex data length: " + quads.get(0).getVertexData().length);
        // }
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
