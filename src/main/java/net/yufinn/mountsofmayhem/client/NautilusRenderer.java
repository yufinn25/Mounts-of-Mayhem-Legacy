package net.yufinn.mountsofmayhem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.entity.NautilusEntity;

public class NautilusRenderer extends MobRenderer<NautilusEntity, NautilusModel> {
    private static final ResourceLocation ADULT = MountsOfMayhem.id("textures/entity/nautilus/nautilus.png");
    private static final ResourceLocation BABY = MountsOfMayhem.id("textures/entity/nautilus/nautilus_baby.png");
    protected final NautilusModel adultModel;
    protected final NautilusModel babyModel;

    public NautilusRenderer(EntityRendererProvider.Context context) {
        super(context, new NautilusModel(context.bakeLayer(NautilusModel.LAYER)), 0.5F);
        this.adultModel = this.model;
        this.babyModel = new NautilusModel(context.bakeLayer(NautilusModel.BABY_LAYER));
        this.addLayer(new NautilusSaddleLayer(this, context.getModelSet()));
        this.addLayer(new NautilusArmorLayer(this, context.getModelSet()));
    }

    @Override
    public void render(NautilusEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int light) {
        this.model = modelFor(entity);
        this.shadowRadius = entity.isBaby() ? 0.25F : 0.5F;
        super.render(entity, yaw, partialTick, pose, buffer, light);
    }

    protected NautilusModel modelFor(NautilusEntity entity) {
        return entity.isBaby() ? this.babyModel : this.adultModel;
    }

    @Override
    public ResourceLocation getTextureLocation(NautilusEntity entity) {
        return entity.isBaby() ? BABY : ADULT;
    }
}
