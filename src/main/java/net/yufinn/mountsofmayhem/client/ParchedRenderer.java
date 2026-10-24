package net.yufinn.mountsofmayhem.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public class ParchedRenderer extends SkeletonRenderer {
    public static final net.minecraft.client.model.geom.ModelLayerLocation LAYER = new net.minecraft.client.model.geom.ModelLayerLocation(MountsOfMayhem.id("parched"), "main");
    private static final ResourceLocation TEXTURE = MountsOfMayhem.id("textures/entity/skeleton/parched.png");

    public ParchedRenderer(EntityRendererProvider.Context context) {
        super(context, LAYER, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractSkeleton skeleton) {
        return TEXTURE;
    }
}
