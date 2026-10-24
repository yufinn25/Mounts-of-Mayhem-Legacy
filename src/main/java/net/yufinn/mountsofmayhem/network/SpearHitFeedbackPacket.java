package net.yufinn.mountsofmayhem.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record SpearHitFeedbackPacket(int entityId) {
    public static void encode(SpearHitFeedbackPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
    }

    public static SpearHitFeedbackPacket decode(FriendlyByteBuf buf) {
        return new SpearHitFeedbackPacket(buf.readVarInt());
    }

    public static void handle(SpearHitFeedbackPacket packet, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> net.yufinn.mountsofmayhem.client.SpearAnimations.onKineticHit(packet.entityId)));
        ctx.setPacketHandled(true);
    }
}
