package net.yufinn.mountsofmayhem.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.Difficulty;

public class ParchedEntity extends AbstractSkeleton {
    public ParchedEntity(EntityType<? extends AbstractSkeleton> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSkeleton.createAttributes().add(Attributes.MAX_HEALTH, 16.0D);
    }

    public static boolean checkSpawn(EntityType<ParchedEntity> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, reason, pos, random) && (reason == MobSpawnType.SPAWNER || level.canSeeSky(pos));
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return effect.getEffect() != MobEffects.WEAKNESS && super.canBeAffected(effect);
    }

    @Override
    protected AbstractArrow getArrow(ItemStack arrowStack, float velocity) {
        AbstractArrow arrow = super.getArrow(arrowStack, velocity);
        if (arrow instanceof Arrow tipped) tipped.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600));
        return arrow;
    }

    @Override
    public void reassessWeaponGoal() {
        super.reassessWeaponGoal();
        if (level() == null || level().isClientSide) return;
        int interval = level().getDifficulty() == Difficulty.HARD ? 50 : 70;
        goalSelector.getAvailableGoals().forEach(wrapped -> {
            if (wrapped.getGoal() instanceof RangedBowAttackGoal<?> bow) bow.setMinAttackInterval(interval);
        });
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.PARCHED_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.PARCHED_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.PARCHED_DEATH.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSounds.PARCHED_STEP.get();
    }
}
