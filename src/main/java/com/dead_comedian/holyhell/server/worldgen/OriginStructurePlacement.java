package com.dead_comedian.holyhell.server.worldgen;

import com.dead_comedian.holyhell.server.registries.HolyHellStructurePlacements;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;

public class OriginStructurePlacement extends StructurePlacement {

    public static final MapCodec<OriginStructurePlacement> CODEC = RecordCodecBuilder.mapCodec(builder ->
            StructurePlacement.placementCodec(builder)
                    .apply(builder, OriginStructurePlacement::new));

    protected OriginStructurePlacement(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<ExclusionZone> exclusionZone) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState structureState, int x, int z) {
        structureState.ensureStructuresGenerated();
        return x == 0 && z == 0;
    }



    @Override
    public StructurePlacementType<?> type() {
        return HolyHellStructurePlacements.ORIGIN_STRUCTURE_PLACEMENT_TYPE.get();
    }
}
