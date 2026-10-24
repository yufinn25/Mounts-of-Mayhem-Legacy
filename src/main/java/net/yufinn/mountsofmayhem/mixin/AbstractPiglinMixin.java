package net.yufinn.mountsofmayhem.mixin;

import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.yufinn.mountsofmayhem.item.SpearItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractPiglin.class)
public abstract class AbstractPiglinMixin {
    @Inject(method = "isHoldingMeleeWeapon()Z", at = @At("HEAD"), cancellable = true)
    private void mountsofmayhem$spearIsNotAMeleeTool(CallbackInfoReturnable<Boolean> cir) {
        if (((AbstractPiglin) (Object) this).getMainHandItem().getItem() instanceof SpearItem) cir.setReturnValue(false);
    }
}
