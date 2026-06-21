package com.dead_comedian.holyhell.server.block.property;

import net.minecraft.util.StringRepresentable;

public enum CoffinState implements StringRepresentable {
    CLOSED("closed"),
    ACTIVATED("activated"),
    OPEN("open"),
    CLOSING("closing");

    private final String name;

    CoffinState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
