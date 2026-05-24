package com.dead_comedian.holyhell.server.entity.ai;

import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum RevenantStates {
    CATATONIC(0),
    SIT_UP(1),
    SIT_DOWN(2),
    UNARMED(3),
    ARMED(4),
    WOLOLO(5),
    ATTACK_UNARMED(6),
    ATTACK_ARMED(7);



    RevenantStates(int index) {
        this.id = index;
    }



    public static final IntFunction<RevenantStates> BY_ID = ByIdMap.continuous(RevenantStates::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int id;

    public int getId() {
        return this.id;
    }
}
