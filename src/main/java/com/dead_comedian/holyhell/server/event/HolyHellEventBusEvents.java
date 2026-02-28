package com.dead_comedian.holyhell.server.event;


import com.dead_comedian.holyhell.Holyhell;
import com.dead_comedian.holyhell.client.event.EndTextOverlay;
import com.dead_comedian.holyhell.server.data.StatueData;
import com.dead_comedian.holyhell.server.entity.*;
import com.dead_comedian.holyhell.server.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Objects;

import static com.dead_comedian.holyhell.Holyhell.LOGGER;


@EventBusSubscriber(modid = Holyhell.MOD_ID)
public class HolyHellEventBusEvents {

    private static int paranoiaTimer;
    private static int paranoiaAmp;
    private static int secTillText = 40;
    private static int musicDuration = 1660;
    private static int musicCooldown;
    private static int explosionMagnitude;

    public static List<? extends AllSeerEntity> getEyes(Level level) {
        if (level instanceof ServerLevel) {
            return null;
        }
        return null;
    }


    @SubscribeEvent
    public static void changePumpkinFace(PlayerInteractEvent.RightClickBlock event) {
        Block pumpkin = event.getLevel().getBlockState(event.getPos()).getBlock();
        if (event.getItemStack().is(Tags.Items.TOOLS_SHEAR)) {
            event.getLevel().playSound((Player) null, event.getPos(), SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (pumpkin == Blocks.CARVED_PUMPKIN) {
                event.getLevel().setBlock(event.getPos(), HolyHellBlocks.CARVED_PUMPKIN_EYE.get().defaultBlockState(), 11);
            }
            if (pumpkin == HolyHellBlocks.CARVED_PUMPKIN_EYE.get()) {
                event.getLevel().setBlock(event.getPos(), HolyHellBlocks.CARVED_PUMPKIN_CROSS.get().defaultBlockState(), 11);
            }
            if (pumpkin == HolyHellBlocks.CARVED_PUMPKIN_CROSS.get()) {
                event.getLevel().setBlock(event.getPos(), Blocks.CARVED_PUMPKIN.defaultBlockState(), 11);
            }

            if (pumpkin == Blocks.JACK_O_LANTERN) {
                event.getLevel().setBlock(event.getPos(), HolyHellBlocks.JACK_O_LANTERN_EYE.get().defaultBlockState(), 11);
            }
            if (pumpkin == HolyHellBlocks.JACK_O_LANTERN_EYE.get()) {
                event.getLevel().setBlock(event.getPos(), HolyHellBlocks.JACK_O_LANTERN_CROSS.get().defaultBlockState(), 11);
            }
            if (pumpkin == HolyHellBlocks.JACK_O_LANTERN_CROSS.get()) {
                event.getLevel().setBlock(event.getPos(), Blocks.JACK_O_LANTERN.defaultBlockState(), 11);
            }
        }
    }


    @SubscribeEvent
    public static void inAngel(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().dimension() == HolyhellDimensions.ANGEL) {

            if (musicDuration == 1660) {
                player.level().playLocalSound(player, HolyHellSounds.HEAVENS_VOICE.get(), SoundSource.MUSIC, 1, 1);

            }
            if (musicDuration <= 1660) {
                musicDuration--;
//                    Minecraft.getInstance().getMusicManager().stopPlaying();
            }
            if (musicDuration <= 0) {
                musicCooldown = player.level().getRandom().nextInt(200, 9000);
                musicDuration = 1;
            }
            if (musicCooldown <= 0 && musicDuration == 1) {
                musicDuration = 1660;
            }

            player.setData(HolyHellAttachments.VISION_SHADER, false);

            player.getAbilities().flying = true;
        }

    }

