package net.yufinn.mountsofmayhem.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.ItemStack;
import net.yufinn.mountsofmayhem.item.SpearItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Inject(method = "canReplaceCurrentItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void mountsofmayhem$noSpearPickup(ItemStack candidate, ItemStack current, CallbackInfoReturnable<Boolean> cir) {
        Object self = this;
        if ((self instanceof Drowned || self instanceof AbstractSkeleton) && candidate.getItem() instanceof SpearItem) cir.setReturnValue(false);
    }
}
