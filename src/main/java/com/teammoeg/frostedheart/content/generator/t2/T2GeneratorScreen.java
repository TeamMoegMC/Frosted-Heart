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

package com.teammoeg.frostedheart.content.generator.t2;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.network.PacketHandler;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.elements.GuiButtonBoolean;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import blusunrize.immersiveengineering.common.network.MessageTileSync;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class T2GeneratorScreen extends IEContainerScreen<T2GeneratorContainer> {
    private static final ResourceLocation TEXTURE = GuiUtils.makeTextureLocation("generator_t2");
    private T2GeneratorTileEntity tile;

    public T2GeneratorScreen(T2GeneratorContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tile = container.tile;
        clearIntArray(tile.guiData);
    }

    @Override
    public void init() {
        super.init();
        this.buttons.clear();
        this.addButton(new GuiButtonBoolean(leftPos + 56, topPos + 35, 19, 10, "", tile.isWorking(), TEXTURE, 0, 245, 0,
                btn -> {
                    CompoundTag tag = new CompoundTag();
                    tile.setWorking(!btn.getState());
                    tag.putBoolean("isWorking", tile.isWorking());
                    PacketHandler.sendToServer(new MessageTileSync(tile.master(), tag));
                    fullInit();
                }));
        this.addButton(new GuiButtonBoolean(leftPos + 101, topPos + 35, 19, 10, "", tile.isOverdrive(), TEXTURE, 0, 245, 0,
                btn -> {
                    CompoundTag tag = new CompoundTag();
                    tile.setOverdrive(!btn.getState());
                    tag.putBoolean("isOverdrive", tile.isOverdrive());
                    PacketHandler.sendToServer(new MessageTileSync(tile.master(), tag));
                    fullInit();
                }));
    }

    @Override
    public void render(PoseStack transform, int mouseX, int mouseY, float partial) {
        super.render(transform, mouseX, mouseY, partial);
        List<Component> tooltip = new ArrayList<>();
        GuiHelper.handleGuiTank(transform, tile.tank, leftPos + 30, topPos + 16, 16, 47, 177, 86, 20, 51, mouseX, mouseY, TEXTURE, tooltip);

        if (isMouseIn(mouseX, mouseY, 57, 36, 19, 10)) {
            if (tile.isWorking()) {
                tooltip.add(GuiUtils.translateGui("generator.mode.off"));
            } else {
                tooltip.add(GuiUtils.translateGui("generator.mode.on"));
            }
        }

        if (isMouseIn(mouseX, mouseY, 102, 36, 19, 10)) {
            if (tile.isOverdrive()) {
                tooltip.add(GuiUtils.translateGui("generator.overdrive.off"));
            } else {
                tooltip.add(GuiUtils.translateGui("generator.overdrive.on"));
            }
        }

        if (isMouseIn(mouseX, mouseY, 12, 13, 2, 54)) {
              tooltip.add(GuiUtils.translateGui("generator.temperature.level").append(GuiUtils.toTemperatureDeltaIntString(tile.getIsActive()?tile.getActualTemp():0)));
           
        }

        if (isMouseIn(mouseX, mouseY, 161, 13, 2, 54)) {
                tooltip.add(GuiUtils.translateGui("generator.range.level").append(Integer.toString(tile.getIsActive()?tile.getActualRange():0)));
        }

        if (isMouseIn(mouseX, mouseY, 146, 13, 2, 54)) {
            tooltip.add(GuiUtils.translateGui("generator.power.level").append(Integer.toString((int) tile.power)));
        }

        if (!tooltip.isEmpty()) {
            net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(transform, tooltip, mouseX, mouseY, width, height, -1, font);
        }
    }

    @Override
    protected void renderBg(PoseStack transform, float partial, int x, int y) {
        ClientUtils.bindTexture(TEXTURE);
        this.blit(transform, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        GuiHelper.handleGuiTank(transform, tile.tank, leftPos + 30, topPos + 16, 16, 47, 177, 86, 20, 51, x, y, TEXTURE, null);

        // recipe progress icon
        if (tile.processMax > 0 && tile.process > 0) {
            int h = (int) (12 * (tile.process / (float) tile.processMax));
            this.blit(transform, leftPos + 84, topPos + 47 - h, 179, 1 + 12 - h, 9, h);
        }

        // work button
        if (tile.isWorking()) {
            this.blit(transform, leftPos + 56, topPos + 35, 232, 1, 19, 10);
        }

        // overdrive button
        if (tile.isOverdrive()) {
            this.blit(transform, leftPos + 101, topPos + 35, 232, 12, 19, 10);
        }

        float tempLevel = tile.getTemperatureLevel();
        float rangeLevel = tile.getRangeLevel();
        float powerRatio = tile.power / tile.getMaxPower(); // (0, 1)

        // temperature bar (182, 30)
        if (tile.getIsActive()) {
            int offset = (int) ((4 - tempLevel) * 14);
            int bar = (int) ((tempLevel - 1) * 14);
            this.blit(transform, leftPos + 12, topPos + 13 + offset, 181, 30, 2, 12 + bar);
        }

        // range bar
        if (tile.getIsActive()) {
            int offset = (int) ((4 - rangeLevel) * 14);
            int bar = (int) ((rangeLevel - 1) * 14);
            this.blit(transform, leftPos + 161, topPos + 13 + offset, 181, 30, 2, 12 + bar);
        }

        // power
        int offset = (int) ((1 - powerRatio) * 56);
        int bar = (int) (powerRatio * 56);
        this.blit(transform, leftPos + 146, topPos + offset + 13, 181, 30, 2, bar);
    }

    @Override
    public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= leftPos + x && mouseY >= topPos + y
                && mouseX < leftPos + x + w && mouseY < topPos + y + h;
    }
}
