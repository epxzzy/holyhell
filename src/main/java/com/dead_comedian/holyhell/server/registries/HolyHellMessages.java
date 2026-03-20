package com.dead_comedian.holyhell.server.registries;

import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.networking.ServerboundAngelShaderAbilityPacket;
import com.dead_comedian.holyhell.networking.ServerboundTpToAngelPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class HolyHellMessages {
    private static final String PROTOCOL_VER = "1";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VER);

        registrar.playToServer(
                ServerboundAngelShaderAbilityPacket.TYPE,
                ServerboundAngelShaderAbilityPacket.STREAM_CODEC,
                ServerboundAngelShaderAbilityPacket::handle
        );
        registrar.playToServer(
                ServerboundTpToAngelPacket.TYPE,
                ServerboundTpToAngelPacket.STREAM_CODEC,
                ServerboundTpToAngelPacket::handle
        );

    }


}