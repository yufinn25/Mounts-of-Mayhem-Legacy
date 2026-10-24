package net.yufinn.mountsofmayhem.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.combat.ModDamageTypes;

@Mod.EventBusSubscriber(modid = MountsOfMayhem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpearRetaliation {
    private SpearRetaliation() {}

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide || !event.getSource().is(ModDamageTypes.SPEAR)) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker) || !(attacker instanceof Zombie || attacker instanceof Piglin)) return;
        if (!(event.getEntity() instanceof Mob victim) || victim instanceof Goat || victim == attacker) return;
        if (!(victim instanceof NeutralMob || victim instanceof Enemy)) return;
        victim.setTarget(attacker);
    }
}
