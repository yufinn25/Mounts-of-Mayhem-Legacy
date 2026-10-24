package net.yufinn.mountsofmayhem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.yufinn.mountsofmayhem.entity.NautilusEntity;
import net.yufinn.mountsofmayhem.item.NautilusArmorItem;

public class NautilusArmorLayer extends RenderLayer<NautilusEntity, NautilusModel> {
    private final NautilusModel model;

    public NautilusArmorLayer(RenderLayerParent<NautilusEntity, NautilusModel> parent, EntityModelSet models) {
        super(parent);
        this.model = new NautilusModel(models.bakeLayer(NautilusModel.ARMOR_LAYER));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, NautilusEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (entity.isBaby() || !(stack.getItem() instanceof NautilusArmorItem armor)) return;
        coloredCutoutModelCopyLayerRender(getParentModel(), this.model, armor.getTexture(), pose, buffer, light, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, 1.0F, 1.0F, 1.0F);
    }
}
