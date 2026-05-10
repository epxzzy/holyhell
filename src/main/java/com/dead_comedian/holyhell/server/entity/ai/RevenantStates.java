package com.dead_comedian.holyhell.server.entity.ai;

import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum RevenantStates {
    CATATONIC(0, false),
    SIT_UP(1, false),
    SIT_DOWN(2, false),
    UNARMED(3, true),
    ARMED(4, true),
    WOLOLO(5, false);

    public static final IntFunction<RevenantStates> BY_ID = ByIdMap.continuous(RevenantStates::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int id;
    private final boolean canMove;

    RevenantStates(int index, boolean canMove) {
        this.id = index;
        this.canMove = canMove;
    }

    public int getId() {
        return this.id;
    }

    public boolean canMove() {
        return this.canMove;
    }

}
