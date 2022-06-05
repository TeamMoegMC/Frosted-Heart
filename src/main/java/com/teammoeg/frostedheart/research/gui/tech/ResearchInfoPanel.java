package com.teammoeg.frostedheart.research.gui.tech;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.network.PacketHandler;
import com.teammoeg.frostedheart.network.research.FHEffectTriggerPacket;
import com.teammoeg.frostedheart.research.ResearchData;
import com.teammoeg.frostedheart.research.TeamResearchData;
import com.teammoeg.frostedheart.research.api.ResearchDataAPI;
import com.teammoeg.frostedheart.research.clues.Clue;
import com.teammoeg.frostedheart.research.effects.*;
import com.teammoeg.frostedheart.research.gui.FramedPanel;
import com.teammoeg.frostedheart.research.gui.TechIcons;
import com.teammoeg.frostedheart.research.gui.TechTextButton;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class ResearchInfoPanel extends Panel {

	ResearchDetailPanel detailPanel;
	List<Panel> panels = new ArrayList<>();

	public ResearchInfoPanel(ResearchDetailPanel panel) {
		super(panel);
		this.setOnlyInteractWithWidgetsInside(true);
		this.setOnlyRenderWidgetsInside(true);
		detailPanel = panel;
	}

	@Override
	public void addWidgets() {
		panels.clear();

		ResearchData researchData = detailPanel.research.getData();

		// exp materials

		FramedPanel prl = new FramedPanel(this, fp -> {
			int ioffset = 4;
			int xoffset = 4;
			for (IngredientWithSize ingredient : detailPanel.research.getRequiredItems()) {
				if (ingredient.getMatchingStacks().length != 0) {
					RequirementSlot button = new RequirementSlot(fp, ingredient.getMatchingStacks());

					button.setPosAndSize(xoffset, ioffset, 16, 16);
					fp.add(button);

					xoffset += 17;
					if (xoffset >= 121) {
						xoffset = 4;
						ioffset += 17;
					}
				}
			}
			ioffset += 23;

			// commit items button
			Button commitItems = new TechTextButton(fp, GuiUtils.translateGui("research.commit_material_and_start"),
					Icon.EMPTY) {
				@Override
				public void onClicked(MouseButton mouseButton) {

					refreshWidgets();
				}
			};

			commitItems.setPos(0, ioffset);
			ioffset += commitItems.height;
			fp.add(commitItems);
			fp.setWidth(width);

			fp.setHeight(ioffset);
		});
		prl.setTitle(GuiUtils.translateGui("research.requirements"));
		prl.setPos(0, 0);
		if (!researchData.canResearch())
			panels.add(prl);

		add(prl);

		FramedPanel ppl = new FramedPanel(this, fp -> {
			int offset = 2;
			int xoffset = 2;
			boolean hasItemRewards = false;
			boolean hasB = false;
			for (Effect effect : detailPanel.research.getEffects()) {
				if (!(effect instanceof EffectBuilding))
					continue;
				LEffectWidget button = new LEffectWidget(fp, effect);
				button.setPos(xoffset, offset);
				fp.add(button);
				xoffset += 32;
				if (xoffset >= 98) {
					offset += 32;
					xoffset = 2;
				}
				hasB = true;
			}
			if (hasB)
				offset += 42;
			hasB = false;
			xoffset = 4;
			for (Effect effect : detailPanel.research.getEffects()) {

				// item reward
				if (effect instanceof EffectItemReward) {
					hasItemRewards = true;

				}

				// building
				if (effect instanceof EffectBuilding) {
					continue;
				}
				EffectWidget button = new EffectWidget(fp, effect);
				button.setPos(xoffset, offset);
				fp.add(button);
				xoffset += 17;
				if (xoffset >= 121) {
					xoffset = 4;
					offset += 17;
				}
				hasB = true;
			}
			if (hasB)
				offset += 24;
			TeamResearchData data = TeamResearchData.getClientInstance();
			// TODO: remove || true after api works
			if (hasItemRewards && (data.getData(detailPanel.research).isCompleted() || true)) {
				Button claimRewards = new TechTextButton(fp, GuiUtils.translateGui("research.claim_rewards"),
						Icon.EMPTY) {
					@Override
					public void onClicked(MouseButton mouseButton) {
						PacketHandler.sendToServer(new FHEffectTriggerPacket(detailPanel.research));
						refreshWidgets();
					}
				};
				claimRewards.setPos(0, offset);
				fp.add(claimRewards);
				offset += claimRewards.height + 1;
			}
			fp.setWidth(width);
			fp.setHeight(offset);
		});
		ppl.setTitle(GuiUtils.translateGui("research.effects"));
		ppl.setPos(0, 0);
		add(ppl);
		panels.add(ppl);

		FramedPanel pcl = new FramedPanel(this, fp -> {
			int offset = 1;

			for (Clue clue : detailPanel.research.getClues()) {
				CluePanel cl=new CluePanel(fp,clue);
				cl.setWidth(width-5);
				cl.initWidgets();
				fp.add(cl);
				offset += cl.height + 1;

			}
			fp.setWidth(width);
			fp.setHeight(offset);
		});

		pcl.setTitle(GuiUtils.translateGui("research.clues"));
		pcl.setPos(0, 0);
		add(pcl);
		panels.add(pcl);
		if (researchData.canResearch())
			panels.add(prl);
	}

	@Override
	public void alignWidgets() {
		int offset = 0;
		for (Panel p : panels) {
			p.setPos(3, offset);
			offset += p.height + 2;
		}
		detailPanel.scrollInfo.setMaxValue(offset);
	}

	@Override
	public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
	}
}