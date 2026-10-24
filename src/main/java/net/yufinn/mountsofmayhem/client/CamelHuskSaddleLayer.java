package net.yufinn.mountsofmayhem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CamelModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.camel.Camel;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public class CamelHuskSaddleLayer extends RenderLayer<Camel, CamelModel<Camel>> {
    private static final ResourceLocation SADDLE = MountsOfMayhem.id("textures/entity/equipment/camel_husk_saddle/saddle.png");

    public CamelHuskSaddleLayer(RenderLayerParent<Camel, CamelModel<Camel>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Camel camel, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!camel.isSaddled()) return;
        renderColoredCutoutModel(this.getParentModel(), SADDLE, poseStack, buffer, packedLight, camel, 1.0F, 1.0F, 1.0F);
    }
}
