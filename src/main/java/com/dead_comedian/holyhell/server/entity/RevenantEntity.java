package com.dead_comedian.holyhell.server.entity;


import com.dead_comedian.holyhell.server.entity.ai.RevenantAi;
import com.dead_comedian.holyhell.server.entity.ai.RevenantStates;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;

public class RevenantEntity extends Monster {

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(RevenantEntity.class, EntityDataSerializers.INT);

    /// ///////////////////////////////

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public final AnimationState catatonicAnimationState = new AnimationState();
    public final AnimationState catatonicRiseAnimationState = new AnimationState();
    public final AnimationState catatonicSitAnimationState = new AnimationState();

    public int catatonicRiseAnimationTimeout = 0;
    public int catatonicSitAnimationTimeout = 0;


    public RevenantStates getState() {
        int stateId = this.entityData.get(STATE);

        return RevenantStates.BY_ID.apply(stateId);
    }

    public void setState(RevenantStates state) {
        this.entityData.set(STATE, state.getId());
    }


    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("state", this.getState().getId());
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        RevenantStates state = RevenantStates.BY_ID.apply(nbt.getInt("state"));
        this.setState(state);
    }

    public RevenantEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.setPathfindingMalus(PathType.LEAVES, 0.0F);
    }


    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("revenantBrain");
        ((Brain<RevenantEntity>) this.brain).tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("revenantActivityUpdate");
        RevenantAi.updateActivity(this);
        this.level().getProfiler().pop();

        super.customServerAiStep();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return RevenantAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    protected Brain.Provider<RevenantEntity> brainProvider() {
        return Brain.provider(RevenantAi.MEMORY_MODULES, RevenantAi.SENSORS);
    }

    @Override
    public Brain<RevenantEntity> getBrain() {
        return (Brain<RevenantEntity>) super.getBrain();
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STATE, RevenantStates.CATATONIC.getId());
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 100).add(Attributes.MOVEMENT_SPEED, 0.3f).add(Attributes.ARMOR, 2f).add(Attributes.ATTACK_DAMAGE, 5);
    }

    @Override
    public void tick() {
        super.tick();
        System.out.println(this.getState());
        if (!this.getState().canMove()) {
            this.navigation.stop();
        }

        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {

        switch (this.getState().getId()){
            case 0:
                this.catatonicAnimationState.startIfStopped(tickCount);
                this.catatonicRiseAnimationState.stop();
                this.catatonicSitAnimationState.stop();

                break;
            case 1:
                this.catatonicRiseAnimationState.startIfStopped(tickCount);
                this.catatonicAnimationState.stop();
                this.catatonicSitAnimationState.stop();
                break;
            case 2:
                this.catatonicSitAnimationState.startIfStopped(tickCount);
                this.catatonicAnimationState.stop();
                this.catatonicRiseAnimationState.stop();
                break;
        }



//        if (this.getState().getId() == 1) {
//            this.catatonicAnimationState.stop();
//            if (this.getDeltaMovement().x == 0 && this.getDeltaMovement().z == 0) {
//
//                if (this.idleAnimationTimeout <= 0) {
//                    this.idleAnimationTimeout = 53;
//                    this.idleAnimationState.startIfStopped(this.tickCount);
//                } else {
//                    --this.idleAnimationTimeout;
//                }
//            } else {
//                this.idleAnimationTimeout = 53;
//                this.idleAnimationState.stop();
//            }
//        } else {
//            this.idleAnimationState.stop();
//            if (this.catatonicSitAnimationTimeout >= 19 ) {
//                this.catatonicAnimationState.startIfStopped(this.tickCount);
//            }
//        }
//
//        if (this.catatonicSitAnimationState.isStarted()) {
//            this.catatonicSitAnimationTimeout++;
//        } else {
//            this.catatonicSitAnimationTimeout = 0;
//        }
//        if (this.catatonicSitAnimationTimeout >= 20) {
//            this.catatonicSitAnimationState.stop();
//        }
//
//        if (this.catatonicRiseAnimationState.isStarted()) {
//            this.catatonicRiseAnimationTimeout++;
//        } else {
//            this.catatonicRiseAnimationTimeout = 0;
//        }
//        if (this.catatonicRiseAnimationTimeout >= 20) {
//            this.catatonicRiseAnimationState.stop();
//        }




    }

    protected void updateWalkAnimation(float v) {
        float f;
        if (this.getPose() == Pose.STANDING) {
            f = Math.min(v * 6.0F, 1.0F);
        } else {
            f = 0.0F;
        }

        this.walkAnimation.update(f, 0.2F);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean bl = super.doHurtTarget(target);

        this.playSound(HolyHellSounds.BAB_2_ATTACK.get(), 0.8F, 1F);
        setAggressive(true);
        return bl;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return this.getState().getId()==0 && !pSource.isCreativePlayer();
    }

    protected SoundEvent getStepSound() {
        return HolyHellSounds.REVENANT_WALK.get();
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(this.getStepSound(), 0.7F, this.getVoicePitch());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return HolyHellSounds.METAL_HURT.get();
    }

}