package me.xjqsh.fallout.client.model;

import me.xjqsh.fallout.MobMod;
import me.xjqsh.fallout.entity.Moth;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MothModel extends GeoModel<Moth> {
	private final ResourceLocation model = new ResourceLocation(MobMod.MOD_ID, "geo/moth.geo.json");
	private final ResourceLocation texture = new ResourceLocation(MobMod.MOD_ID, "textures/mob/moth.png");
	private final ResourceLocation animations = new ResourceLocation(MobMod.MOD_ID, "animations/moth.animation.json");

	@Override
	public ResourceLocation getModelResource(Moth moth) {
		return model;
	}

	@Override
	public ResourceLocation getTextureResource(Moth moth) {
		return texture;
	}

	@Override
	public ResourceLocation getAnimationResource(Moth moth) {
		return animations;
	}
}