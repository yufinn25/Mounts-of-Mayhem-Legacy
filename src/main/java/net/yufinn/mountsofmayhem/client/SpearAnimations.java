package net.yufinn.mountsofmayhem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.yufinn.mountsofmayhem.item.SpearItem;

public final class SpearAnimations {
    private SpearAnimations() {}

    public static LivingEntity poseEntity;

    private static final java.util.Map<LivingEntity, Long> HIT_FEEDBACK = new java.util.WeakHashMap<>();

    public static void onKineticHit(int entityId) {
        net.minecraft.client.multiplayer.ClientLevel level = net.minecraft.client.Minecraft.getInstance().level;
        if (level == null) return;
        net.minecraft.world.entity.Entity entity = level.getEntity(entityId);
        if (!(entity instanceof LivingEntity living)) return;
        Long last = HIT_FEEDBACK.get(living);
        if (last == null || level.getGameTime() - last > 10L) {
            HIT_FEEDBACK.put(living, level.getGameTime());

            net.minecraft.world.item.ItemStack use = living.getUseItem();
            if (use.getItem() instanceof SpearItem) {
                net.minecraft.sounds.SoundEvent sound = net.yufinn.mountsofmayhem.combat.SpearAttacks.wood(use) ? net.yufinn.mountsofmayhem.entity.ModSounds.ITEM_SPEAR_WOOD_HIT.get() : net.yufinn.mountsofmayhem.entity.ModSounds.ITEM_SPEAR_HIT.get();
                level.playLocalSound(living.getX(), living.getY(), living.getZ(), sound, living.getSoundSource(), 1.0F, 1.0F, false);
            }
        }
    }

    public static float ticksSinceHitFeedback(LivingEntity entity, float partialTick) {
        Long last = HIT_FEEDBACK.get(entity);
        return last == null ? 0.0F : (float) (entity.level().getGameTime() - last) + partialTick;
    }

    private static float hitFeedbackAmount(float ticksSinceFeedbackStart) {
        return 0.4F * (Ease.outQuart(progress(ticksSinceFeedbackStart, 1.0F, 3.0F)) - Ease.inOutSine(progress(ticksSinceFeedbackStart, 3.0F, 10.0F)));
    }

    private record Jab(long start, net.minecraft.world.InteractionHand hand, int duration) {}

    private static final java.util.Map<LivingEntity, Jab> JABS = new java.util.WeakHashMap<>();

    public static void trackSwing(LivingEntity entity) {
        if (entity.swinging && entity.swingTime == -1 && entity.getItemInHand(entity.swingingArm).getItem() instanceof SpearItem spear) {
            JABS.put(entity, new Jab(entity.level().getGameTime(), entity.swingingArm, spear.getJabDuration()));
        }
    }

    public static float jabProgress(LivingEntity entity, float partialTick) {
        Jab jab = JABS.get(entity);
        if (jab == null) return 0.0F;
        float progress = (entity.level().getGameTime() - jab.start() + partialTick) / jab.duration();
        if (progress >= 1.0F) {
            JABS.remove(entity);
            return 0.0F;
        }
        return Math.max(progress, 0.0F);
    }

    public static float jabProgress(LivingEntity entity, SpearItem spear, float partialTick) {
        return jabProgress(entity, partialTick);
    }

    public static HumanoidArm jabArm(LivingEntity entity) {
        Jab jab = JABS.get(entity);
        if (jab == null) return null;
        return jab.hand() == net.minecraft.world.InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
    }

    public static boolean usesSpearPose(LivingEntity entity, HumanoidArm arm) {
        return jabArm(entity) == arm || (arm == entity.getMainArm() ? entity.getMainHandItem() : entity.getOffhandItem()).getItem() instanceof SpearItem;
    }

    private static float progress(float time, float start, float end) {
        return Mth.clamp(Mth.inverseLerp(time, start, end), 0.0F, 1.0F);
    }

    private static void rotateAround(PoseStack poseStack, org.joml.Quaternionf rotation, float x, float y, float z) {
        poseStack.translate(x, y, z);
        poseStack.mulPose(rotation);
        poseStack.translate(-x, -y, -z);
    }

