package com.dead_comedian.holyhell.server.event;

import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.server.block.entity.CoffinBlockEntity;
import com.dead_comedian.holyhell.server.data.PlayerCoffinStatus;
import com.dead_comedian.holyhell.server.data.StoredInventory;
import com.dead_comedian.holyhell.server.registries.HolyHellAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = HolyHell.MOD_ID)
public class CoffinEvents {
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ServerLevel level = player.serverLevel();

        PlayerCoffinStatus status = player.getData(HolyHellAttachments.COFFIN_STATUS);

        BlockEntity blockEntity = level.getBlockEntity(status.coffinPos);

        if (!(blockEntity instanceof CoffinBlockEntity entity)) return;
        if (!status.active) return;

        entity.setStoredPlayer(player.getUUID());

        StoredInventory data = player.getData(HolyHellAttachments.STORED_INVENTORY);

        for (int i = 0; i < 36; i++) {
            data.items[i] = player.getInventory().items.get(i).copy();
        }

        for (int i = 0; i < 4; i++) {
            data.armor[i] = player.getInventory().armor.get(i).copyAndClear();
        }

        data.offhand[0] = player.getInventory().offhand.get(0).copyAndClear();

        player.getInventory().clearContent();

        status.update(false, status.coffinPos);
        entity.postDeathHook();
    }


    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
        if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;

        // Only copy if death caused the clone
        if (!event.isWasDeath()) return;

        StoredInventory oldData =
                oldPlayer.getData(HolyHellAttachments.STORED_INVENTORY);

        StoredInventory newData =
                newPlayer.getData(HolyHellAttachments.STORED_INVENTORY);

        newData.items = oldData.items.clone();
        newData.armor = oldData.armor.clone();
        newData.offhand = oldData.offhand.clone();
    }
}
