package com.dead_comedian.holyhell.networking;

import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.server.registries.HolyHellAttachments;
import com.dead_comedian.holyhell.server.registries.HolyHellDimensions;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ServerboundTpToAngelPacket() implements CustomPacketPayload {

    public static final Type<ServerboundTpToAngelPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(HolyHell.MOD_ID, "tp_to_angel"));

    public static final StreamCodec<ByteBuf, ServerboundTpToAngelPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundTpToAngelPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundTpToAngelPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) context.player();
            player.setData(HolyHellAttachments.TP_TO_ANGEL, player.level().dimension() != HolyHellDimensions.ANGEL);
        });
    }
}