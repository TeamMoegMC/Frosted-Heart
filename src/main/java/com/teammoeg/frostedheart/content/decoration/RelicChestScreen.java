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

package com.teammoeg.frostedheart.content.decoration;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.frostedheart.client.util.GuiUtils;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class RelicChestScreen extends IEContainerScreen<RelicChestContainer> {
    private static final ResourceLocation TEXTURE = GuiUtils.makeTextureLocation("relic_chest");

    public RelicChestScreen(RelicChestContainer inventorySlotsIn, Inventory inv, Component title) {
        super(inventorySlotsIn, inv, title);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        ClientUtils.bindTexture(TEXTURE);
        this.blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
