package net.yufinn.mountsofmayhem.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.phys.Vec3;
import net.yufinn.mountsofmayhem.item.SpearItem;

import java.util.EnumSet;

public class SpearChargeGoal extends Goal {
    private static final double MAX_FLEEING_TIME = reducedTickDelay(100);
    private final PathfinderMob mob;
    private State state;
    private final double speedModifierWhenCharging;
    private final double speedModifierWhenRepositioning;
    private final float approachDistanceSq;
    private final float targetInRangeRadiusSq;

    public SpearChargeGoal(PathfinderMob mob, double speedModifierWhenCharging, double speedModifierWhenRepositioning, float approachDistance, float targetInRangeRadius) {
        this.mob = mob;
        this.speedModifierWhenCharging = speedModifierWhenCharging;
        this.speedModifierWhenRepositioning = speedModifierWhenRepositioning;
        this.approachDistanceSq = approachDistance * approachDistance;
        this.targetInRangeRadiusSq = targetInRangeRadius * targetInRangeRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.ableToAttack() && !this.mob.isUsingItem();
    }

    private boolean ableToAttack() {
        return this.mob.getTarget() != null && this.mob.getMainHandItem().getItem() instanceof SpearItem;
    }

    private int getKineticWeaponUseDuration() {
        if (!(this.mob.getMainHandItem().getItem() instanceof SpearItem spear)) return reducedTickDelay(0);
        SpearItem.ChargeStats stats = spear.getChargeStats();
        return reducedTickDelay(stats.delay() + stats.engaged() + stats.tired() + stats.disengaged());
    }

    @Override
    public boolean canContinueToUse() {
        return this.state != null && !this.state.done && this.ableToAttack();
    }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
        this.state = new State();
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.getNavigation().stop();
        this.mob.setAggressive(false);
        this.state = null;
        this.mob.stopUsingItem();
    }

    static float mountSpeedModifier(PathfinderMob mob) {
        Entity mount = mob.getRootVehicle();
        if (mount instanceof CamelHuskEntity) return 4.0F;
        if (mount instanceof ZombieHorse) return 1.4F;
        return 1.0F;
    }

    @Override
    public void tick() {
        if (this.state == null) return;
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;
        double targetDistSqr = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
        float speedModifier = mountSpeedModifier(this.mob);
        int mountDistance = this.mob.isPassenger() ? 2 : 0;
        this.mob.lookAt(target, 30.0F, 30.0F);
        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (this.state.notEngagedYet()) {
            if (targetDistSqr > this.approachDistanceSq) {
                this.mob.getNavigation().moveTo(target, speedModifier * this.speedModifierWhenRepositioning);
                return;
            }
            this.state.startEngagement(this.getKineticWeaponUseDuration());
            this.mob.startUsingItem(InteractionHand.MAIN_HAND);
        }
        if (this.state.tickAndCheckEngagement()) {
            this.mob.stopUsingItem();
            double distance = Math.sqrt(targetDistSqr);
            this.state.awayPos = posAway(Math.max(1.0D, 11 + mountDistance - distance), target.position());
            this.state.fleeingTime = 1;
        }
        if (!this.state.tickAndCheckFleeing()) {
            if (this.state.awayPos != null) {
                this.mob.getNavigation().moveTo(this.state.awayPos.x, this.state.awayPos.y, this.state.awayPos.z, speedModifier * this.speedModifierWhenRepositioning);
                if (this.mob.getNavigation().isDone()) {
                    if (this.state.fleeingTime > 0) {
                        this.state.done = true;
                        return;
                    }
                    this.state.awayPos = null;
                }
            } else {
                this.mob.getNavigation().moveTo(target, speedModifier * this.speedModifierWhenCharging);
                if (targetDistSqr < this.targetInRangeRadiusSq || this.mob.getNavigation().isDone()) {
                    double distance = Math.sqrt(targetDistSqr);
                    this.state.awayPos = posAway(Math.max(1.0D, 7 + mountDistance - distance), target.position());
                }
            }
        }
    }

    private Vec3 posAway(double maxHorizontal, Vec3 avoid) {
        return LandRandomPos.getPosAway(this.mob, (int) Math.ceil(maxHorizontal), 7, avoid);
    }

    private static final class State {
        private int engageTime = -1;
        private int fleeingTime = -1;
        private Vec3 awayPos;
        private boolean done = false;

        boolean notEngagedYet() {
            return this.engageTime < 0;
        }

        void startEngagement(int spearDownTime) {
            this.engageTime = spearDownTime;
        }

        boolean tickAndCheckEngagement() {
            if (this.engageTime > 0) {
                --this.engageTime;
                return this.engageTime == 0;
            }
            return false;
        }

        boolean tickAndCheckFleeing() {
            if (this.fleeingTime > 0) {
                ++this.fleeingTime;
                if (this.fleeingTime > MAX_FLEEING_TIME) {
                    this.done = true;
                    return true;
                }
            }
            return false;
        }
    }
}
