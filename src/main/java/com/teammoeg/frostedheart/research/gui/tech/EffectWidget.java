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

package com.teammoeg.frostedheart.research.gui.tech;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.frostedheart.research.effects.Effect;
import com.teammoeg.frostedheart.research.gui.FHIcons.FHIcon;
import com.teammoeg.frostedheart.research.gui.TechIcons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class EffectWidget extends Widget {
    List<ITextComponent> tooltips;
    ITextComponent title;
    FHIcon icon;
    Effect e;

    public EffectWidget(Panel panel, Effect e) {
        super(panel);
        tooltips = e.getTooltip();
        title = e.getName();
        icon = e.getIcon();
        this.e = e;
        this.setSize(16, 16);
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        list.add(title);
        tooltips.forEach(list::add);
    }

    @Override
    public boolean mousePressed(MouseButton button) {
        if (isMouseOver()) {
            if (getWidgetType() != WidgetType.DISABLED) {
                //TODO edit effect
            }

            return true;
        }

        return false;
    }

    @Override
    public void draw(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        GuiHelper.setupDrawing();
        TechIcons.SLOT.draw(matrixStack, x - 4, y - 4, 24, 24);
        icon.draw(matrixStack, x, y, w, h);
        if (e.isGranted()) {
            matrixStack.push();
            matrixStack.translate(0, 0, 300);
            GuiHelper.setupDrawing();
            TechIcons.FIN.draw(matrixStack, x, y, w, h);
            matrixStack.pop();
        }
    }
}