    @SubscribeEvent
    public static void spawnAllSeer(PlayerTickEvent.Post event) {


        Level level = event.getEntity().level();

        if (level.dimension() == HolyhellDimensions.ANGEL) {
            List<? extends AllSeerEntity> list = getEyes(level);

            if (list != null) {
                list.removeIf(Objects::isNull);
                if (list.isEmpty()) {

                    if (level.dimension() == HolyhellDimensions.ANGEL) {
                        LOGGER.debug("Haven't seen the All Seer , respawning it");
                    }


                    level.getChunkAt(new BlockPos(0, 0, 0));
                    AllSeerEntity allSeerEntity = HolyHellEntities.ALL_SEER.get().create(level);
                    if (allSeerEntity != null) {
                        level.addFreshEntity(allSeerEntity);
                    }
                } else if (list.size() >= 2) {

                    for (AllSeerEntity allSeerEntity : list) {
                        if (allSeerEntity != list.get(0)) {
                            allSeerEntity.discard();
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void teleportPlayer(PlayerTickEvent.Post event) {
        Player player = event.getEntity();


        if (player.level().dimension() == Level.END && player.getY() <= -50) {
            if (player.getData(HolyHellAttachments.CAN_TP_TO_ANGEL)) {
                if (player.level() instanceof ServerLevel serverLevel) {

                    ServerLevel targetLevel = serverLevel.getServer().getLevel(HolyhellDimensions.ANGEL);
                    if (targetLevel != null) {
                        player.removeEffect(HolyHellEffects.ANGELIC_VISION);
                        player.changeDimension(new DimensionTransition(targetLevel, new Vec3(20, 12, 0), player.getDeltaMovement(), Direction.EAST.toYRot(), player.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)));
//                        player.setData(HolyHellAttachments.CAN_TP_TO_ANGEL, false);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void paranoiaTimer(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.getData(HolyHellAttachments.VISION_SHADER)) {
            if (player.hasEffect(HolyHellEffects.PARANOIA)) {
                paranoiaTimer = player.getEffect(HolyHellEffects.PARANOIA).getDuration();
                paranoiaAmp = player.getEffect(HolyHellEffects.PARANOIA).getAmplifier();
            } else {
                paranoiaTimer++;
            }


            if (paranoiaTimer == 300 && !player.hasEffect(HolyHellEffects.PARANOIA)) {
                player.addEffect(new MobEffectInstance(HolyHellEffects.PARANOIA, 200, 0));
            } else if (paranoiaTimer == 0 && paranoiaAmp == 0) {
                player.addEffect(new MobEffectInstance(HolyHellEffects.PARANOIA, 300, 1));
            } else if (paranoiaTimer == 0 && paranoiaAmp == 1) {
                player.addEffect(new MobEffectInstance(HolyHellEffects.PARANOIA, 400, 2));
            } else if (paranoiaTimer == 0 && paranoiaAmp == 2) {
                player.addEffect(new MobEffectInstance(HolyHellEffects.PARANOIA, 500, 3));
            }

            if (paranoiaAmp == 3 && player.level().dimension() == Level.END) {
                if (!player.getData(HolyHellAttachments.CAN_TP_TO_ANGEL)) {
                    if (secTillText > 0) {
                        secTillText--;
                    } else {
                        if (EndTextOverlay.textCounter == 185) {
                            EndTextOverlay.textCounter = 0;
                            player.setData(HolyHellAttachments.SHOULD_DISPLAY_TEXT, true);
                        }
                    }
                }
            }


        } else {
            if (player.hasEffect(HolyHellEffects.PARANOIA)) {
                player.removeEffect(HolyHellEffects.PARANOIA);
            }
            paranoiaTimer = 0;
        }
    }

    @SubscribeEvent
    public static void jesistence(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();

        if (entity.getEffect(HolyHellEffects.JESISTANCE) != null) {
            int duration = entity.getEffect(HolyHellEffects.JESISTANCE).getDuration();
            int amp = entity.getEffect(HolyHellEffects.JESISTANCE).getAmplifier();

            event.setNewDamage((float) (event.getOriginalDamage() - (event.getOriginalDamage() * (entity.getEffect(HolyHellEffects.JESISTANCE).getAmplifier() + 1) * 0.25)));
            entity.removeEffect(HolyHellEffects.JESISTANCE);
            entity.addEffect(new MobEffectInstance(HolyHellEffects.JESISTANCE, duration - (int) event.getNewDamage()*10, amp   ));
        }
    }

    /// ////////////////////////

    private static boolean isAprilFools() {
        LocalDate localdate = LocalDate.now();

        Month j = localdate.getMonth();
        return j.equals(Month.APRIL );
    }

    @SubscribeEvent
    public static void spawnHolyCow(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Cow cow && !(event.getEntity() instanceof HolyCowEntity)) {
            if (isAprilFools()) {
                BlockPos blockPos = cow.blockPosition();
                HolyCowEntity holyCowEntity  = new HolyCowEntity(HolyHellEntities.HOLY_COW.get(), event.getLevel());
                event.getLevel().addFreshEntity(holyCowEntity);
                holyCowEntity.moveTo(blockPos, holyCowEntity.getYRot(), holyCowEntity.getXRot());
                cow.discard();
            }
        }
    }

    /// /////////////////////
    @SubscribeEvent
    public static void datapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(HolyHellCodecs.STATUES, StatueData.FullStatueCodec.CODEC, StatueData.FullStatueCodec.CODEC);

    }

    @SubscribeEvent
    public static void triggerFallingCrossAchievement(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            DamageSource damageSource = event.getSource();

            if (damageSource.is(DamageTypes.FALLING_BLOCK)) {
                Entity directEntity = damageSource.getDirectEntity();

                if (directEntity instanceof FallingBlockEntity fallingBlock) {
                    BlockState blockState = fallingBlock.getBlockState();

                    if (blockState.getBlock() == HolyHellBlocks.FALLING_CROSS.get()) {
                        if (player instanceof ServerPlayer) {
                            HolyHellCriteriaTriggers.KILLED_BY_CROSS.get().trigger(((ServerPlayer) (Object) player));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(HolyHellEntities.ANGEL.get(), AngelEntity.createAttributes().build());
        event.put(HolyHellEntities.ALL_SEER.get(), AllSeerEntity.createAttributes().build());
        event.put(HolyHellEntities.BAB_ONE.get(), BabOneEntity.createAttributes().build());
        event.put(HolyHellEntities.BAB_TWO.get(), BabTwoEntity.createAttributes().build());
        event.put(HolyHellEntities.BAB_THREE.get(), BabThreeEntity.createAttributes().build());
        event.put(HolyHellEntities.CHERUB.get(), CherubEntity.createAttributes().build());
        event.put(HolyHellEntities.HERETIC.get(), HereticEntity.createAttributes().build());
        event.put(HolyHellEntities.HOLY_COW.get(), HolyCowEntity.createAttributes().build());
        event.put(HolyHellEntities.HOLY_SPIRIT.get(), HolySpiritEntity.createAttributes().build());
        event.put(HolyHellEntities.KAMIKAZE.get(), KamikazeEntity.createAttributes().build());
    }


}