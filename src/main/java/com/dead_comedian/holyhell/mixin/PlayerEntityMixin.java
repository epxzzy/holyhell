package com.dead_comedian.holyhell.mixin;

import com.dead_comedian.holyhell.server.entity.non_living.GlobularDomeEntity;
import com.dead_comedian.holyhell.server.item.HolyhellArmorMaterials;
import com.dead_comedian.holyhell.server.registries.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Player.class)

public abstract class PlayerEntityMixin extends LivingEntity {

    @Unique
    int holyhell$blockingCounter = 0;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);

    }

    @Unique
    public int holyHell1_21_1$countArmorPieces(Player player, ArmorMaterial material) {
        int count = 0;
        for (ItemStack armorStack : player.getArmorSlots()) {
            if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorItem armorItem) {
                if (armorItem.getMaterial().value() == material) {
                    count++;
                }
            }
        }

        return count;
    }


    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        if ((this.isBlocking() && this.getMainHandItem().is(HolyHellItems.HOLY_SHIELD.get()) || this.getOffhandItem().is(HolyHellItems.HOLY_SHIELD.get()))) {
            holyhell$blockingCounter++;
        } else {
            holyhell$blockingCounter = 0;
        }
    }

    @ModifyVariable(method = "hurt", at = @At(value = "HEAD"))
    private float modifyDamage(float value, DamageSource source) {

        //Globular Dome
        List<Entity> entityBelow = this.level().getEntities(this, this.getBoundingBox().inflate(-0.1));
        for (Entity entity : entityBelow) {
            if (this.canCollideWith(entity) && entity instanceof GlobularDomeEntity) {
                return 0;
            }

        }
        entityBelow.removeAll(entityBelow);
        return value;

    }

    @Inject(method = "attack", at = @At(value = "HEAD"))
    private void attack(Entity target, CallbackInfo ci) {
        //Sword Cross
        ItemStack itemStack = this.getItemInHand(this.getUsedItemHand());
        if (itemStack.is(HolyHellItems.HOLY_GRAIL.get())) {
            this.level().playSound(this, this.blockPosition(), HolyHellSounds.SWORD_SLASH.get(), SoundSource.PLAYERS, 0.5f, 1f);
        }
        if (itemStack.is(HolyHellItems.SACRIFICIAL_KATAR.get())) {

            this.level().playSound(this, this.blockPosition(), HolyHellSounds.SWORD_SLASH.get(), SoundSource.PLAYERS, 0.5f, 2f);

        }


    }


    @Inject(method = "hurt", at = @At(value = "HEAD"), cancellable = true)
    private void modifyDamage(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {

        // Divine Prot
        if (this.hasEffect(HolyHellEffects.DIVINE_PROTECTION) && !damageSource.is(HolyHellTags.DamageTypes.DIVINE_PROTECTION_IGNORE)) {

            switch (holyHell1_21_1$countArmorPieces(((Player) (Object) this), HolyhellArmorMaterials.EVANGELIST.value())) {
                case 1:
                    this.addEffect(new MobEffectInstance(HolyHellEffects.DIVINE_PROTECTION_COOLDOWN, 1200, 1));
                    this.level().playSound(null, ((Player) (Object) this).blockPosition(), HolyHellSounds.DIVINE_PROTECTION.get(), SoundSource.PLAYERS);
                    break;
                case 2:
                    this.addEffect(new MobEffectInstance(HolyHellEffects.DIVINE_PROTECTION_COOLDOWN, 1000, 2));
                    this.level().playSound(null, ((Player) (Object) this).blockPosition(), HolyHellSounds.DIVINE_PROTECTION.get(), SoundSource.PLAYERS);
                    break;
                case 3:
                    this.addEffect(new MobEffectInstance(HolyHellEffects.DIVINE_PROTECTION_COOLDOWN, 800, 3));
                    this.level().playSound(null, ((Player) (Object) this).blockPosition(), HolyHellSounds.DIVINE_PROTECTION.get(), SoundSource.PLAYERS);
                    break;
                case 4:
                    this.addEffect(new MobEffectInstance(HolyHellEffects.DIVINE_PROTECTION_COOLDOWN, 600, 4));
                    this.level().playSound(null, ((Player) (Object) this).blockPosition(), HolyHellSounds.DIVINE_PROTECTION.get(), SoundSource.PLAYERS);
                    break;

            }
            if (this.level() instanceof ServerLevel world) {
                world.sendParticles(HolyHellParticles.LIGHT_RING.get(), this.getX(), this.getEyeY(), this.getZ(), 1, 0, 0.1, 0, 0);

            }

            cir.cancel();

        }
    }


}