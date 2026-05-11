package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.entity.ai.RevenantStates;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import com.dead_comedian.holyhell.server.registries.HolyHellTags;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class Ritual extends Behavior<RevenantEntity> {

    LivingEntity targetEntity;
    private static final int DURATION = 20;

    public Ritual() {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), DURATION
        );
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, RevenantEntity owner) {
        targetEntity = owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        return owner.distanceTo(targetEntity) < 2 &&
                owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                        .get().getType().is(HolyHellTags.Entities.REVENANT_TRANSCENDS)
                && owner.getState().getId() == 3;
    }

    @Override
    protected void start(ServerLevel level, RevenantEntity entity, long gameTime) {
        super.start(level, entity, gameTime);
        entity.setState(RevenantStates.WOLOLO);
        entity.playSound(HolyHellSounds.ULULU.get());
    }

    @Override
    protected void stop(ServerLevel level, RevenantEntity entity, long gameTime) {
        super.stop(level, entity, gameTime);
        RandomSource rng = entity.level().getRandom();

        if (rng.nextInt(7) == 6) {
            entity.level().playSound(null, entity.blockPosition(), HolyHellSounds.PERISH.get(), SoundSource.HOSTILE, 1.2f, entity.getVoicePitch());
        } else if (rng.nextInt(13) == 9) {
            entity.level().playSound(null, entity.blockPosition(), HolyHellSounds.DISSAPEAR.get(), SoundSource.HOSTILE, 1.2f, entity.getVoicePitch());
        } else {
            entity.level().playSound(null, entity.blockPosition(), HolyHellSounds.MOB_PASSES.get(), SoundSource.HOSTILE, 1.2f, entity.getVoicePitch());
        }

        targetEntity.discard();
        entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        entity.setState(RevenantStates.UNARMED);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, RevenantEntity entity, long gameTime) {
        return targetEntity != null && targetEntity.getType().is(HolyHellTags.Entities.REVENANT_TRANSCENDS);
    }

}
