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

package com.teammoeg.frostedheart.client.renderer;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.frostedheart.FHBlocks;
import com.teammoeg.frostedheart.research.machines.MechCalcTileEntity;

import blusunrize.immersiveengineering.api.IEProperties.IEObjState;
import blusunrize.immersiveengineering.api.IEProperties.Model;
import blusunrize.immersiveengineering.api.IEProperties.VisibilityList;
import blusunrize.immersiveengineering.api.utils.client.SinglePropertyModelData;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;

public class MechCalcRenderer extends TileEntityRenderer<MechCalcTileEntity> {
    public static DynamicModel<Direction> MODEL;
    public static IEObjState drum = new IEObjState(VisibilityList.show("c7"));
    public static IEObjState register = new IEObjState(VisibilityList.show("c6"));

    public MechCalcRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(MechCalcTileEntity te, float partialTicks, MatrixStack matrixStack,
                       IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockPos blockPos = te.getBlockPos();
        BlockState state = te.getLevel().getBlockState(blockPos);
        if (state.getBlock() != FHBlocks.mech_calc)
            return;
        matrixStack.pushPose();
        Direction rd = te.getDirection().getClockWise();
        double forward = (te.process / 1067) / 16d;
        matrixStack.translate(rd.getStepX() * forward, 0, rd.getStepZ() * forward);
        List<BakedQuad> quads = MODEL.getNullQuads(te.getDirection(), state, new SinglePropertyModelData<>(register, Model.IE_OBJ_STATE));
        RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.solid()), matrixStack, combinedLightIn, combinedOverlayIn);
        matrixStack.popPose();
        Direction fw = te.getDirection();
        matrixStack.pushPose();
        int deg = 0, dx = 0, dz = 0;
        switch (fw) {
            case SOUTH:
                deg = 180;
                dx = -1;
                dz = -1;
                break;
            case EAST:
                deg = -90;
                dz = -1;
                break;
            case WEST:
                deg = 90;
                dx = -1;
                break;
        }
        matrixStack.mulPose(new Quaternion(0, deg, 0, true));
        matrixStack.translate(dx, 0, dz);

        matrixStack.translate(0, 13.75 / 16, 7d / 16);
        float rotn = ((te.process) % 160) * 2.25f;
        Quaternion rot = new Quaternion(rotn, 0f, 0f, true);
        matrixStack.mulPose(rot);

        matrixStack.translate(0, -1.5 / 16, -1.5 / 16);
        quads = MODEL.getNullQuads(Direction.NORTH, state, new SinglePropertyModelData<>(drum, Model.IE_OBJ_STATE));
        RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.solid()), matrixStack, combinedLightIn, combinedOverlayIn);
        matrixStack.popPose();
    }

}
