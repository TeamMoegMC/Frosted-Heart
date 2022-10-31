/*
 * Copyright (c) 2021 TeamMoeg
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
 */

package com.teammoeg.frostedheart;

import java.util.ArrayList;
import java.util.List;

import com.teammoeg.frostedheart.content.decoration.RelicChestContainer;
import com.teammoeg.frostedheart.content.decoration.RelicChestTileEntity;
import com.teammoeg.frostedheart.content.generator.t1.T1GeneratorContainer;
import com.teammoeg.frostedheart.content.generator.t1.T1GeneratorTileEntity;
import com.teammoeg.frostedheart.content.generator.t2.T2GeneratorContainer;
import com.teammoeg.frostedheart.content.generator.t2.T2GeneratorTileEntity;
import com.teammoeg.frostedheart.content.incubator.HeatIncubatorTileEntity;
import com.teammoeg.frostedheart.content.incubator.IncubatorT1Container;
import com.teammoeg.frostedheart.content.incubator.IncubatorT2Container;
import com.teammoeg.frostedheart.content.incubator.IncubatorTileEntity;
import com.teammoeg.frostedheart.content.steamenergy.sauna.SaunaContainer;
import com.teammoeg.frostedheart.content.steamenergy.sauna.SaunaTileEntity;
import com.teammoeg.frostedheart.research.gui.drawdesk.DrawDeskContainer;
import com.teammoeg.frostedheart.research.machines.DrawingDeskTileEntity;

import blusunrize.immersiveengineering.common.gui.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class FHContent {

    public static List<Block> registeredFHBlocks = new ArrayList<>();
    public static List<Item> registeredFHItems = new ArrayList<>();
    public static List<Fluid> registeredFHFluids = new ArrayList<>();

    public static void registerContainers() {
        GuiHandler.register(T1GeneratorTileEntity.class, new ResourceLocation(FHMain.MODID, "generator"), T1GeneratorContainer::new);
        GuiHandler.register(T2GeneratorTileEntity.class, new ResourceLocation(FHMain.MODID, "generator_t2"), T2GeneratorContainer::new);
        GuiHandler.register(RelicChestTileEntity.class, new ResourceLocation(FHMain.MODID, "relic_chest"), RelicChestContainer::new);
        GuiHandler.register(DrawingDeskTileEntity.class, new ResourceLocation(FHMain.MODID, "draw_desk"), DrawDeskContainer::new);
        GuiHandler.register(SaunaTileEntity.class, new ResourceLocation(FHMain.MODID, "sauna_vent"), SaunaContainer::new);
        GuiHandler.register(IncubatorTileEntity.class, new ResourceLocation(FHMain.MODID, "incubator"), IncubatorT1Container::new);
        GuiHandler.register(HeatIncubatorTileEntity.class, new ResourceLocation(FHMain.MODID, "heat_incubator"), IncubatorT2Container::new);
    }
}
