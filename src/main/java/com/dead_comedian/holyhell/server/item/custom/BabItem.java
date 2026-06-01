package com.dead_comedian.holyhell.server.item.custom;

import com.dead_comedian.holyhell.server.entity.BabOneEntity;
import com.dead_comedian.holyhell.server.entity.BabThreeEntity;
import com.dead_comedian.holyhell.server.entity.BabTwoEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellEntities;
import com.dead_comedian.holyhell.server.registries.HolyhellDataComps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class BabItem extends Item {
    public BabItem(Properties properties) {
        super(properties);
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        CompoundTag tag = stack.get(HolyhellDataComps.BAB_DATA);

        if (tag != null) {


            tooltipComponents.add(Component.literal("level: " + tag.getInt("level")));
            tooltipComponents.add(Component.literal("tamed: " + (tag.getCompound("entity_data").get("Owner") != null)));

        }
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        TamableAnimal babEntity = new BabOneEntity(HolyHellEntities.BAB_ONE.get(), level);


        if (stack.get(HolyhellDataComps.BAB_DATA) != null) {
            if (stack.get(HolyhellDataComps.BAB_DATA).copy().getInt("level") == 2) {
                babEntity = new BabTwoEntity(HolyHellEntities.BAB_TWO.get(), level);
            } else if (stack.get(HolyhellDataComps.BAB_DATA).copy().getInt("level") == 3) {
                babEntity = new BabThreeEntity(HolyHellEntities.BAB_THREE.get(), level);
            }

            babEntity.load(Objects.requireNonNull(stack.get(HolyhellDataComps.BAB_DATA)).getCompound("entity_data"));
        }

        level.addFreshEntity(babEntity);

        babEntity.moveTo(player.blockPosition().above(), babEntity.getYRot(), babEntity.getXRot());
        babEntity.addDeltaMovement(player.getLookAngle().multiply(2, 2, 2));
        stack.consume(1, player);

        return InteractionResultHolder.pass(stack);
    }

}
