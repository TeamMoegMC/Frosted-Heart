/*
 * Copyright (c) 2022-2024 TeamMoeg
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

package com.teammoeg.frostedheart.events;

import com.teammoeg.frostedheart.FHConfig;
import com.teammoeg.frostedheart.FHItems;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.climate.ClimateData;
import com.teammoeg.frostedheart.climate.WorldClimate;
import com.teammoeg.frostedheart.network.climate.FHTemperatureDisplayPacket;
import com.teammoeg.frostedheart.util.FHUtils;

import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetTitlesPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FHMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {
	@SuppressWarnings("resource")
	public static void onRC(PlayerInteractEvent.RightClickItem rci) {
		if (!rci.getWorld().isClientSide
				&& rci.getItemStack().getItem().getRegistryName().getNamespace().equals("projecte")) {
			rci.setCancellationResult(InteractionResult.SUCCESS);
			rci.setCanceled(true);
			Level world = rci.getWorld();
			Player player = rci.getPlayer();
			BlockPos pos = rci.getPos();
			ServerLevel serverWorld = (ServerLevel) world;
			ServerPlayer serverPlayerEntity = (ServerPlayer) player;

			serverPlayerEntity.addEffect(
					new MobEffectInstance(MobEffects.BLINDNESS, (int) (100 * (world.random.nextDouble() + 0.5)), 3));
			serverPlayerEntity.addEffect(
					new MobEffectInstance(MobEffects.CONFUSION, (int) (1000 * (world.random.nextDouble() + 0.5)), 5));

			serverPlayerEntity.connection.send(
					new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.TITLE, GuiUtils.translateMessage("too_cold_to_transmute")));
			serverPlayerEntity.connection.send(
					new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.SUBTITLE, GuiUtils.translateMessage("magical_backslash")));

			double posX = pos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * 4.5D;
			double posY = pos.getY() + world.random.nextInt(3) - 1;
			double posZ = pos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * 4.5D;
			if (world.noCollision(EntityType.WITCH.getAABB(posX, posY, posZ))
					&& SpawnPlacements.checkSpawnRules(EntityType.WITCH, serverWorld, MobSpawnType.NATURAL,
							new BlockPos(posX, posY, posZ), world.getRandom())) {
				FHUtils.spawnMob(serverWorld, new BlockPos(posX, posY, posZ), new CompoundTag(),
						new ResourceLocation("minecraft", "witch"));
			}
		}
	}


	@SubscribeEvent
	public static void sendForecastMessages(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer) {
			ServerPlayer serverPlayer = (ServerPlayer) event.player;
			boolean configAllows = FHConfig.COMMON.enablesTemperatureForecast.get();
			boolean hasRadar = serverPlayer.inventory.contains(new ItemStack(FHItems.weatherRadar));
			boolean hasHelmet = serverPlayer.inventory.armor.get(3)
					.sameItemStackIgnoreDurability(new ItemStack(FHItems.weatherHelmet));
			if (configAllows && (hasRadar || hasHelmet)) {
				// Blizzard warning
				float thisHour = ClimateData.getTemp(serverPlayer.level);
				float nextHour = ClimateData.getFutureTemp(serverPlayer.level, 1);
				if (thisHour >= WorldClimate.BLIZZARD_TEMPERATURE) { // not in blizzard yet
					if (nextHour < WorldClimate.BLIZZARD_TEMPERATURE) {
						serverPlayer.displayClientMessage(GuiUtils.translateMessage("forecast.blizzard_warning")
								.withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD), true);
						// serverPlayer.connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE,
						// GuiUtils.translateMessage("forecast.blizzard_warning")));
					}
				} else { // in blizzard now
					if (nextHour >= WorldClimate.BLIZZARD_TEMPERATURE) {
						serverPlayer.displayClientMessage(GuiUtils.translateMessage("forecast.blizzard_retreating")
								.withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD), true);
						// serverPlayer.connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE,
						// GuiUtils.translateMessage("forecast.blizzard_retreating")));
					}
				}

				// Morning forecast wakeup time
				if (serverPlayer.level.getDayTime() % 24000 == 40) {
					float morningTemp = Math.round(ClimateData.getTemp(serverPlayer.level) * 10) / 10.0F;
					float noonTemp = Math.round(ClimateData.getFutureTemp(serverPlayer.level, 0, 6) * 10) / 10.0F;
					float nightTemp = Math.round(ClimateData.getFutureTemp(serverPlayer.level, 0, 12) * 10) / 10.0F;
					float midnightTemp = Math.round(ClimateData.getFutureTemp(serverPlayer.level, 0, 18) * 10) / 10.0F;
					float tomorrowMorningTemp = Math.round(ClimateData.getFutureTemp(serverPlayer.level, 1, 0) * 10)
							/ 10.0F;
					FHTemperatureDisplayPacket.sendStatus(serverPlayer,"forecast.morning",false,morningTemp, noonTemp,
							nightTemp, midnightTemp, tomorrowMorningTemp);
					boolean snow = morningTemp < WorldClimate.SNOW_TEMPERATURE
							|| noonTemp < WorldClimate.SNOW_TEMPERATURE || nightTemp < WorldClimate.SNOW_TEMPERATURE
							|| midnightTemp < WorldClimate.SNOW_TEMPERATURE
							|| tomorrowMorningTemp < WorldClimate.SNOW_TEMPERATURE;
					boolean blizzard = morningTemp < WorldClimate.BLIZZARD_TEMPERATURE
							|| noonTemp < WorldClimate.BLIZZARD_TEMPERATURE
							|| nightTemp < WorldClimate.BLIZZARD_TEMPERATURE
							|| midnightTemp < WorldClimate.BLIZZARD_TEMPERATURE
							|| tomorrowMorningTemp < WorldClimate.BLIZZARD_TEMPERATURE;
					if (blizzard)
						serverPlayer.displayClientMessage(GuiUtils.translateMessage("forecast.blizzard_today"), false);
					else if (snow)
						serverPlayer.displayClientMessage(GuiUtils.translateMessage("forecast.snow_today"), false);
					else
						serverPlayer.displayClientMessage(GuiUtils.translateMessage("forecast.clear_today"), false);
				}

				// Night forecast bedtime
				if (serverPlayer.level.getDayTime() % 24000 == 12542) {
					float nightTemp = Math.round(ClimateData.getTemp(serverPlayer.level) * 10) / 10.0F;
					float midnightTemp = Math.round(ClimateData.getFutureTemp(serverPlayer.level, 0, 6) * 10) / 10.0F;
					float tomorrowMorningTemp = Math.round(ClimateData.getFutureTemp(serverPlayer.level, 0, 12) * 10)
							/ 10.0F;
					float tomorrowNoonTemp = Math.round(ClimateData.getFutureTemp(serverPlayer.level, 0, 18) * 10)
							/ 10.0F;
					float tomorrowNightTemp = Math.round(ClimateData.getFutureTemp(serverPlayer.level, 1, 0) * 10)
							/ 10.0F;
					FHTemperatureDisplayPacket.sendStatus(serverPlayer,"forecast.night", false, nightTemp, midnightTemp,
							tomorrowMorningTemp, tomorrowNoonTemp, tomorrowNightTemp);
					boolean snow = nightTemp < WorldClimate.SNOW_TEMPERATURE
							|| midnightTemp < WorldClimate.SNOW_TEMPERATURE
							|| tomorrowMorningTemp < WorldClimate.SNOW_TEMPERATURE
							|| tomorrowNoonTemp < WorldClimate.SNOW_TEMPERATURE
							|| tomorrowNightTemp < WorldClimate.SNOW_TEMPERATURE;
					boolean blizzard = nightTemp < WorldClimate.BLIZZARD_TEMPERATURE
							|| midnightTemp < WorldClimate.BLIZZARD_TEMPERATURE
							|| tomorrowMorningTemp < WorldClimate.BLIZZARD_TEMPERATURE
							|| tomorrowNoonTemp < WorldClimate.BLIZZARD_TEMPERATURE
							|| tomorrowNightTemp < WorldClimate.BLIZZARD_TEMPERATURE;
					if (blizzard)
						serverPlayer.displayClientMessage(GuiUtils.translateMessage("forecast.blizzard_tomorrow"), false);
					else if (snow)
						serverPlayer.displayClientMessage(GuiUtils.translateMessage("forecast.snow_tomorrow"), false);
					else
						serverPlayer.displayClientMessage(GuiUtils.translateMessage("forecast.clear_tomorrow"), false);
				}
			}
		}
	}
}
