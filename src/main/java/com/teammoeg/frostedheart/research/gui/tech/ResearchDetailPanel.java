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

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.frostedheart.research.gui.FHIcons.FHIcon;
import com.teammoeg.frostedheart.research.research.Research;
import com.teammoeg.frostedheart.research.gui.TechIcons;
import com.teammoeg.frostedheart.research.gui.TechScrollBar;

import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.util.text.ITextComponent;

public class ResearchDetailPanel extends Panel {
    Research research;
    FHIcon icon;
    ResearchDashboardPanel dashboardPanel;
    ResearchInfoPanel infoPanel;
    DescPanel descPanel;
    ResearchPanel researchScreen;

    public PanelScrollBar scrollInfo;

    public ResearchDetailPanel(ResearchPanel panel) {
        super(panel);
        this.setOnlyInteractWithWidgetsInside(true);
        this.setOnlyRenderWidgetsInside(true);
        descPanel = new DescPanel(this);
        infoPanel = new ResearchInfoPanel(this);
        scrollInfo = new TechScrollBar(this, infoPanel);
        dashboardPanel = new ResearchDashboardPanel(this);
        researchScreen = panel;

    }

    @Override
    public void addWidgets() {
        if (research == null)
            return;
        icon = research.getIcon();

        add(dashboardPanel);
        dashboardPanel.setPosAndSize(4, 11, 140, 51);

        add(descPanel);
        descPanel.setPosAndSize(8, 64, 132, 100);

        add(infoPanel);
        infoPanel.setPosAndSize(150, 15, 135, 151);
        Button closePanel = new Button(this) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                close();
            }

            @Override
            public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
            }
        };
        closePanel.setPosAndSize(284, 7, 9, 8);
        add(closePanel);

        scrollInfo.setPosAndSize(285, 18, 8, 146);
        //scrollInfo.setMaxValue(height);
        add(scrollInfo);
        // already committed items
        //ResearchData rd = research.getData();
        TextField status = new TextField(this);
        status.setMaxWidth(135);
		/*if (research.getData().isInProgress()) {
			status.setText(GuiUtils.translateGui("research.in_progress").mergeStyle(TextFormatting.BOLD)
					.mergeStyle(TextFormatting.BLUE));
		} else if (rd.canResearch()) {
			status.setText(GuiUtils.translateGui("research.can_research").mergeStyle(TextFormatting.BOLD)
					.mergeStyle(TextFormatting.GREEN));

		}*/
        status.setPos(0, 6);
        add(status);
    }

    @Override
    public void alignWidgets() {
    }

    @Override
    public void draw(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        if (research == null) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0, 0, 500);
        super.draw(matrixStack, theme, x, y, w, h);
        matrixStack.pop();
    }

    public void open(Research r) {
        this.research = r;
        this.refreshWidgets();
        researchScreen.setModal(this);
        ;
        //researchScreen.refreshWidgets();

    }

    public void close() {
        this.research = null;
        this.refreshWidgets();
        researchScreen.closeModal(this);
        //researchScreen.refreshWidgets();
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        list.zOffset = 950;
        list.zOffsetItemTooltip = 500;
        super.addMouseOverText(list);
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        // drawBackground(matrixStack, theme, x, y, w, h);
        // theme.drawGui(matrixStack, x, y, w, h,WidgetType.NORMAL);
        TechIcons.DIALOG.draw(matrixStack, x, y, w, h);
    }

    public static class DescPanel extends Panel {
        ResearchDetailPanel detailPanel;

        public DescPanel(ResearchDetailPanel panel) {
            super(panel);
            this.setOnlyInteractWithWidgetsInside(true);
            this.setOnlyRenderWidgetsInside(true);
            detailPanel = panel;
        }

        @Override
        public void addWidgets() {
            List<ITextComponent> itxs = detailPanel.research.getDesc();
            int offset = 0;
            for (ITextComponent itx : itxs) {
                TextField desc = new TextField(this);
                add(desc);
                desc.setMaxWidth(width);
                desc.setPosAndSize(0, offset, width, height);
                desc.setText(itx);
                desc.setColor(TechIcons.text);
                offset += desc.height;
            }
            this.setHeight(offset);
        }

        @Override
        public void alignWidgets() {

        }
    }

    @Override
    public boolean isEnabled() {
        return researchScreen.canEnable(this) && research != null;
    }
}
