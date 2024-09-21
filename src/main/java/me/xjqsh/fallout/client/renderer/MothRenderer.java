package me.xjqsh.fallout.client.renderer;

import me.xjqsh.fallout.client.model.MothModel;
import me.xjqsh.fallout.entity.Moth;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MothRenderer extends GeoEntityRenderer<Moth> {
    public MothRenderer(EntityRendererProvider.Context context) {
        super(context, new MothModel());
    }

    @Override
    protected float getDeathMaxRotation(Moth animatable) {
        return 0f;
    }
}