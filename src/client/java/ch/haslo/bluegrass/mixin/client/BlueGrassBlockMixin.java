package ch.haslo.bluegrass.mixin.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.GrassBlock;
import net.minecraft.client.render.block
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrassBlock.class)
public class BlueGrassBlockMixin {

    @Inject(method = "getBlockModel", at = @At("RETURN"), cancellable = true)
    private void changeSideTexture(BlockState state, CallbackInfo returnInfo) {
        // Replace the side texture with your custom blue grass texture
        // You'll need to replace this comment with the actual code to change the texture
    }

    @Inject(method = "getTopBlockColor", at = @At("HEAD"), cancellable = true)
    private void changeTopTint(CallbackInfo returnInfo) {
        // Replace the top tint with blue
        // You'll need to replace this comment with the actual code to change the tint
    }
}
