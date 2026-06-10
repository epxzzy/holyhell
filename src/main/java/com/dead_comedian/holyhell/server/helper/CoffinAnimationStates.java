package com.dead_comedian.holyhell.server.helper;

import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum CoffinAnimationStates {
    NONE(0),
    CHARGE(1),
    OPEN(2),
    CLOSE(3);


    CoffinAnimationStates(int index) {
        this.id = index;
    }


    public static final IntFunction<CoffinAnimationStates> BY_ID = ByIdMap.continuous(CoffinAnimationStates::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int id;

    public int getId() {
        return this.id;
    }
}
