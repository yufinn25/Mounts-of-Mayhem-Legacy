package net.yufinn.mountsofmayhem.network;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public final class ModNetwork {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(MountsOfMayhem.id("main"), () -> VERSION, VERSION::equals, VERSION::equals);

    private ModNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(0, SpearHitFeedbackPacket.class, SpearHitFeedbackPacket::encode, SpearHitFeedbackPacket::decode, SpearHitFeedbackPacket::handle);
    }

    public static void sendKineticHit(LivingEntity user) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> user), new SpearHitFeedbackPacket(user.getId()));
    }
}
