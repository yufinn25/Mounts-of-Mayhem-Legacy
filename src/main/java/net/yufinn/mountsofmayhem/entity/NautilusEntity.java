package net.yufinn.mountsofmayhem.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.tags.FluidTags;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.minecraft.tags.TagKey;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.item.ShearsItem;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.Comparator;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.EquipmentSlot;
import net.yufinn.mountsofmayhem.item.NautilusArmorItem;

public class NautilusEntity extends TamableAnimal implements PlayerRideableJumping, net.minecraft.world.entity.HasCustomInventoryScreen {
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(NautilusEntity.class, EntityDataSerializers.BOOLEAN);
    private int dashCooldown;
    private boolean dashing;
    private int chargeCooldown;
    private int attackCooldown = 2400;
    private UUID angryAt;
    private long angryUntil;

    public static final TagKey<Item> FOOD = ItemTags.create(MountsOfMayhem.id("nautilus_food"));
    public static final TagKey<Item> TAMING = ItemTags.create(MountsOfMayhem.id("nautilus_taming_items"));

    public NautilusEntity(EntityType<? extends NautilusEntity> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.011F, 0.0F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    public static boolean checkSpawn(EntityType<? extends NautilusEntity> type, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        int sea = level.getSeaLevel();
        return pos.getY() >= sea - 25 && pos.getY() <= sea - 5 && level.getFluidState(pos.below()).is(FluidTags.WATER) && level.getBlockState(pos.above()).is(net.minecraft.world.level.block.Blocks.WATER);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return effect.getEffect() != net.minecraft.world.effect.MobEffects.POISON && super.canBeAffected(effect);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new NautilusChargeGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.4D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.3D, Ingredient.of(FOOD), false));
        this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return !isAggravated() && super.canBeLeashed(player);
    }

    @Override
    public boolean isInSittingPose() {
        return false;
    }

    @Override
    public void setOrderedToSit(boolean sit) {
    }

    @Override
    public boolean isFood(ItemStack stack) {

        return !isTame() && !isBaby() ? stack.is(TAMING) : stack.is(FOOD);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return !isPersistenceRequired() && !isTame();
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, net.minecraft.world.level.LevelReader level) {
        return 0.0F;
    }

    @Override
    public boolean checkSpawnObstruction(net.minecraft.world.level.LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public void baseTick() {
        int air = this.getAirSupply();
        super.baseTick();
        if (this.isAlive() && !this.isInWaterOrBubble() && !isZombie()) {
            this.setAirSupply(air - 1);
            if (this.getAirSupply() <= -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(this.getMaxAirSupply());
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SADDLED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Saddled", isSaddled());
        tag.putInt("AttackCooldown", attackCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setSaddled(tag.getBoolean("Saddled"));
        if (tag.contains("AttackCooldown")) attackCooldown = tag.getInt("AttackCooldown");
    }

    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, saddled);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (isSaddled() && getFirstPassenger() instanceof Player player) return player;
        return !isTame() && getFirstPassenger() instanceof net.minecraft.world.entity.Mob mob ? mob : null;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 1.0D;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction move) {
        super.positionRider(passenger, move);
        if (passenger instanceof Drowned drowned && !level().isClientSide) {
            if (drowned.isSwimming()) drowned.setSwimming(false);
            if (drowned.getPose() == Pose.SWIMMING) drowned.setPose(Pose.STANDING);
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!level().isClientSide && passenger instanceof LivingEntity living) living.removeEffect(ModEffects.BREATH_OF_THE_NAUTILUS.get());
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (isSaddled()) spawnAtLocation(Items.SADDLE);
    }

    public boolean isDashing() {
        return this.dashing;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getFirstPassenger() instanceof Player player && (!player.hasEffect(ModEffects.BREATH_OF_THE_NAUTILUS.get()) || level().getGameTime() % 40L == 0L)) player.addEffect(new MobEffectInstance(ModEffects.BREATH_OF_THE_NAUTILUS.get(), 60, 0, true, false, true));
        if (!level().isClientSide) tickAttackAi();
        if (this.dashCooldown > 0) {
            this.dashCooldown--;
            if (this.dashCooldown == 35) this.dashing = false;
            if (this.dashCooldown == 0 && !level().isClientSide) playSound(isInWaterOrBubble() ? snd(ModSounds.NAUTILUS_DASH_READY, ModSounds.ZOMBIE_DASH_READY) : snd(ModSounds.NAUTILUS_DASH_READY_LAND, ModSounds.ZOMBIE_DASH_READY_LAND), 1.0F, 1.0F);
        }
        if (!level().isClientSide && getControllingPassenger() != null && isInWater() && this.tickCount % 20 == 0 && getDeltaMovement().lengthSqr() > 0.002D) playSound(ModSounds.NAUTILUS_RIDING.get(), 1.0F, 1.0F);
    }

    public int getChargeCooldown() {
        return chargeCooldown;
    }

    public void setChargeCooldown(int ticks) {
        this.chargeCooldown = ticks;
    }

    protected boolean isZombie() {
        return false;
    }

    private SoundEvent snd(net.minecraftforge.registries.RegistryObject<SoundEvent> normal, net.minecraftforge.registries.RegistryObject<SoundEvent> zombie) {
        return (isZombie() ? zombie : normal).get();
    }

    public void playChargeSound() {
        playSound(snd(ModSounds.NAUTILUS_DASH, ModSounds.ZOMBIE_DASH), 1.0F, 1.0F);
    }

    @javax.annotation.Nullable
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty, net.minecraft.world.entity.MobSpawnType reason, @javax.annotation.Nullable net.minecraft.world.entity.SpawnGroupData data, @javax.annotation.Nullable net.minecraft.nbt.CompoundTag tag) {
        this.attackCooldown = 2400 + level.getRandom().nextInt(1201);
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    protected boolean isAggravated() {
        return getTarget() != null || (angryAt != null && level().getGameTime() < angryUntil);
    }

    private boolean canAngerAt(LivingEntity entity) {
        return entity != this && entity.isAlive() && entity.level() == level() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && canAttack(entity) && !(entity instanceof TamableAnimal tamable && tamable.isTame());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide && source.getEntity() instanceof LivingEntity attacker && canAngerAt(attacker)) {
            angryAt = attacker.getUUID();
            angryUntil = level().getGameTime() + 400L;
        }
        return hurt;
    }

    private void tickAttackAi() {
        if (chargeCooldown > 0) chargeCooldown--;
        if (attackCooldown > 0) attackCooldown--;
        LivingEntity target = getTarget();
        if (target != null && (!target.isAlive() || !canAttack(target))) setTarget(null);
        if (target != null || tickCount % 10 != 0) return;

        if (isInLove() || !isInWater() || isBaby() || isTame()) return;
        LivingEntity angry = angryAt != null && level().getGameTime() < angryUntil && level() instanceof ServerLevel server && server.getEntity(angryAt) instanceof LivingEntity living && canAngerAt(living) ? living : null;
        if (angry != null) {
            setTarget(angry);
            return;
        }
        if (attackCooldown > 0) return;
        attackCooldown = 2400 + random.nextInt(1201);
        if (random.nextFloat() < 0.5F) return;
        level().getEntitiesOfClass(Pufferfish.class, getBoundingBox().inflate(16.0D), fish -> fish.isAlive() && hasLineOfSight(fish) && canAngerAt(fish)).stream().min(Comparator.comparingDouble(this::distanceToSqr)).ifPresent(this::setTarget);
    }

    public void startDash(int power) {
        LivingEntity rider = getControllingPassenger();
        if (this.dashCooldown > 0 || rider == null) return;
        float scale = power >= 90 ? 1.0F : 0.4F + 0.4F * power / 90.0F;
        applyDash(rider.getLookAngle(), scale);
    }

    public void dashToward(LivingEntity target) {
        if (this.dashCooldown > 0) return;
        applyDash(target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D).subtract(position()).normalize(), 1.0F);
    }

    private void applyDash(Vec3 direction, float scale) {
        setDeltaMovement(getDeltaMovement().add(direction.scale((isInWater() ? 1.2D : 0.5D) * scale * getAttributeValue(Attributes.MOVEMENT_SPEED) * getBlockSpeedFactor())));
        this.dashCooldown = 40;
        this.dashing = true;
        this.hasImpulse = true;
        if (!level().isClientSide) playSound(isInWaterOrBubble() ? snd(ModSounds.NAUTILUS_DASH, ModSounds.ZOMBIE_DASH) : snd(ModSounds.NAUTILUS_DASH_LAND, ModSounds.ZOMBIE_DASH_LAND), 1.0F, 1.0F);
    }

    @Override
    public void onPlayerJump(int power) {
        if (canJump() && power > 0) startDash(power);
    }

    @Override
    public boolean canJump() {
        return isSaddled() && getControllingPassenger() != null;
    }

    @Override
    public void handleStartJump(int power) {

        if (power <= 0 || this.dashCooldown > 0) return;
        this.dashCooldown = 40;
        this.dashing = true;
        playSound(isInWaterOrBubble() ? snd(ModSounds.NAUTILUS_DASH, ModSounds.ZOMBIE_DASH) : snd(ModSounds.NAUTILUS_DASH_LAND, ModSounds.ZOMBIE_DASH_LAND), 1.0F, 1.0F);
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public void travel(Vec3 input) {
        LivingEntity rider = getControllingPassenger();
        if (rider instanceof Player player && isVehicle()) {
            float yaw = getYRot() + Mth.wrapDegrees(player.getYRot() - getYRot()) * 0.5F;
            setRot(yaw, player.getXRot() * 0.5F);
            this.yRotO = yaw;
            this.yBodyRot = yaw;
            this.yHeadRot = yaw;
            float pitch = player.getXRot() * Mth.DEG_TO_RAD;
            double y = 0.0D;
            double z = 0.0D;
            if (player.zza != 0.0F) {
                z = Mth.cos(pitch);
                y = -Mth.sin(pitch);
                if (player.zza < 0.0F) {
                    z *= -0.5D;
                    y *= -0.5D;
                }
            }
            double speed = getAttributeValue(Attributes.MOVEMENT_SPEED);
            if (isInWater()) {
                setSpeed((float) (0.0325D * speed));
                moveRelative(getSpeed(), new Vec3(player.xxa, y, z));
                move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
                setDeltaMovement(getDeltaMovement().scale(0.9D));
            } else {
                setSpeed((float) (0.02D * speed));
                super.travel(new Vec3(player.xxa, 0.0D, z));
            }
            calculateEntityAnimation(false);
        } else if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), input);
            this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(input);
        }
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (level().isClientSide || !(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) return;
        net.minecraftforge.network.NetworkHooks.openScreen(serverPlayer, new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> new net.yufinn.mountsofmayhem.menu.NautilusInventoryMenu(id, inventory, this), getDisplayName()), buf -> buf.writeInt(getId()));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isTame() && !isBaby() && player.isSecondaryUseActive()) {
            openCustomInventoryScreen(player);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (isAlive() && isTame() && !isBaby()) {
            if (stack.getItem() instanceof NautilusArmorItem && getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
                if (!level().isClientSide) {
                    ItemStack worn = stack.copy();
                    worn.setCount(1);
                    setItemSlot(EquipmentSlot.CHEST, worn);
                    setDropChance(EquipmentSlot.CHEST, 2.0F);
                    setPersistenceRequired();
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    playSound(ModSounds.ARMOR_EQUIP_NAUTILUS.get(), 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            if (stack.getItem() instanceof ShearsItem && !getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
                if (!level().isClientSide) {
                    spawnAtLocation(getItemBySlot(EquipmentSlot.CHEST));
                    setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                    playSound(ModSounds.ARMOR_UNEQUIP_NAUTILUS.get(), 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }
        if (isTame() && !isBaby()) {
            if (stack.is(Items.SADDLE) && !isSaddled()) {
                if (!level().isClientSide) {
                    setSaddled(true);
                    setPersistenceRequired();
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    playSound(isInWaterOrBubble() ? ModSounds.SADDLE_UNDERWATER_EQUIP.get() : ModSounds.SADDLE_EQUIP.get(), 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            if (stack.getItem() instanceof ShearsItem && isSaddled()) {
                if (!level().isClientSide) {
                    setSaddled(false);
                    spawnAtLocation(Items.SADDLE);
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                    playSound(ModSounds.SADDLE_UNEQUIP.get(), 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            if (!isFood(stack) && !player.isSecondaryUseActive() && !isVehicle()) {
                if (!level().isClientSide) {
                    setPersistenceRequired();
                    player.startRiding(this);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }
        if (isBaby()) return super.mobInteract(player, hand);
        if (!stack.isEmpty()) {
            if (!level().isClientSide && !isTame() && isFood(stack)) {
                usePlayerItem(player, hand, stack);
                tryToTame(player);
                return InteractionResult.SUCCESS;
            }
            if (isFood(stack) && getHealth() < getMaxHealth()) {

                FoodProperties food = stack.getItem().getFoodProperties(stack, this);
                usePlayerItem(player, hand, stack);
                heal(food != null ? 2.0F * (float) food.getNutrition() : 1.0F);
                playSound(getEatSound(), 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            InteractionResult result = stack.interactLivingEntity(player, this, hand);
            if (result.consumesAction()) return result;
        }
        return super.mobInteract(player, hand);
    }

    private void tryToTame(Player player) {
        if (this.random.nextInt(3) == 0) {
            this.tame(player);
            this.navigation.stop();
            this.level().broadcastEntityEvent(this, (byte) 7);
        } else {
            this.level().broadcastEntityEvent(this, (byte) 6);
        }
        this.playSound(getEatSound(), 1.0F, 1.0F);
    }

    @Override
    protected void usePlayerItem(Player player, InteractionHand hand, ItemStack stack) {
        if (stack.is(Items.COD_BUCKET) || stack.is(Items.SALMON_BUCKET) || stack.is(Items.PUFFERFISH_BUCKET) || stack.is(Items.TROPICAL_FISH_BUCKET)) {
            player.setItemInHand(hand, net.minecraft.world.item.ItemUtils.createFilledResult(stack, player, new ItemStack(Items.WATER_BUCKET)));
        } else {
            super.usePlayerItem(player, hand, stack);
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return ModEntities.NAUTILUS.get().create(level);
    }

    @Override
    public float getScale() {
        return isBaby() ? 0.5F : 1.0F;
    }

    @Override
    protected float getStandingEyeHeight(net.minecraft.world.entity.Pose pose, net.minecraft.world.entity.EntityDimensions dim) {
        return dim.height * 0.5F;
    }

    protected SoundEvent getEatSound() {
        return snd(ModSounds.NAUTILUS_EAT, ModSounds.ZOMBIE_EAT);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isInWaterOrBubble() ? snd(ModSounds.NAUTILUS_AMBIENT, ModSounds.ZOMBIE_AMBIENT) : snd(ModSounds.NAUTILUS_AMBIENT_LAND, ModSounds.ZOMBIE_AMBIENT_LAND);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return isInWaterOrBubble() ? snd(ModSounds.NAUTILUS_HURT, ModSounds.ZOMBIE_HURT) : snd(ModSounds.NAUTILUS_HURT_LAND, ModSounds.ZOMBIE_HURT_LAND);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return isInWaterOrBubble() ? snd(ModSounds.NAUTILUS_DEATH, ModSounds.ZOMBIE_DEATH) : snd(ModSounds.NAUTILUS_DEATH_LAND, ModSounds.ZOMBIE_DEATH_LAND);
    }

    @Override
    protected SoundEvent getSwimSound() {
        return snd(ModSounds.NAUTILUS_SWIM, ModSounds.ZOMBIE_SWIM);
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.PLAYER_SPLASH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }
}
