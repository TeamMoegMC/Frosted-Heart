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

package com.teammoeg.frostedheart.client.model;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.client.model.data.IModelData;

public class LiningModel implements BakedModel {

    private BakedModel baseArmorModel;
    private LiningItemOverrideList overrideList;

    public LiningModel(BakedModel i_baseChessboardModel) {
        baseArmorModel = i_baseChessboardModel;
        overrideList = new LiningItemOverrideList();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return baseArmorModel.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return baseArmorModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return baseArmorModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return baseArmorModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return baseArmorModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return baseArmorModel.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return baseArmorModel.getTransforms();
    }

    // This is a forge extension that is expected for blocks only.
    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        throw new AssertionError("LiningModel::getQuads(IModelData) should never be called");
    }

    // This is a forge extension that is expected for blocks only.
    @Override
    @Nonnull
    public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        throw new AssertionError("LiningModel::getModelData should never be called");
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrideList;
    }
}
