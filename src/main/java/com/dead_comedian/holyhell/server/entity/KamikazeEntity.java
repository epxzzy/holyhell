package com.dead_comedian.holyhell.server.entity;


import com.dead_comedian.holyhell.server.entity.ai.KamikazeAi;
import com.dead_comedian.holyhell.server.registries.HolyHellParticles;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
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

public class KamikazeEntity extends Monster implements FlyingAnimal {


    ///////////////
    // VARIABLES //
    /// ////////////

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    public int flutterLoop = 24;


    ///////////
    // BRAIN //

    /// ////////


    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("kamikazeBrain");
        ((Brain<KamikazeEntity>) this.brain).tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("kamikazeActivityUpdate");
        KamikazeAi.updateActivity(this);
        this.level().getProfiler().pop();

        super.customServerAiStep();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return KamikazeAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    protected Brain.Provider<KamikazeEntity> brainProvider() {
        return Brain.provider(KamikazeAi.MEMORY_MODULES, KamikazeAi.SENSORS);
    }

    @Override
    public Brain<KamikazeEntity> getBrain() {
        return (Brain<KamikazeEntity>) super.getBrain();
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
        if (!this.isAlive()) {
            this.level().addParticle(HolyHellParticles.KAMIKAZE_EXPLOSION.get(), this.getX(), this.getY(), this.getZ(), 0.1, 0.1, 0.1);

        }


        if (this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes().add(Attributes.MAX_HEALTH, 5f)
                .add(Attributes.FLYING_SPEED, 1.2)
                .add(Attributes.MOVEMENT_SPEED, 0.7)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 30);
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
        return world.getBlockState(pos).isAir() ? 20.0F : 1.4F;
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
        } else if (source.getEntity() instanceof Player player) {

            return false;
        } else {
            return false;
        }

    }


    @Override
    public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float explosionPower) {
        return false;
    }


    public void explode(double power) {
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) (3 * power), Level.ExplosionInteraction.MOB);
            this.discard();
        }

    }


    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (!(damageSource.getDirectEntity() instanceof AbstractArrow) &&
                (damageSource.is(DamageTypes.GENERIC_KILL) &&
                        damageSource.getDirectEntity() instanceof Player player &&
                        player.isCreative())
                && damageSource != damageSources().fellOutOfWorld()
                && damageSource != damageSources().inWall()) {


            if (damageSource.getDirectEntity() != null) {
                this.knockback(2, damageSource.getDirectEntity().getLookAngle().x(), damageSource.getDirectEntity().getLookAngle().z());
//                this.addDeltaMovement(damageSource.getDirectEntity().getLookAngle().multiply(-20,20,-20));
            }
            return false;
        } else {
            return super.isInvulnerableTo(damageSource);
        }
    }


}