package net.yufinn.mountsofmayhem.mixin;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.yufinn.mountsofmayhem.client.SpearAnimations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimationUtils.class)
public abstract class AnimationUtilsMixin {
    @Inject(method = "animateZombieArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;ZFF)V", at = @At("HEAD"), cancellable = true)
    private static void mountsofmayhem$skipForSpear(net.minecraft.client.model.geom.ModelPart leftArm, net.minecraft.client.model.geom.ModelPart rightArm, boolean aggressive, float attackTime, float ageInTicks, CallbackInfo ci) {
        LivingEntity entity = SpearAnimations.poseEntity;
        if (entity != null && (SpearAnimations.usesSpearPose(entity, HumanoidArm.RIGHT) || SpearAnimations.usesSpearPose(entity, HumanoidArm.LEFT))) ci.cancel();
    }
}
