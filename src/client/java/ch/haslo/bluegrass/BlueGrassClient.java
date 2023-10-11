package ch.haslo.bluegrass;

import ch.haslo.bluegrass.models.BakedBlueGrassModel;
import ch.haslo.bluegrass.models.UnbakedBlueGrassModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class BlueGrassClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Register your ModelLoadingPlugin
		ModelLoadingPlugin.register(pluginCtx -> {
			pluginCtx.modifyModelOnLoad().register(ModelModifier.WRAP_PHASE, (model, context) -> {
				if (context.id() instanceof ModelIdentifier modelId) {
					System.out.println("Model ID: " + modelId);  // Debug print

					if (modelId.getNamespace().equals("minecraft") && modelId.getPath().equals("grass_block") && modelId.getVariant().equals("snowy=false")) {
						System.out.println("Inside non-snowy grass block logic");
						return new UnbakedBlueGrassModel(modelId);
					}
				}
				return model;
			});
		});
	}
}
