package net.yufinn.mountsofmayhem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.yufinn.mountsofmayhem.item.SpearItem;

public final class SpearClientExtensions implements IClientItemExtensions {
    public static final SpearClientExtensions INSTANCE = new SpearClientExtensions();

    private SpearClientExtensions() {}

    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
        if (!(stack.getItem() instanceof SpearItem spear)) return false;
        int invert = arm == HumanoidArm.RIGHT ? 1 : -1;
        boolean using = player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUseItem() == stack;
        if (using) {

            poseStack.translate(invert * 0.56F, -0.52F, -0.72F);
            float timeHeld = (float) (stack.getUseDuration() - (player.getUseItemRemainingTicks() - partialTick + 1.0F));
            SpearAnimations.firstPersonUse(SpearAnimations.ticksSinceHitFeedback(player, partialTick), poseStack, timeHeld, arm, spear);
            return true;
        }
        float jab = SpearAnimations.jabProgress(player, spear, partialTick);
        if (jab > 0.0F) {

            poseStack.translate(invert * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
            SpearAnimations.firstPersonAttack(jab, poseStack, invert);
            return true;
        }
        return false;
    }
}
