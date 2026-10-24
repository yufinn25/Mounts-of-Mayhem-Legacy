package net.yufinn.mountsofmayhem.combat;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> SPEAR = ResourceKey.create(Registries.DAMAGE_TYPE, MountsOfMayhem.id("spear"));

    public static DamageSource spear(Entity attacker) {
        return new DamageSource(attacker.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SPEAR), attacker);
    }
}
