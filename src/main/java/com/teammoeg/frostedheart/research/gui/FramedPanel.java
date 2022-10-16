/*
 * Copyright (c) 2022 TeamMoeg
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

package com.teammoeg.frostedheart.research.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;

public class FramedPanel extends Panel {
    ITextComponent title;
    Consumer<Panel> addWidgets;

    public FramedPanel(Panel panel, Consumer<Panel> addWidgets) {
        super(panel);
        this.addWidgets = addWidgets;
    }

    @Override
    public void addWidgets() {
        if (addWidgets != null)
            addWidgets.accept(this);
        for (Widget w : widgets) {
            w.setPos(w.posX + 5, w.posY + 12);
            w.setWidth(Math.min(w.width, width - 12));
        }
    }

    @Override
    public void alignWidgets() {
        setHeight(height + 12);

    }

    public void setTitle(ITextComponent title) {
        this.title = title;
    }

    @Override
    public void draw(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        theme.drawString(matrixStack, title, x, y, TechIcons.text, 0);
        TechIcons.HLINE_L.draw(matrixStack, x, y + 8, 80, 3);
        TechIcons.VLINE.draw(matrixStack, x + 2, y + 9, 1, this.height - 16);
        super.draw(matrixStack, theme, x + 5, y + 12, w - 5, h - 12);
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {

    }

}
