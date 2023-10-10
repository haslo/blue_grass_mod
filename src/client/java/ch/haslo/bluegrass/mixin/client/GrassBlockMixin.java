package ch.haslo.bluegrass.mixin.client;

import ch.haslo.bluegrass.models.BlueGrassSideModel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(BakedModelManager.class)
public class GrassBlockMixin {
    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    public void getModel(ModelIdentifier id, CallbackInfoReturnable<BakedModel> cir) {
        System.out.println("Requested model: " + id.toString());
        if ("minecraft".equals(id.getNamespace()) && "grass_block".equals(id.getPath())) {
            Identifier blockAtlasId = new Identifier("minecraft", "textures/atlas/blocks.png");
            Function<Identifier, Sprite> spriteGetter = MinecraftClient.getInstance().getSpriteAtlas(blockAtlasId);
            BakedModel customModel = new BlueGrassSideModel(spriteGetter);
            cir.setReturnValue(customModel);
            System.out.println("Mixin applied!");
        }
    }
}
