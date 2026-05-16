package com.dead_comedian.holyhell.server.entity;


import com.dead_comedian.holyhell.server.registries.HolyHellEntities;
import com.dead_comedian.holyhell.server.registries.HolyHellParticles;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class KamikazeEntity extends Monster implements FlyingAnimal {


    ///////////////
    // VARIABLES //
    /// ////////////

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    public boolean isHit = false;
    public int flutterLoop = 24;

    public boolean getIsHit() {
        return isHit;
    }

    public void setIsHit(boolean boolea) {
        isHit = boolea;
    }

    //////////
    // MISC //

    /// ///////

    public KamikazeEntity(EntityType<? extends Monster> entityType, Level world) {
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
        flutterLoop--;
        if (flutterLoop >= 23) {
            this.playSound(HolyHellSounds.CHERUB_FLUTTER.get(), 1F, 1F);
        }
        if (flutterLoop <= 0) {
            flutterLoop = 24;
        }
        if (!isAlive()) {
            this.explode(2d);
            this.level().addParticle(HolyHellParticles.KAMIKAZE_EXPLOSION.get(), this.getX(), this.getY(), this.getZ(), 0.1, 0.1, 0.1);
            AngelEntity angelEntity = new AngelEntity(HolyHellEntities.ANGEL.get(), this.level());
            this.level().addFreshEntity(angelEntity);
            angelEntity.moveTo(this.getBlockX() + 0.5, this.getBlockY(), this.getBlockZ() + 0.5);
            this.discard();
        }


        if (this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 4f));
        this.goalSelector.addGoal(1, new KamikazeAngelWanderAroundGoal());
        this.goalSelector.addGoal(2, new KamikazeExplodeGoal(this, 1));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes().add(Attributes.MAX_HEALTH, 5f)
                .add(Attributes.FLYING_SPEED, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.7)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 30);

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
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


    /// ////////////
    // EXPLOSION //

    /// ////////////

    @Override
    public boolean hurt(DamageSource source, float amount) {

        if (source.isCreativePlayer() || source.getEntity() instanceof Projectile || source.is(DamageTypes.GENERIC_KILL)) {
            return super.hurt(source, amount);
        } else if (source.getEntity() instanceof Player) {
            this.knockback(2, 1, 1);
            return false;
        } else {
            return false;
        }

    }


    @Override
    public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float explosionPower) {
        return false;
    }


    protected void explode(double power) {


        if (!this.level().isClientSide) {


            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) (3 * power), Level.ExplosionInteraction.MOB);

            this.discard();

        }

    }

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);
        if (!(player.isCreative() || player.isSpectator())) {
            this.kill();
        }
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }


    ////////
    // AI //

    /// /////


    class KamikazeAngelWanderAroundGoal extends Goal {
        KamikazeAngelWanderAroundGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return KamikazeEntity.this.navigation.isDone() && KamikazeEntity.this.random.nextInt(10) == 0;
        }

        public boolean canContinueToUse() {
            return KamikazeEntity.this.navigation.isInProgress();
        }

        public void start() {
            Vec3 vec3d = this.getRandomLocation();
            if (vec3d != null) {
                KamikazeEntity.this.navigation.moveTo(KamikazeEntity.this.navigation.createPath(BlockPos.containing(vec3d), 1), 1.0);
            }

        }

        @Nullable
        private Vec3 getRandomLocation() {
            Vec3 vec3d2;
            vec3d2 = KamikazeEntity.this.getViewVector(0.0F);

            Vec3 vec3d3 = HoverRandomPos.getPos(KamikazeEntity.this, 8, 7, vec3d2.x, vec3d2.z, 1.5707964F, 3, 1);
            return vec3d3 != null ? vec3d3 : AirAndWaterRandomPos.getPos(KamikazeEntity.this, 8, 4, -2, vec3d2.x, vec3d2.z, 1.5707963705062866);
        }

    }


    class KamikazeExplodeGoal extends Goal {
        private final KamikazeEntity entity;
        private int timeSinceAttack;
        private double speednt;
        private final PathNavigation navigation;
        protected TargetingConditions targetPredicate;

        public KamikazeExplodeGoal(PathfinderMob mob, double speed) {
            entity = ((KamikazeEntity) mob);
            speednt = speed;
            this.navigation = mob.getNavigation();
            this.targetPredicate = TargetingConditions.forCombat().range(this.entity.getAttributeValue(Attributes.FOLLOW_RANGE)).selector((Predicate<LivingEntity>) targetPredicate);
        }

        @Override
        public boolean canUse() {
            return entity.getTarget() != null;
        }

        @Override
        public void start() {
            super.start();
            timeSinceAttack = 0;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.entity.getTarget() != null) {
                if (entity.distanceTo(this.entity.getTarget()) > 8) {


                    if (navigation.isDone()) {
                        this.navigation.moveTo(this.entity.getTarget().getX() + random.nextInt(-4, 4),
                                this.entity.getTarget().getY() + 3,
                                this.entity.getTarget().getZ() + random.nextInt(-3, 3),
                                this.speednt * 0.8);
                    }
                } else {
                    entity.lookAt(this.entity.getTarget(), 90, 90);
                    entity.addDeltaMovement(entity.getLookAngle().scale(0.25));
                    if (this.entity.getIsHit()) {
                        setIsHit(false);
                        this.timeSinceAttack = 0;
                    }
                }
            }
        }
    }


    // attacking

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(KamikazeEntity.class, EntityDataSerializers.BOOLEAN);

    public void setAggressive(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    @Override
    public boolean isAggressive() {
        return this.entityData.get(ATTACKING);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (!(damageSource.getDirectEntity() instanceof AbstractArrow) && damageSource.is(DamageTypes.GENERIC_KILL)) {
            if (damageSource.getDirectEntity() != null) {
                this.addDeltaMovement(damageSource.getDirectEntity().getLookAngle());
            }
            setIsHit(true);
            return false;
        } else {
            return super.isInvulnerableTo(damageSource);
        }
    }


}