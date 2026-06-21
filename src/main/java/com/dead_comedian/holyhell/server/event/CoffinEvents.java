package com.dead_comedian.holyhell.server.event;

import com.dead_comedian.holyhell.CommonConfig;
import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.server.data.StoredInventory;
import com.dead_comedian.holyhell.server.registries.HolyHellAttachments;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = HolyHell.MOD_ID)
public class CoffinEvents {


    @SubscribeEvent
    public static void deathEvent(LivingDeathEvent deathEvent) {
        if (CommonConfig.ENABLE_COFFINS.get()) {
            if (deathEvent.getEntity() instanceof Player player && player.getData(HolyHellAttachments.HAS_COFFIN)) {
                player.setData(HolyHellAttachments.DIED, true);
                player.setData(HolyHellAttachments.SAVED_INVENTORY, StoredInventory.saveInventory(player.getInventory()));
                player.getInventory().clearContent();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (CommonConfig.ENABLE_COFFINS.get()) {
            Player playerOld = event.getOriginal();
            Player playerNew = event.getEntity();

            if (playerOld.getData(HolyHellAttachments.HAS_COFFIN.get()) && event.isWasDeath()) {

                playerNew.setData(HolyHellAttachments.SAVED_INVENTORY, playerOld.getData(HolyHellAttachments.SAVED_INVENTORY));
                playerNew.setData(HolyHellAttachments.DIED, playerOld.getData(HolyHellAttachments.DIED));
            }
        }
    }
}

