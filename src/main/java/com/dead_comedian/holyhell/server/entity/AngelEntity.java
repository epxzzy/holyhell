package com.dead_comedian.holyhell.server.entity;


import com.dead_comedian.holyhell.server.entity.non_living.AngelProjectileEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellEntities;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class AngelEntity extends Monster implements RangedAttackMob {


    ///////////////
    // VARIABLES //
    /// ////////////


    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;


    //////////
    // MISC //

    /// ////////

    public AngelEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }


    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        if (pSource.getEntity() instanceof KamikazeEntity) {
            return true;
        } else {
            return super.isInvulnerableTo(pSource);
        }

    }


    @Override
    public void tick() {
        super.tick();


        if (this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoala(this, 1.25, 5, 10F));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .add(Attributes.ARMOR, 1.3f)
                .add(Attributes.ATTACK_DAMAGE, 2)
                .add(Attributes.FOLLOW_RANGE, 25);
    }


    ///////////////
    // ANIMATION //

    /// ////////////


    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0 && this.getPose() == Pose.STANDING) {
            this.idleAnimationTimeout = this.random.nextInt(30) + 60;

            this.idleAnimationState.start(this.tickCount);

        }


    }

    @Override
    protected void updateWalkAnimation(float posDelta) {
        float f = this.getPose() == Pose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.walkAnimation.update(f, 0.2f);
    }


    ////////
    // AI //

    /// /////

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {

        double radius = 1.5;           // distance from angel center
        double sideShootStrength = 0.9; // how hard it shoots outward

        // Get forward direction
        Vec3 forward = this.getLookAngle();
        Vec3 flatForward = new Vec3(forward.x, 0, forward.z);

        if (flatForward.lengthSqr() < 0.001) {
            flatForward = new Vec3(0, 0, 1);
        }

        flatForward = flatForward.normalize();

        // Horizontal right vector (90° rotated)
        Vec3 right = new Vec3(-flatForward.z, 0, flatForward.x).normalize();

        // ===== LEFT PROJECTILE =====
        Vec3 leftOffset = right.scale(-radius);
        Vec3 leftSpawn = this.position()
                .add(leftOffset)
                .add(0, this.getBbHeight() * 0.6, 0);

        AngelProjectileEntity leftProjectile =
                new AngelProjectileEntity(this.level(), this, target);

        leftProjectile.setPos(leftSpawn);
        leftProjectile.setDeltaMovement(right.scale(-sideShootStrength));

        this.level().addFreshEntity(leftProjectile);

        // ===== RIGHT PROJECTILE =====
        Vec3 rightOffset = right.scale(radius);
        Vec3 rightSpawn = this.position()
                .add(rightOffset)
                .add(0, this.getBbHeight() * 0.6, 0);

        AngelProjectileEntity rightProjectile =
                new AngelProjectileEntity(this.level(), this, target);

        rightProjectile.setPos(rightSpawn);
        rightProjectile.setDeltaMovement(right.scale(sideShootStrength));

        this.level().addFreshEntity(rightProjectile);

        this.playSound(SoundEvents.FIRECHARGE_USE,
                0.4F,
                0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }


    public static class RangedAttackGoala extends Goal {
        private final Mob mob;
        private final RangedAttackMob rangedAttackMob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public RangedAttackGoala(RangedAttackMob rangedAttackMob, double speedModifier, int attackInterval, float attackRadius) {
            this(rangedAttackMob, speedModifier, attackInterval, attackInterval, attackRadius);
        }

        public RangedAttackGoala(RangedAttackMob rangedAttackMob, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius) {
            if (!(rangedAttackMob instanceof LivingEntity)) {
                throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
            } else {
                this.rangedAttackMob = rangedAttackMob;
                this.mob = (Mob) rangedAttackMob;
                this.speedModifier = speedModifier;
                this.attackIntervalMin = attackIntervalMin;
                this.attackIntervalMax = attackIntervalMax;
                this.attackRadius = attackRadius;
                this.attackRadiusSqr = attackRadius * attackRadius;
                this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            }
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
        }

        @Override
        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
            if (flag) {
                this.seeTime++;
            } else {
                this.seeTime = 0;
            }

            if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().moveTo(this.target, this.speedModifier);
            }

            this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (--this.attackTime == 0) {
                if (!flag) {
                    return;
                }

                float f = (float) Math.sqrt(d0) / this.attackRadius;
                float f1 = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackMob.performRangedAttack(this.target, f1);
                this.attackTime = 100 + Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
            } else if (this.attackTime < 0) {
                this.attackTime = 100 + Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, (double) this.attackIntervalMin, (double) this.attackIntervalMax));
            }
        }
    }


    ///////////
    // SOUND //

    /// ////////


    protected SoundEvent getStepSound() {
        return HolyHellSounds.ANGEL_FLUTTER.get();
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return HolyHellSounds.ANGEL_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return HolyHellSounds.ANGEL_HURT.get();
    }

}