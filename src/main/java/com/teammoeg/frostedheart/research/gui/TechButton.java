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

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.util.text.ITextComponent;

public abstract class TechButton extends Button {

    public TechButton(Panel panel) {
        super(panel);
    }

    public TechButton(Panel panel, Icon i) {
        super(panel);
        super.setIcon(i);
    }

    public TechButton(Panel panel, ITextComponent t, Icon i) {
        super(panel, t, i);
    }

    public boolean hasIcon() {
        return icon != null && !icon.isEmpty();
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        GuiHelper.setupDrawing();

        TechIcons.drawTexturedRect(matrixStack, x, y, w, h, isMouseOver());

        if (hasIcon()) {
            drawIcon(matrixStack, theme, x + (w - 16) / 2, y + (h - 16) / 2, 16, 16);
        }
    }

}
