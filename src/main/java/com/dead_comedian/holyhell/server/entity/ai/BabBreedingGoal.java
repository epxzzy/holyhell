package com.dead_comedian.holyhell.server.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class BabBreedingGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0).ignoreLineOfSight();
    protected final TamableAnimal animal;
    private final Class<? extends TamableAnimal> partnerClass;
    protected final Level level;
    @Nullable
    protected TamableAnimal partner;
    private final double speedModifier;
    private final EntityType<? extends TamableAnimal> bebe;

    public BabBreedingGoal(TamableAnimal animal, EntityType<? extends TamableAnimal> baba, double speedModifier) {
        this(animal, baba, speedModifier, (Class<? extends TamableAnimal>) animal.getClass());
    }

    public BabBreedingGoal(TamableAnimal animal, EntityType<? extends TamableAnimal> bebe, double speedModifier, Class<? extends TamableAnimal> partnerClass) {
        this.animal = animal;
        this.bebe = bebe;
        this.level = animal.level();
        this.partnerClass = partnerClass;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.partner = this.getFreePartner();
            return this.partner != null && (this.animal.getOwner() != null && (this.animal.getOwner() == this.partner.getOwner() || !this.partner.isTame()));
        }
    }

    @Override
    public boolean canContinueToUse() {
        return (this.animal.getOwner() != null && (this.animal.getOwner() == this.partner.getOwner() || !this.partner.isTame())) && (this.partner.isAlive() && this.partner.isInLove() && !this.partner.isPanicking());
    }

    @Override
    public void stop() {
        this.partner = null;

    }

    @Override
    public void tick() {
        this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float) this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
        if (this.animal.distanceToSqr(this.partner) < 9.0) {
            this.breed();
        }
    }

    @Nullable
    private TamableAnimal getFreePartner() {
        List<? extends TamableAnimal> list = this.level
                .getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0));
        double d0 = Double.MAX_VALUE;
        TamableAnimal animal = null;

        for (TamableAnimal animal1 : list) {
            if (this.animal.canMate(animal1) && !animal1.isPanicking() && this.animal.distanceToSqr(animal1) < d0) {
                animal = animal1;
                d0 = this.animal.distanceToSqr(animal1);
            }
        }

        return animal;
    }

    protected void breed() {
        BlockPos blockPos = this.animal.blockPosition();
        TamableAnimal bibi = bebe.create(level);

        if (bibi == null) return;

        bibi.setCustomName(this.animal.getCustomName());
        bibi.tame((Player) this.animal.getOwner());
        this.animal.level().addFreshEntity(bibi);
        bibi.moveTo(blockPos, bibi.getYRot(), bibi.getXRot());
        this.animal.discard();
        this.partner.discard();
    }
}
