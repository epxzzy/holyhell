package com.dead_comedian.holyhell.server.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class StatueData {


    public record StatueCodec(Block top, Block statue) {
        public static final Codec<StatueCodec> STATUE_CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("top").forGetter(StatueCodec::top),
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("statue").forGetter(StatueCodec::statue)

                        ).apply(instance, StatueCodec::new)
        );
    }



    
    public record FullStatueCodec( List<StatueCodec> statuePairs) {


        public static final Codec<FullStatueCodec> CODEC = RecordCodecBuilder.create(instance -> // Given an instance
                instance.group(
                        StatueCodec.STATUE_CODEC.listOf().fieldOf("statue_pairs").forGetter(FullStatueCodec::statuePairs    )
                ).apply(instance, FullStatueCodec::new)
        );


    }
}