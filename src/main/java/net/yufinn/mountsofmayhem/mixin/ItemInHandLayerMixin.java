package net.yufinn.mountsofmayhem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.yufinn.mountsofmayhem.client.SpearAnimations;
import net.yufinn.mountsofmayhem.item.SpearItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {
    @Inject(
            method = "renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void mountsofmayhem$spearTransforms(LivingEntity entity, ItemStack stack, ItemDisplayContext context, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int light, CallbackInfo ci) {
        float partial = Minecraft.getInstance().getFrameTime();
        float jab = SpearAnimations.jabProgress(entity, partial);
        if (jab > 0.0F && SpearAnimations.jabArm(entity) == arm) {
            SpearAnimations.thirdPersonAttackItem(poseStack, jab, stack);
        }
        HumanoidArm usedArm = entity.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
        if (entity.isUsingItem() && usedArm == arm && stack.getItem() instanceof SpearItem spear) {
            SpearAnimations.thirdPersonUseItem(SpearAnimations.ticksSinceHitFeedback(entity, partial), poseStack, jab, entity.getTicksUsingItem() + partial, arm, spear);
        }
    }
}
