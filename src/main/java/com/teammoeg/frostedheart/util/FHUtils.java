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

package com.teammoeg.frostedheart.util;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.ToIntFunction;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableList;
import com.teammoeg.frostedheart.climate.WorldClimate;
import com.teammoeg.frostedheart.climate.chunkdata.ChunkData;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.NBTIngredient;

public class FHUtils {
    private static class NBTIngredientAccess extends NBTIngredient {
        public NBTIngredientAccess(ItemStack stack) {
            super(stack);
        }
    }

    public static <T> T notNull() {
        return null;
    }
    public static void applyEffectTo(MobEffectInstance effectinstance,Player playerentity) {
    	if (effectinstance.getEffect().isInstantenous()) {
            effectinstance.getEffect().applyInstantenousEffect(playerentity, playerentity, playerentity, effectinstance.getAmplifier(), 1.0D);
         } else {
        	 playerentity.addEffect(new MobEffectInstance(effectinstance));
         }
    }
    public static Ingredient createIngredient(ItemStack is) {
        if (is.hasTag()) return new NBTIngredientAccess(is);
        return Ingredient.of(is);
    }

    public static IngredientWithSize createIngredientWithSize(ResourceLocation tag, int count) {
        return new IngredientWithSize(Ingredient.of(ItemTags.getAllTags().getTag(tag)), count);
    }

    public static IngredientWithSize createIngredientWithSize(ItemStack is) {
        if (is.hasTag()) return new IngredientWithSize(new NBTIngredientAccess(is), is.getCount());
        return new IngredientWithSize(Ingredient.of(is), is.getCount());
    }

    public static Ingredient createIngredient(ResourceLocation tag) {
        return Ingredient.of(ItemTags.getAllTags().getTag(tag));
    }

    public static void giveItem(Player pe, ItemStack is) {
        if (!pe.addItem(is))
            pe.level.addFreshEntity(new ItemEntity(pe.level, pe.blockPosition().getX(), pe.blockPosition().getY(), pe.blockPosition().getZ(), is));
    }

    public static void registerSimpleCapability(Class<?> clazz) {
        CapabilityManager.INSTANCE.register(clazz, new NoopStorage<>(), () -> {
            throw new UnsupportedOperationException("Creating default instances is not supported. Why would you ever do this");
        });
    }

    public static ToIntFunction<BlockState> getLightValueLit(int lightValue) {
        return (state) -> {
            return state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
        };
    }

    public static boolean isRainingAt(BlockPos pos, Level world) {
        if (!world.isRaining()) {
            return false;
        } else if (!world.canSeeSky(pos)) {
            return false;
        } else if (world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        } else {
            return true;
        }
    }

    public static void canBigTreeGenerate(Level w, BlockPos p, Random r, CallbackInfoReturnable<Boolean> cr) {
        if (!canBigTreeGenerate(w, p, r))
            cr.setReturnValue(false);
    }

    public static boolean canTreeGenerate(Level w, BlockPos p, Random r,int chance) {
        return r.nextInt(chance) == 0;

    }
    public static boolean canTreeGrow(Level w, BlockPos p, Random r) {
        float temp=ChunkData.getTemperature(w, p);
        if(temp<=-6)
        	return false;
        if(temp>WorldClimate.VANILLA_PLANT_GROW_TEMPERATURE_MAX)
        	return false;
        if(temp>0)
        	return true;
    	return r.nextInt(Math.max(1,Mth.ceil(-temp/2))) == 0;
    }
    public static boolean canNetherTreeGrow(BlockGetter w, BlockPos p) {
    	if(!(w instanceof LevelAccessor)) {
    		return false;
    	}
        float temp=ChunkData.getTemperature((LevelAccessor) w, p);
        if(temp<=300)
        	return false;
        if(temp>300+WorldClimate.VANILLA_PLANT_GROW_TEMPERATURE_MAX)
        	return false;
        return true;
    }
    public static boolean canBigTreeGenerate(Level w, BlockPos p, Random r) {

        return canTreeGenerate(w,p,r,7);

    }

    public static void spawnMob(ServerLevel world, BlockPos blockpos, CompoundTag nbt, ResourceLocation type) {
        if (Level.isInSpawnableBounds(blockpos)) {
            CompoundTag compoundnbt = nbt.copy();
            compoundnbt.putString("id", type.toString());
            Entity entity = EntityType.loadEntityRecursive(compoundnbt, world, (p_218914_1_) -> {
                p_218914_1_.moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ(), p_218914_1_.yRot, p_218914_1_.xRot);
                return p_218914_1_;
            });
            if (entity != null) {
                if (entity instanceof Mob) {
                    ((Mob) entity).finalizeSpawn(world, world.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.NATURAL, (SpawnGroupData) null, (CompoundTag) null);
                }
                if (!world.tryAddFreshEntityWithPassengers(entity)) {
                    return;
                }
            }
        }
    }

    public static int getEnchantmentLevel(Enchantment enchID, CompoundTag tags) {
        ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(enchID);
        ListTag listnbt = tags.getList("Enchantments", 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundTag compoundnbt = listnbt.getCompound(i);
            ResourceLocation resourcelocation1 = ResourceLocation.tryParse(compoundnbt.getString("id"));
            if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
                return Mth.clamp(compoundnbt.getInt("lvl"), 0, 255);
            }
        }

        return 0;
    }

    public static MobEffectInstance noHeal(MobEffectInstance ei) {
        ei.setCurativeItems(ImmutableList.of());
        return ei;
    }

    public static boolean canGrassSurvive(LevelReader world, BlockPos pos) {
        float t = ChunkData.getTemperature(world, pos);
        return t >= WorldClimate.HEMP_GROW_TEMPERATURE && t <= WorldClimate.VANILLA_PLANT_GROW_TEMPERATURE_MAX;
    }
    public static <O,T> Optional<T> ofMap(Map<O,T> map,O key){
    	return Optional.ofNullable(map.get(key));
    }
}
