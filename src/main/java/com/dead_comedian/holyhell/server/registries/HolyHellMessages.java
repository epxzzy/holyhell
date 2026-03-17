package com.dead_comedian.holyhell.server.registries;

import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.networking.ServerboundAngelShaderAbilityPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = HolyHell.MOD_ID)
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


    }


}