    public static void thirdPersonHandUse(HumanoidModel<?> model, LivingEntity entity, HumanoidArm holdingArm) {
        ModelPart arm = holdingArm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        ModelPart head = model.head;
        int invert = holdingArm == HumanoidArm.RIGHT ? 1 : -1;
        arm.yRot = -0.1F * invert + head.yRot;
        arm.xRot = (-(float) Math.PI / 2.0F) + head.xRot + 0.8F;
        if (entity.isFallFlying() || model.swimAmount > 0.0F) arm.xRot -= 0.9599311F;
        arm.yRot = ((float) Math.PI / 180.0F) * Mth.clamp((180.0F / (float) Math.PI) * arm.yRot, -60.0F, 60.0F);
        arm.xRot = ((float) Math.PI / 180.0F) * Mth.clamp((180.0F / (float) Math.PI) * arm.xRot, -120.0F, 30.0F);
        float time = entity.isUsingItem() ? entity.getTicksUsingItem() + net.minecraft.client.Minecraft.getInstance().getFrameTime() : 0.0F;
        HumanoidArm usedArm = entity.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
        if (time > 0.0F && (!entity.isUsingItem() || usedArm == holdingArm) && entity.getUseItem().getItem() instanceof SpearItem spear) {
            UseParams p = UseParams.of(spear.getChargeStats(), time);
            arm.yRot += -invert * p.swayScaleFast * ((float) Math.PI / 180.0F) * p.swayIntensity * 1.0F;
            arm.zRot += -invert * p.swayScaleSlow * ((float) Math.PI / 180.0F) * p.swayIntensity * 0.5F;
            arm.xRot += ((float) Math.PI / 180.0F) * (-40.0F * p.raiseProgressStart + 30.0F * p.raiseProgressMiddle + -20.0F * p.raiseProgressEnd + 20.0F * p.lowerProgress + 10.0F * p.raiseBackProgress + 0.6F * p.swayScaleSlow * p.swayIntensity);
        }
    }

    public static void thirdPersonAttackHand(HumanoidModel<?> model, float animation, HumanoidArm arm) {
        float prepare = Ease.inOutSine(progress(animation, 0.0F, 0.05F));
        float attack = Ease.inQuad(progress(animation, 0.05F, 0.2F));
        float retract = Ease.inOutExpo(progress(animation, 0.4F, 1.0F));
        ModelPart part = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        part.xRot += (90.0F * prepare - 120.0F * attack + 30.0F * retract) * ((float) Math.PI / 180.0F);
    }

    private static final float FORWARD_MOVEMENT = 0.38F;

    public static void thirdPersonAttackItem(PoseStack poseStack, float animation, ItemStack item) {
        if (animation <= 0.0F) return;
        float jetForward = item.getItem() instanceof SpearItem ? FORWARD_MOVEMENT : 0.0F;
        float attack = Ease.inQuad(progress(animation, 0.05F, 0.2F));
        float retract = Ease.inOutExpo(progress(animation, 0.4F, 1.0F));
        rotateAround(poseStack, Axis.XN.rotationDegrees(70.0F * (attack - retract)), 0.0F, -0.125F, 0.125F);
        poseStack.translate(0.0F, jetForward * (attack - retract), 0.0F);
    }

    public static void thirdPersonUseItem(float ticksSinceHitFeedback, PoseStack poseStack, float animation, float timeHeld, HumanoidArm arm, SpearItem spear) {
        if (timeHeld == 0.0F) return;
        float attack = Ease.inQuad(progress(animation, 0.05F, 0.2F));
        float retract = Ease.inOutExpo(progress(animation, 0.4F, 1.0F));
        UseParams p = UseParams.of(spear.getChargeStats(), timeHeld);
        int invert = arm == HumanoidArm.RIGHT ? 1 : -1;
        float raiseProgressModified = 1.0F - Ease.outBack(1.0F - p.raiseProgress);
        float hitFeedback = hitFeedbackAmount(ticksSinceHitFeedback);
        poseStack.translate(0.0F, -hitFeedback * 0.4F, -FORWARD_MOVEMENT * (raiseProgressModified - p.raiseBackProgress) + hitFeedback);
        rotateAround(poseStack, Axis.XN.rotationDegrees(70.0F * (p.raiseProgress - p.raiseBackProgress) - 40.0F * (attack - retract)), 0.0F, -0.03125F, 0.125F);
        rotateAround(poseStack, Axis.YP.rotationDegrees(invert * 90 * (p.raiseProgress - p.swayProgress + 3.0F * retract + attack)), 0.0F, 0.0F, 0.125F);
    }

