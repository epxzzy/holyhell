package com.dead_comedian.holyhell.server.entity;


import com.dead_comedian.holyhell.server.block.entity.DiviningTableBlockEntity;
import com.dead_comedian.holyhell.server.helper.WaveSpawner;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import com.dead_comedian.holyhell.server.registries.HolyhellParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class CherubEntity extends Monster implements FlyingAnimal {
    ///////////////
    // VARIABLES //
    /// ////////////


    private List<Entity> trackedEntities = new ArrayList<>();

    public int flutterLoop = 24;
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    float damageMultiplier = 0;

    private BlockPos blockPos;


    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    /*
     * 0 = Kamikaze
     * 1 = Angel
     * 2 = Heretic
     * */
    int[] mobSpawnIndex = new int[]{5, 3, 4};


    //////////
    // MISC //

    /// ///////


    public CherubEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
        this.setPathfindingMalus(PathType.FENCE, -1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            setupAnimationStates();
        }

        flutterLoop--;
        if (flutterLoop >= 23) {
            this.playSound(HolyHellSounds.CHERUB_FLUTTER.get(), 1F, 1F);
        }
        if (flutterLoop <= 0) {
            flutterLoop = 24;
        }


    }

    @Override
    public void die(DamageSource damageSource) {
        trackedEntities.clear();

        super.die(damageSource);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 4f));
        this.goalSelector.addGoal(1, new CherubWanderAroundGoal());

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes().add(Attributes.MAX_HEALTH, 15f).add(Attributes.FLYING_SPEED, 1).add(Attributes.MOVEMENT_SPEED, 0.7).add(Attributes.FOLLOW_RANGE, 15.0);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        if (!(this.level() instanceof ServerLevel server)) {
            return super.hurt(source, amount);
        }

        server.sendParticles(
                HolyhellParticles.SOUND_RING.get(),
                this.getX(),
                this.getY(),
                this.getZ(),
                1,
                0,0,0,0
        );

        float damageMultiplier = Math.clamp(amount,0,100) * random.nextFloat();

        int difficulty = (int) damageMultiplier > 0 ? (int) damageMultiplier : 1;

        List<Entity> wave =
                WaveSpawner.spawnWave(server,this.blockPosition(),difficulty);

        if(blockPos != null &&
                server.getBlockEntity(blockPos) instanceof DiviningTableBlockEntity table) {

            table.setTrackedEntities(wave);
            table.setDifficulty(difficulty);
        }

        this.discard();

        return super.hurt(source, amount);
    }



    ////////////////
    // NAVIGATION //

    /// /////////////


    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation birdNavigation = new FlyingPathNavigation(this, world) {
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }
        };
        birdNavigation.setCanOpenDoors(false);
        birdNavigation.setCanFloat(false);
        birdNavigation.setCanPassDoors(true);
        return birdNavigation;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader world) {
        return world.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    // fall
    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        fallDistance = 0;
    }


    ///////////////
    // ANIMATION //

    /// ////////////


    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(25) + 50;

            this.idleAnimationState.start(this.tickCount);


        } else {
            --this.idleAnimationTimeout;
        }


    }

    @Override
    protected void updateWalkAnimation(float posDelta) {
        float f = this.getPose() == Pose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.walkAnimation.update(f, 0.2f);
    }


    class CherubWanderAroundGoal extends Goal {
        CherubWanderAroundGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return CherubEntity.this.navigation.isDone() && CherubEntity.this.random.nextInt(10) == 0;
        }

        public boolean canContinueToUse() {
            return CherubEntity.this.navigation.isInProgress();
        }

        public void start() {
            Vec3 vec3d = this.getRandomLocation();
            if (vec3d != null) {
                CherubEntity.this.navigation.moveTo(CherubEntity.this.navigation.createPath(BlockPos.containing(vec3d), 1), 1.0);
            }

        }

        @Nullable
        private Vec3 getRandomLocation() {
            Vec3 vec3d2;
            vec3d2 = CherubEntity.this.getViewVector(0.0F);

            Vec3 vec3d3 = HoverRandomPos.getPos(CherubEntity.this, 8, 7, vec3d2.x, vec3d2.z, 1.5707964F, 3, 1);
            return vec3d3 != null ? vec3d3 : AirAndWaterRandomPos.getPos(CherubEntity.this, 8, 4, -2, vec3d2.x, vec3d2.z, 1.5707963705062866);
        }

    }
}