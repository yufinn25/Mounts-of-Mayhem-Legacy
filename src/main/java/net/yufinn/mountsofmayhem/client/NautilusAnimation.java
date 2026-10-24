package net.yufinn.mountsofmayhem.client;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class NautilusAnimation {
    public static final AnimationDefinition BREATHING = AnimationDefinition.Builder.withLength(1.0F).looping()
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    scale(0.0F, 1.0D, 1.0D, 1.0D), scale(0.5F, 1.0D, 1.0D, 1.2D), scale(0.75F, 1.0D, 1.0D, 0.9D), scale(0.875F, 1.0D, 1.0D, 1.0D), scale(1.0F, 1.0D, 1.0D, 1.0D)))
            .addAnimation("upper_mouth", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    rot(0.0F, 0.0F), rot(0.5F, 30.0F), rot(0.75F, 0.0F), rot(0.875F, 0.0F), rot(1.0F, 0.0F)))
            .addAnimation("upper_mouth", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    scale(0.0F, 1.0D, 1.0D, 1.0D), scale(0.5F, 1.0D, 1.0D, 1.4D), scale(0.75F, 1.0D, 1.0D, 0.9D), scale(0.875F, 1.0D, 1.0D, 1.0D), scale(1.0F, 1.0D, 1.0D, 1.0D)))
            .addAnimation("inner_mouth", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    scale(0.0F, 1.0D, 1.0D, 1.0D), scale(0.5F, 0.8D, 0.8D, 1.0D), scale(0.75F, 1.0D, 1.0D, 0.9D), scale(0.875F, 1.0D, 1.0D, 1.0D), scale(1.0F, 1.0D, 1.0D, 1.0D)))
            .addAnimation("lower_mouth", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    rot(0.0F, 0.0F), rot(0.5F, -30.0F), rot(0.75F, 0.0F), rot(0.875F, 0.0F), rot(1.0F, 0.0F)))
            .addAnimation("lower_mouth", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    scale(0.0F, 1.0D, 1.0D, 1.0D), scale(0.5F, 1.0D, 1.0D, 1.4D), scale(0.75F, 1.0D, 1.0D, 0.9D), scale(0.875F, 1.0D, 1.0D, 1.0D), scale(1.0F, 1.0D, 1.0D, 1.0D)))
            .build();

    private static Keyframe scale(float time, double x, double y, double z) {
        return new Keyframe(time, KeyframeAnimations.scaleVec(x, y, z), AnimationChannel.Interpolations.LINEAR);
    }

    private static Keyframe rot(float time, float x) {
        return new Keyframe(time, KeyframeAnimations.degreeVec(x, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR);
    }
}
