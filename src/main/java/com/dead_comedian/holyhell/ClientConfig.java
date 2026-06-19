package com.dead_comedian.holyhell;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue RING_SIZE;

    static {
        BUILDER.push("features");

        RING_SIZE = BUILDER
                .comment("Choose the size of the model displayed when player has Divine Protection effect")
                .comment("Use 0 to disable the visual, ranges between 0 and 2, and supports decimals")
                .comment("This is a client config so only you will see it and will apply to you and other players as well")
                .gameRestart()
                .defineInRange("ring_size", 1D, 0D, 2D);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
