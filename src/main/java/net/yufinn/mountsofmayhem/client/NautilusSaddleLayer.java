package net.yufinn.mountsofmayhem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.entity.NautilusEntity;

public class NautilusSaddleLayer extends RenderLayer<NautilusEntity, NautilusModel> {
    private static final ResourceLocation TEXTURE = MountsOfMayhem.id("textures/entity/equipment/nautilus_saddle/saddle.png");
    private final NautilusModel model;

    public NautilusSaddleLayer(RenderLayerParent<NautilusEntity, NautilusModel> parent, EntityModelSet models) {
        super(parent);
        this.model = new NautilusModel(models.bakeLayer(NautilusModel.SADDLE_LAYER));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, NautilusEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isSaddled() || entity.isBaby()) return;
        coloredCutoutModelCopyLayerRender(getParentModel(), this.model, TEXTURE, pose, buffer, light, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, 1.0F, 1.0F, 1.0F);
    }
}
