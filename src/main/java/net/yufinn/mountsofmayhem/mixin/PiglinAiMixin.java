package net.yufinn.mountsofmayhem.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.yufinn.mountsofmayhem.entity.PiglinSpearBehaviors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {
    @Inject(method = "initFightActivity(Lnet/minecraft/world/entity/monster/piglin/Piglin;Lnet/minecraft/world/entity/ai/Brain;)V", at = @At("TAIL"))
    private static void mountsofmayhem$addSpearBehaviors(Piglin piglin, Brain<Piglin> brain, CallbackInfo ci) {
        brain.addActivity(Activity.FIGHT, ImmutableList.<Pair<Integer, ? extends BehaviorControl<? super Piglin>>>of(
                Pair.of(12, new PiglinSpearBehaviors.Approach(1.0D, 10.0F)),
                Pair.of(12, new PiglinSpearBehaviors.Attack(1.0D, 1.0D, 2.0F)),
                Pair.of(12, new PiglinSpearBehaviors.Retreat(1.0D))));
    }
}