    public static void firstPersonUse(float ticksSinceHitFeedback, PoseStack poseStack, float timeHeld, HumanoidArm arm, SpearItem spear) {
        UseParams p = UseParams.of(spear.getChargeStats(), timeHeld);
        int invert = arm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(invert * (p.raiseProgress * 0.15F + p.raiseProgressEnd * -0.05F + p.swayProgress * -0.1F + p.swayScaleSlow * 0.005F), p.raiseProgress * -0.075F + p.raiseProgressMiddle * 0.075F + p.swayScaleFast * 0.01F, p.raiseProgressStart * 0.05F + p.raiseProgressEnd * -0.05F + p.swayScaleSlow * 0.005F);
        rotateAround(poseStack, Axis.XP.rotationDegrees(-65.0F * Ease.inOutBack(p.raiseProgress) - 35.0F * p.lowerProgress + 100.0F * p.raiseBackProgress + -0.5F * p.swayScaleFast), 0.0F, 0.1F, 0.0F);
        rotateAround(poseStack, Axis.YN.rotationDegrees(invert * (-90.0F * progress(p.raiseProgress, 0.5F, 0.55F) + 90.0F * p.swayProgress + 2.0F * p.swayScaleSlow)), invert * 0.15F, 0.0F, 0.0F);
        poseStack.translate(0.0F, -hitFeedbackAmount(ticksSinceHitFeedback), 0.0F);
    }

    public static void firstPersonAttack(float attack, PoseStack poseStack, int invert) {
        float start = Ease.inOutSine(progress(attack, 0.0F, 0.05F));
        float middle = Ease.outBack(progress(attack, 0.05F, 0.2F));
        float ending = Ease.inOutExpo(progress(attack, 0.4F, 1.0F));
        poseStack.translate(invert * 0.1F * (start - middle), -0.075F * (start - ending), 0.65F * (start - middle));
        poseStack.mulPose(Axis.XP.rotationDegrees(-70.0F * (start - ending)));
        poseStack.translate(0.0F, 0.0F, -0.25F * (ending - middle));
    }

    private static final class UseParams {
        float raiseProgress, raiseProgressStart, raiseProgressMiddle, raiseProgressEnd, swayProgress, lowerProgress, raiseBackProgress, swayIntensity, swayScaleSlow, swayScaleFast;

        static UseParams of(SpearItem.ChargeStats s, float time) {
            UseParams u = new UseParams();
            int finishRaising = s.delay();
            int finishSwaying = s.engaged() + finishRaising;
            int startSwaying = finishSwaying - 20;
            int finishLowering = s.engaged() + s.tired() + finishRaising;
            int startLowering = finishLowering - 40;
            int finishRaisingBack = s.engaged() + s.tired() + s.disengaged() + finishRaising;
            u.raiseProgress = progress(time, 0.0F, finishRaising);
            u.raiseProgressStart = progress(u.raiseProgress, 0.0F, 0.5F);
            u.raiseProgressMiddle = progress(u.raiseProgress, 0.5F, 0.8F);
            u.raiseProgressEnd = progress(u.raiseProgress, 0.8F, 1.0F);
            u.swayProgress = progress(time, startSwaying, finishSwaying);
            u.lowerProgress = Ease.outCubic(Ease.inOutElastic(progress(time - 20.0F, startLowering, finishLowering)));
            u.raiseBackProgress = progress(time, finishRaisingBack - 5, finishRaisingBack);
            u.swayIntensity = 2.0F * Ease.outCirc(u.swayProgress) - 2.0F * Ease.inCirc(u.raiseBackProgress);
            u.swayScaleSlow = Mth.sin(time * 19.0F * ((float) Math.PI / 180.0F)) * u.swayIntensity;
            u.swayScaleFast = Mth.sin(time * 30.0F * ((float) Math.PI / 180.0F)) * u.swayIntensity;
            return u;
        }
    }
}
