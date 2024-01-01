/*
 * Copyright (c) 2021-2024 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.teammoeg.frostedheart.climate;

import java.util.ArrayList;

import com.teammoeg.frostedheart.FHConfig;
import com.teammoeg.frostedheart.FHDamageSources;
import com.teammoeg.frostedheart.FHEffects;
import com.teammoeg.frostedheart.climate.chunkdata.ChunkData;
import com.teammoeg.frostedheart.climate.data.FHDataManager;
import com.teammoeg.frostedheart.compat.CuriosCompat;
import com.teammoeg.frostedheart.network.PacketHandler;
import com.teammoeg.frostedheart.network.climate.FHBodyDataSyncPacket;
import com.teammoeg.frostedheart.util.FHUtils;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber
public class TemperatureUpdate {
    public static final float HEAT_EXCHANGE_CONSTANT = 0.0012F;
    public static final float SELF_HEATING_CONSTANT = 0.036F;

    private static final class HeatingEquipment {
        IHeatingEquipment e;
        ItemStack i;

        public HeatingEquipment(IHeatingEquipment e, ItemStack i) {
            this.e = e;
            this.i = i;
        }

        public float compute(float body, float env) {
            return e.compute(i, body, env);
        }
    }

    /**
     * Perform temperature tick logic
     *
     * @param event fired every tick on player
     */
    @SubscribeEvent
    public static void updateTemperature(PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == Phase.START
                && event.player instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.player;
            if (player.tickCount % 10 != 0 || player.isCreative() || player.isSpectator())
                return;
            //soak in water modifier
            if (player.isInWater()) {
                boolean hasArmor = false;
                for (ItemStack is : player.getArmorSlots()) {
                    if (!is.isEmpty()) {
                        hasArmor = true;
                        break;
                    }
                }
                MobEffectInstance current = player.getEffect(FHEffects.WET);
                if (hasArmor)
                    player.addEffect(new MobEffectInstance(FHEffects.WET, 400, 0));// punish for wet clothes
                else if (current == null || current.getDuration() < 100)
                    player.addEffect(new MobEffectInstance(FHEffects.WET, 100, 0));
            }
            //load current data
            float current = TemperatureCore.getBodyTemperature(player);
            double tspeed = FHConfig.SERVER.tempSpeed.get();
            if (current < 0) {
            	float delt=(float) (FHConfig.SERVER.tdiffculty.get().self_heat.apply(player) * tspeed);
            	player.causeFoodExhaustion(Math.min(delt,-current)*0.5f);//cost hunger for cold.
                current += delt;
            }
            //world and chunk temperature
            Level world = player.getCommandSenderWorld();
            BlockPos pos = new BlockPos(player.getX(),player.getEyeY(),player.getZ());
            float envtemp = ChunkData.getTemperature(world, pos);
            //time temperature
            float skyLight = world.getChunkSource().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos);
            float gameTime = world.getDayTime() % 24000L;
            gameTime = gameTime / (200 / 3);
            gameTime = Mth.sin((float) Math.toRadians(gameTime));
            float bt=TemperatureCore.getBlockTemp(player);
            envtemp += bt;
            envtemp += skyLight > 5.0F ?
            		(world.isRaining() ?
            				(FHUtils.isRainingAt(player.blockPosition(), world)?-8F:-5f)
            				: (gameTime * 5.0F)) 
            		: -5F;
            // burning heat
            if (player.isOnFire())
                envtemp += 150F;
            // normalize
            envtemp -= 37F;
            float keepwarm = 0;
            //list of equipments to be calculated
            ArrayList<HeatingEquipment> equipments = new ArrayList<>(7);
            for (ItemStack is : CuriosCompat.getAllCuriosIfVisible(player)) {
                if (is == null)
                    continue;
                Item it = is.getItem();
                if (it instanceof IHeatingEquipment)
                    equipments.add(new HeatingEquipment((IHeatingEquipment) it, is));
                if (it instanceof IWarmKeepingEquipment) {//only for direct warm keeping
                    keepwarm += ((IWarmKeepingEquipment) it).getFactor(player, is);
                } else {
                    IWarmKeepingEquipment iw = FHDataManager.getArmor(is);
                    if (iw != null)
                        keepwarm += iw.getFactor(player, is);
                }
            }
            for (ItemStack is : player.getArmorSlots()) {
                if (is.isEmpty())
                    continue;
                Item it = is.getItem();
                if (it instanceof IHeatingEquipment)
                    equipments.add(new HeatingEquipment((IHeatingEquipment) it, is));
                if (it instanceof IWarmKeepingEquipment) {
                    keepwarm += ((IWarmKeepingEquipment) it).getFactor(player, is);
                } else {//include inner
                    String s = ItemNBTHelper.getString(is, "inner_cover");
                    IWarmKeepingEquipment iw = null;
                    EquipmentSlot aes = Mob.getEquipmentSlotForItem(is);
                    if (s.length() > 0 && aes != null) {
                        iw = FHDataManager.getArmor(s + "_" + aes.getName());
                    } else
                        iw = FHDataManager.getArmor(is);
                    if (iw != null)
                        keepwarm += iw.getFactor(player, is);
                }
            }
            {//main hand
                ItemStack hand = player.getMainHandItem();
                Item it = hand.getItem();
                if (it instanceof IHeatingEquipment && ((IHeatingEquipment) it).canHandHeld())
                    equipments.add(new HeatingEquipment((IHeatingEquipment) it, hand));
            }
            {//off hand
                ItemStack hand = player.getOffhandItem();
                Item it = hand.getItem();
                if (it instanceof IHeatingEquipment && ((IHeatingEquipment) it).canHandHeld())
                    equipments.add(new HeatingEquipment((IHeatingEquipment) it, hand));
                ;
            }
            if (keepwarm > 1)//prevent negative
                keepwarm = 1;
            //environment heat exchange
            float dheat = HEAT_EXCHANGE_CONSTANT * (1 - keepwarm) * (envtemp - current);
            //simulate temperature transform to get heating device working
            float simulated = (float) (current / tspeed + dheat);
            for (HeatingEquipment it : equipments) {
                float addi = it.compute(simulated, envtemp);
                dheat += addi;
                simulated += addi;
            }
            if (dheat > 0.1)
                player.hurt(FHDamageSources.HYPERTHERMIA_INSTANT, (dheat) * 10);
            else if (dheat < -0.1)
                player.hurt(FHDamageSources.HYPOTHERMIA_INSTANT, (-dheat) * 10);
            current += dheat * tspeed;
            if (current < -10)
                current = -10;
            else if (current > 10)
                current = 10;
            float lenvtemp=TemperatureCore.getEnvTemperature(player);//get a smooth change in display
            TemperatureCore.setTemperature(player, current, (envtemp + 37)*.2f+lenvtemp*.8f);
            PacketHandler.send(PacketDistributor.PLAYER.with(() -> player), new FHBodyDataSyncPacket(player));
        }
    }

    /**
     * Perform temperature effect
     *
     * @param event fired every tick on player
     */
    @SubscribeEvent
    public static void regulateTemperature(PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == Phase.END
                && event.player instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.player;
            double calculatedTarget = TemperatureCore.getBodyTemperature(player);
            if (!(player.isCreative() || player.isSpectator())) {
                if (calculatedTarget > 1 || calculatedTarget < -1) {
                    if (!player.hasEffect(FHEffects.HYPERTHERMIA)
                            && !player.hasEffect(FHEffects.HYPOTHERMIA)) {
                        if (calculatedTarget > 1) { // too hot
                            if (calculatedTarget <= 2) {
                                player.addEffect(new MobEffectInstance(FHEffects.HYPERTHERMIA, 100, 0));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0)));
                            } else if (calculatedTarget <= 3) {
                                player.addEffect(new MobEffectInstance(FHEffects.HYPERTHERMIA, 100, 1));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.CONFUSION, 100, 2)));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0)));
                            } else if (calculatedTarget <= 5) {
                                player.addEffect(new MobEffectInstance(FHEffects.HYPERTHERMIA, 100, 2));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.CONFUSION, 100, 2)));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0)));
                            } else {
                                player.addEffect(
                                        new MobEffectInstance(FHEffects.HYPERTHERMIA, 100, (int) (calculatedTarget - 2)));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.CONFUSION, 100, 2)));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0)));
                            }
                        } else { // too cold
                            if (calculatedTarget >= -2) {
                                player.addEffect(new MobEffectInstance(FHEffects.HYPOTHERMIA, 100, 0));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0)));
                            } else if (calculatedTarget >= -3) {
                                player.addEffect(new MobEffectInstance(FHEffects.HYPOTHERMIA, 100, 1));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.CONFUSION, 100, 2)));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0)));
                            } else if (calculatedTarget >= -5) {
                                player.addEffect(new MobEffectInstance(FHEffects.HYPOTHERMIA, 100, 2));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.CONFUSION, 100, 2)));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0)));
                            } else {
                                player.addEffect(
                                        new MobEffectInstance(FHEffects.HYPOTHERMIA, 100, (int) (-calculatedTarget - 2)));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.CONFUSION, 100, 2)));
                                player.addEffect(FHUtils.noHeal(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0)));
                            }
                        }
                    }
                }
            }
        }
    }
}
