package net.yufinn.mountsofmayhem.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import net.yufinn.mountsofmayhem.item.SpearItem;

public final class PiglinSpearBehaviors {
    private PiglinSpearBehaviors() {}

    private enum SpearStatus { APPROACH, CHARGING, RETREAT }

    private static final class State {
        SpearStatus status;
        int engageTime;
        Vec3 chargePosition;
        int fleeingTime;
        Vec3 fleeingPosition;
    }

    private static final Map<LivingEntity, State> STATES = new WeakHashMap<>();

    private static State state(LivingEntity mob) {
        return STATES.computeIfAbsent(mob, m -> new State());
    }

    private static final Map<MemoryModuleType<?>, MemoryStatus> NEEDS_TARGET = ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT);

    private static LivingEntity target(PathfinderMob mob) {
        return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    private static boolean ableToAttack(PathfinderMob mob) {
        return target(mob) != null && mob.getMainHandItem().getItem() instanceof SpearItem;
    }

    private static Vec3 posAway(PathfinderMob mob, double maxHorizontal, Vec3 avoid) {
        return LandRandomPos.getPosAway(mob, (int) Math.ceil(Math.max(1.0D, maxHorizontal)), 7, avoid);
    }

    private static void lookAt(PathfinderMob mob, LivingEntity target) {
        mob.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
    }

    public static final class Approach extends Behavior<PathfinderMob> {
        private final double speedModifierWhenRepositioning;
        private final float approachDistanceSq;

        public Approach(double speedModifierWhenRepositioning, float approachDistance) {
            super(NEEDS_TARGET);
            this.speedModifierWhenRepositioning = speedModifierWhenRepositioning;
            this.approachDistanceSq = approachDistance * approachDistance;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, PathfinderMob body) {
            return state(body).status == null && ableToAttack(body) && !body.isUsingItem();
        }

        @Override
        protected void start(ServerLevel level, PathfinderMob body, long time) {
            body.setAggressive(true);
            state(body).status = SpearStatus.APPROACH;
            super.start(level, body, time);
        }

        @Override
        protected boolean canStillUse(ServerLevel level, PathfinderMob body, long time) {
            if (!ableToAttack(body)) return false;
            LivingEntity target = target(body);
            return body.distanceToSqr(target.getX(), target.getY(), target.getZ()) > this.approachDistanceSq;
        }

        @Override
        protected void tick(ServerLevel level, PathfinderMob mob, long time) {
            LivingEntity target = target(mob);
            lookAt(mob, target);
            mob.getNavigation().moveTo(target, SpearChargeGoal.mountSpeedModifier(mob) * this.speedModifierWhenRepositioning);
        }

        @Override
        protected void stop(ServerLevel level, PathfinderMob body, long time) {
            body.getNavigation().stop();
            state(body).status = SpearStatus.CHARGING;
        }

        @Override
        protected boolean timedOut(long time) {
            return false;
        }
    }

    public static final class Attack extends Behavior<PathfinderMob> {
        private final double speedModifierWhenCharging;
        private final double speedModifierWhenRepositioning;
        private final float targetInRangeRadiusSq;

        public Attack(double speedModifierWhenCharging, double speedModifierWhenRepositioning, float targetInRangeRadius) {
            super(NEEDS_TARGET);
            this.speedModifierWhenCharging = speedModifierWhenCharging;
            this.speedModifierWhenRepositioning = speedModifierWhenRepositioning;
            this.targetInRangeRadiusSq = targetInRangeRadius * targetInRangeRadius;
        }

        private int useDuration(PathfinderMob mob) {
            if (!(mob.getMainHandItem().getItem() instanceof SpearItem spear)) return 0;
            SpearItem.ChargeStats stats = spear.getChargeStats();
            return stats.delay() + stats.engaged() + stats.tired() + stats.disengaged();
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, PathfinderMob body) {
            return state(body).status == SpearStatus.CHARGING && ableToAttack(body) && !body.isUsingItem();
        }

        @Override
        protected void start(ServerLevel level, PathfinderMob body, long time) {
            body.setAggressive(true);
            State state = state(body);
            state.engageTime = useDuration(body);
            state.chargePosition = null;
            body.startUsingItem(InteractionHand.MAIN_HAND);
            super.start(level, body, time);
        }

        @Override
        protected boolean canStillUse(ServerLevel level, PathfinderMob body, long time) {
            return state(body).engageTime > 0 && ableToAttack(body);
        }

        @Override
        protected void tick(ServerLevel level, PathfinderMob mob, long time) {
            LivingEntity target = target(mob);
            State state = state(mob);
            double targetDistSqr = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            float speedModifier = SpearChargeGoal.mountSpeedModifier(mob);
            int mountDistance = mob.isPassenger() ? 2 : 0;
            lookAt(mob, target);
            state.engageTime--;
            Vec3 awayPos = state.chargePosition;
            if (awayPos != null) {
                mob.getNavigation().moveTo(awayPos.x, awayPos.y, awayPos.z, speedModifier * this.speedModifierWhenRepositioning);
                if (mob.getNavigation().isDone()) state.chargePosition = null;
            } else {
                mob.getNavigation().moveTo(target, speedModifier * this.speedModifierWhenCharging);
                if (targetDistSqr < this.targetInRangeRadiusSq || mob.getNavigation().isDone()) {
                    double distance = Math.sqrt(targetDistSqr);
                    state.chargePosition = posAway(mob, 7 + mountDistance - distance, target.position());
                }
            }
        }

        @Override
        protected void stop(ServerLevel level, PathfinderMob body, long time) {
            body.getNavigation().stop();
            body.stopUsingItem();
            State state = state(body);
            state.chargePosition = null;
            state.engageTime = 0;
            state.status = SpearStatus.RETREAT;
        }

        @Override
        protected boolean timedOut(long time) {
            return false;
        }
    }

    public static final class Retreat extends Behavior<PathfinderMob> {
        private final double speedModifierWhenRepositioning;

        public Retreat(double speedModifierWhenRepositioning) {
            super(NEEDS_TARGET, 100);
            this.speedModifierWhenRepositioning = speedModifierWhenRepositioning;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, PathfinderMob body) {
            if (!ableToAttack(body) || body.isUsingItem()) return false;
            State state = state(body);
            if (state.status != SpearStatus.RETREAT) return false;
            LivingEntity target = target(body);
            double distance = Math.sqrt(body.distanceToSqr(target.getX(), target.getY(), target.getZ()));
            int mountDistance = body.isPassenger() ? 2 : 0;
            Vec3 awayPos = posAway(body, 11 + mountDistance - distance, target.position());
            if (awayPos == null) return false;
            state.fleeingPosition = awayPos;
            return true;
        }

        @Override
        protected void start(ServerLevel level, PathfinderMob body, long time) {
            body.setAggressive(true);
            state(body).fleeingTime = 0;
            super.start(level, body, time);
        }

        @Override
        protected boolean canStillUse(ServerLevel level, PathfinderMob body, long time) {
            State state = state(body);
            return state.fleeingTime < 100 && state.fleeingPosition != null && !body.getNavigation().isDone() && ableToAttack(body);
        }

        @Override
        protected void tick(ServerLevel level, PathfinderMob mob, long time) {
            LivingEntity target = target(mob);
            State state = state(mob);
            lookAt(mob, target);
            state.fleeingTime++;
            if (state.fleeingPosition != null) {
                Vec3 pos = state.fleeingPosition;
                mob.getNavigation().moveTo(pos.x, pos.y, pos.z, SpearChargeGoal.mountSpeedModifier(mob) * this.speedModifierWhenRepositioning);
            }
        }

        @Override
        protected void stop(ServerLevel level, PathfinderMob body, long time) {
            body.getNavigation().stop();
            body.setAggressive(false);
            body.stopUsingItem();
            STATES.remove(body);
        }
    }
}
