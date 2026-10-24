package net.yufinn.mountsofmayhem.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import javax.annotation.Nullable;

public class ZombieNautilusEntity extends NautilusEntity {
    private static final EntityDataAccessor<Boolean> CORAL = SynchedEntityData.defineId(ZombieNautilusEntity.class, EntityDataSerializers.BOOLEAN);

    public ZombieNautilusEntity(EntityType<? extends ZombieNautilusEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return NautilusEntity.createAttributes().add(Attributes.MOVEMENT_SPEED, 1.1D);
    }

    @Override
    protected boolean isZombie() {
        return true;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return effect.getEffect() != MobEffects.REGENERATION && super.canBeAffected(effect);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return null;
    }

    @Override
    public boolean canBeLeashed(net.minecraft.world.entity.player.Player player) {

        return !(getFirstPassenger() instanceof Mob) && !isAggravated() && super.canBeLeashed(player);
    }

    @Override
    public void aiStep() {
        if (this.isAlive() && !this.level().isClientSide && this.isSunBurnTick() && this.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) this.setSecondsOnFire(8);
        if (!this.level().isClientSide && this.isInWater() && !this.isTame() && this.getFirstPassenger() instanceof Mob rider && rider.isAlive()) this.steerForRider(rider);
        super.aiStep();
    }

    private void steerForRider(Mob rider) {
        LivingEntity target = rider.getTarget();
        if (target != null && target.isAlive()) {
            if (this.tickCount % 10 == 0 && this.distanceToSqr(target) > 64.0D) this.getNavigation().moveTo(target, 1.0D);
            return;
        }
        if (this.getNavigation().isDone() && this.random.nextInt(40) == 0) {
            for (int i = 0; i < 8; i++) {
                BlockPos pos = this.blockPosition().offset(this.random.nextInt(21) - 10, this.random.nextInt(9) - 4, this.random.nextInt(21) - 10);
                if (this.level().getFluidState(pos).is(FluidTags.WATER) && this.level().getFluidState(pos.above()).is(FluidTags.WATER)) {
                    this.getNavigation().moveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1.0D);
                    return;
                }
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CORAL, false);
    }

    public boolean isCoral() {
        return this.entityData.get(CORAL);
    }

    public void setCoral(boolean coral) {
        this.entityData.set(CORAL, coral);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Variant", isCoral() ? "coral" : "temperate");
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setCoral("coral".equals(tag.getString("Variant")));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        setCoral(level.getBiome(blockPosition()).is(Biomes.WARM_OCEAN));
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }
}
