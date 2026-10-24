package net.yufinn.mountsofmayhem.entity;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MountsOfMayhem.MOD_ID);
    public static final RegistryObject<MobEffect> BREATH_OF_THE_NAUTILUS = EFFECTS.register("breath_of_the_nautilus", () -> new BreathEffect(MobEffectCategory.BENEFICIAL, 0x4FA8D6));

    public static class BreathEffect extends MobEffect {
        public BreathEffect(MobEffectCategory category, int color) {
            super(category, color);
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return true;
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.isEyeInFluid(FluidTags.WATER) && entity.getAirSupply() < entity.getMaxAirSupply()) entity.setAirSupply(entity.getAirSupply() + 1);
        }
    }
}
