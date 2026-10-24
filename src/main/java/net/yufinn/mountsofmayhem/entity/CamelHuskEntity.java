package net.yufinn.mountsofmayhem.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

public class CamelHuskEntity extends Camel {
    public CamelHuskEntity(EntityType<? extends Camel> type, Level level) {
        super(type, level);
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
    public boolean removeWhenFarAway(double distance) {
        return true;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.RABBIT_FOOT);
    }

    @Override
    protected boolean handleEating(Player player, ItemStack stack) {

        if (!isFood(stack)) return false;
        boolean couldHeal = getHealth() < getMaxHealth();
        if (couldHeal) heal(2.0F);
        if (!couldHeal) return false;
        if (!isSilent()) {
            level().playSound(null, getX(), getY(), getZ(), ModSounds.CAMEL_HUSK_EAT.get(), getSoundSource(), 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
        }
        gameEvent(net.minecraft.world.level.gameevent.GameEvent.EAT);
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide) setPersistenceRequired();
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return !(getFirstPassenger() instanceof Mob) && super.canBeLeashed(player);
    }

    @Override
    public void aiStep() {
        if (!level().isClientSide && getFirstPassenger() instanceof Mob) getBrain().eraseMemory(MemoryModuleType.IS_PANICKING);
        super.aiStep();
    }

    @Override
    public boolean canMate(Animal other) {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Nullable
    @Override
    public Camel getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.CAMEL_HUSK_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CAMEL_HUSK_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.CAMEL_HUSK_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        boolean sandy = state.is(BlockTags.SAND) || state.getBlock() instanceof net.minecraft.world.level.block.ConcretePowderBlock;
        playSound(sandy ? ModSounds.CAMEL_HUSK_STEP_SAND.get() : ModSounds.CAMEL_HUSK_STEP.get(), 0.4F, 1.0F);
    }
}
