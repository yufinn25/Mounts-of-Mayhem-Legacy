package net.yufinn.mountsofmayhem.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.item.SpearItem;

@Mod.EventBusSubscriber(modid = MountsOfMayhem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (!event.getEntity().isUsingItem() || event.getEntity().isPassenger() || !(event.getEntity().getUseItem().getItem() instanceof SpearItem)) return;
        Input input = event.getInput();
        input.leftImpulse *= 5.0F;
        input.forwardImpulse *= 5.0F;
    }

    @SubscribeEvent
    public static void onRenderLivingPost(net.minecraftforge.client.event.RenderLivingEvent.Post<?, ?> event) {
        SpearAnimations.poseEntity = null;
    }

    @SubscribeEvent
    public static void onLivingTick(net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide) SpearAnimations.trackSwing(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !(event.player instanceof LocalPlayer player) || !player.isUsingItem() || player.isPassenger() || !(player.getUseItem().getItem() instanceof SpearItem)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.keySprint.isDown() && mc.options.keyUp.isDown() && !player.horizontalCollision && !player.isInWater() && player.getFoodData().getFoodLevel() > 6) player.setSprinting(true);
    }
}
