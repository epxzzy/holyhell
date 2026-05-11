package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;

public class RevenantPrepareTarget extends Behavior<RevenantEntity> {
    public RevenantPrepareTarget() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected void tick(ServerLevel level, RevenantEntity owner, long gameTime) {
        super.tick(level, owner, gameTime);
        //comments by me cuz i cant read me own code

        if (owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
            //checks if target is not player, thus can only be a mob to transcend
            if (!(owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get() instanceof Player)) {
                if (owner.getState().getId() != 4) {
                    BehaviorUtils.setWalkAndLookTargetMemories(owner, owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(), 1, 0);
                }
            } else {

                // sets position target for unarmed attack
                if (owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).isEmpty() && owner.getState().getId() == 3) {
                    BehaviorUtils.setWalkAndLookTargetMemories(owner, owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(), 1, 0);

                    //sets position target to get weapon
                } else if (owner.getState().getId() == 3 && owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).isPresent()) {
                    owner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(
                            owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get(),
                            1F,
                            1));
                }

                //sets position target for armed attack
                if (owner.getState().getId() == 4) {
                    BehaviorUtils.setWalkAndLookTargetMemories(owner, owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(), 1, 5);
                }

                //stops the mob from moving while performing weapon attacked
                if (owner.getState().getId() == 7) {
                    owner.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                }

            }
        } else {
            // sets the position target for the revenant to go place the weapon back

            if (owner.getState().getId() == 4 && owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).isPresent()) {

                owner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(
                        owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get(),
                        1F,
                        1));
            }
        }


    }


    @Override
    protected boolean canStillUse(ServerLevel level, RevenantEntity entity, long gameTime) {
        return true;
    }
}
