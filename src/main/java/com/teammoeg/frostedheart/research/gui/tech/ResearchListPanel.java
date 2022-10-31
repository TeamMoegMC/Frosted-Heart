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
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.research.FHResearch;
import com.teammoeg.frostedheart.research.Research;
import com.teammoeg.frostedheart.research.gui.RTextField;
import com.teammoeg.frostedheart.research.gui.TechIcons;
import com.teammoeg.frostedheart.research.gui.TechScrollBar;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;

public class ResearchListPanel extends Panel {

    public static final int RESEARCH_HEIGHT = 18;
    public static final int RES_PANEL_WIDTH = 80;

    public ResearchPanel researchScreen;
    public PanelScrollBar scroll;
    public ResearchList rl;

    public ResearchListPanel(ResearchPanel panel) {
        super(panel);
        researchScreen = panel;
    }

    public static class ResearchList extends Panel {
        public ResearchPanel researchScreen;

        public ResearchList(ResearchListPanel panel) {
            super(panel);
            researchScreen = panel.researchScreen;
            this.setWidth(103);
            this.setHeight(118);
        }

        @Override
        public void addWidgets() {
            int offset = 0;

            for (Research r : FHResearch.getResearchesForRender(this.researchScreen.selectedCategory, FHResearch.editor)) {
                ResearchButton button = new ResearchButton(this, r);
                add(button);
                button.setPos(4, offset);
                offset += 18;
            }
            //this.setHeight(offset+1);
            researchScreen.researchListPanel.scroll.setMaxValue(offset + 1);
        }

        @Override
        public void alignWidgets() {
        }

    }

    public static class ResearchButton extends Button {

        Research research;
        ResearchList listPanel;
        RTextField tf;

        public ResearchButton(ResearchList panel, Research research) {
            super(panel, research.getName(), research.getIcon());
            this.research = research;
            this.listPanel = panel;
            setSize(101, RESEARCH_HEIGHT);
            tf = new RTextField(panel).setMaxLine(1).setMaxWidth(84).setText(research.getName());
            if(research.hasUnclaimedReward())
            	tf.setColor(Color4I.rgb(0x5555ff));
            else if (research.isCompleted()) {
                tf.setColor(Color4I.rgb(0x229000));
            } else if (!research.isUnlocked()) {
                tf.setColor(TechIcons.text_red);
            } else
                tf.setColor(TechIcons.text);
            lastupdate=System.currentTimeMillis()/1000;
        }

        @Override
        public void onClicked(MouseButton mouseButton) {
        	
            listPanel.researchScreen.selectResearch(research);
        }
        long lastupdate;
        @Override
        public void draw(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
            //GuiHelper.setupDrawing();
            this.drawIcon(matrixStack, theme, x + 1, y + 1, 16, 16);
            long secs=System.currentTimeMillis()/1000;
            if(lastupdate!=secs) {
            	lastupdate=secs;
	            if(research.hasUnclaimedReward()) {
	            	if(secs%2==0) {
	            		tf.setText(GuiUtils.translateGui("research.unclaimed"));
	            	}else
	            		tf.setText(research.getName());
	            	tf.setColor(Color4I.rgb(0x5555ff));
	            }else if (research.isCompleted()) {
	                tf.setColor(Color4I.rgb(0x229000));
	            } else if (!research.isUnlocked()) {
	                tf.setColor(TechIcons.text_red);
	            } else
	                tf.setColor(TechIcons.text);
            }
            tf.draw(matrixStack, theme, x + 18, y + 6, 81, tf.height);
            if (listPanel.researchScreen.selectedResearch == this.research)
                TechIcons.SELECTED.draw(matrixStack, x - 4, y + 7, 4, 4);
            TechIcons.HLINE.draw(matrixStack, x, y + 17, 99, 1);
        }
    }

    @Override
    public void addWidgets() {
        rl = new ResearchList(this);
        scroll = new TechScrollBar(this, rl);
        add(rl);
        add(scroll);
        scroll.setX(106);
        scroll.setSize(8, height);

    }

    @Override
    public void alignWidgets() {

    }

    @Override
    public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        //theme.drawPanelBackground(matrixStack, x, y, w, h);
    }

    @Override
    public void draw(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        super.draw(matrixStack, theme, x, y, w, h);
    }

    @Override
    public boolean isEnabled() {
        return researchScreen.canEnable(this);
    }
}

