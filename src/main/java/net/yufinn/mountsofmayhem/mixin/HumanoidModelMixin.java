package net.yufinn.mountsofmayhem.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.yufinn.mountsofmayhem.client.SpearAnimations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
    private void mountsofmayhem$trackPosedEntity(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        SpearAnimations.poseEntity = entity;
    }

    @Inject(method = "poseRightArm(Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("TAIL"))
    private void mountsofmayhem$poseRightArm(T entity, CallbackInfo ci) {
        if (SpearAnimations.usesSpearPose(entity, HumanoidArm.RIGHT)) {
            SpearAnimations.thirdPersonHandUse((HumanoidModel<?>) (Object) this, entity, HumanoidArm.RIGHT);
        }
    }

    @Inject(method = "poseLeftArm(Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("TAIL"))
    private void mountsofmayhem$poseLeftArm(T entity, CallbackInfo ci) {
        if (SpearAnimations.usesSpearPose(entity, HumanoidArm.LEFT)) {
            SpearAnimations.thirdPersonHandUse((HumanoidModel<?>) (Object) this, entity, HumanoidArm.LEFT);
        }
    }

    @Inject(method = "setupAttackAnimation(Lnet/minecraft/world/entity/LivingEntity;F)V", at = @At("HEAD"), cancellable = true)
    private void mountsofmayhem$setupStabAnimation(T entity, float ageInTicks, CallbackInfo ci) {
        HumanoidArm swingArm = SpearAnimations.jabArm(entity);
        if (swingArm == null) return;
        float swing = SpearAnimations.jabProgress(entity, Minecraft.getInstance().getFrameTime());
        if (swing <= 0.0F) return;
        HumanoidModel<?> model = (HumanoidModel<?>) (Object) this;
        model.body.yRot = Mth.sin(Mth.sqrt(swing) * ((float) Math.PI * 2.0F)) * 0.2F;
        if (swingArm == HumanoidArm.LEFT) model.body.yRot *= -1.0F;
        model.rightArm.z = Mth.sin(model.body.yRot) * 5.0F;
        model.rightArm.x = -Mth.cos(model.body.yRot) * 5.0F;
        model.leftArm.z = -Mth.sin(model.body.yRot) * 5.0F;
        model.leftArm.x = Mth.cos(model.body.yRot) * 5.0F;
        SpearAnimations.thirdPersonAttackHand(model, swing, swingArm);
        ci.cancel();
    }
}
