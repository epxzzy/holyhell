package com.dead_comedian.holyhell;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue ENABLE_COFFINS;

    static {
        BUILDER.push("features");
        ENABLE_COFFINS = BUILDER
                .comment("Enables coffins storing the players inventory, on death if activated. DISABLE if you have any other inventory saving mod like corpses or graves")
                .gameRestart()
                .define("enableCoffins", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
