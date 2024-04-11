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

package com.teammoeg.frostedheart.content.research.gui;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.CursorType;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.util.TooltipList;

public abstract class TristateButton extends Button {
    boolean enabled;
    Icon normal, over, locked;
    Consumer<TooltipList> tooltips;

    public TristateButton(Panel panel, Icon normal, Icon over, Icon locked) {
        super(panel);
        this.normal = normal;
        this.over = over;
        this.locked = locked;
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        super.addMouseOverText(list);
        if (tooltips != null)
            tooltips.accept(list);
    }

    @Override
    public void draw(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        if (getEnabled()) {
            if (super.isMouseOver())
                over.draw(matrixStack, x, y, w, h);
            else
                normal.draw(matrixStack, x, y, w, h);
        } else
            locked.draw(matrixStack, x, y, w, h);
    }

    @Override
    public CursorType getCursor() {
        if (enabled)
            return CursorType.HAND;
        return CursorType.ARROW;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public Icon getLocked() {
        return locked;
    }

    public Icon getNormal() {
        return normal;
    }

    public Icon getOver() {
        return over;
    }

    public Consumer<TooltipList> getTooltips() {
        return tooltips;
    }

    public void resetTooltips() {
        this.tooltips = null;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setLocked(Icon locked) {
        this.locked = locked;
    }

    public void setNormal(Icon normal) {
        this.normal = normal;
    }

    public void setOver(Icon over) {
        this.over = over;
    }

    public void setTooltips(Consumer<TooltipList> tooltips) {
        this.tooltips = tooltips;
    }

}
