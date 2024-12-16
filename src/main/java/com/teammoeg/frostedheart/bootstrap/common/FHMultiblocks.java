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

package com.teammoeg.frostedheart.bootstrap.common;

import com.teammoeg.frostedheart.content.climate.heatdevice.generator.t1.T1GeneratorLogic;
import com.teammoeg.frostedheart.content.climate.heatdevice.generator.t1.T1GeneratorMultiblock;
import com.teammoeg.frostedheart.content.climate.heatdevice.generator.t1.T1GeneratorState;
import com.teammoeg.frostedheart.content.climate.heatdevice.generator.t2.T2GeneratorLogic;
import com.teammoeg.frostedheart.content.climate.heatdevice.generator.t2.T2GeneratorMultiblock;
import com.teammoeg.frostedheart.content.climate.heatdevice.generator.t2.T2GeneratorState;
import com.teammoeg.frostedheart.content.climate.heatdevice.radiator.RadiatorLogic;
import com.teammoeg.frostedheart.content.climate.heatdevice.radiator.RadiatorMultiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.teammoeg.frostedheart.content.climate.heatdevice.radiator.RadiatorState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class FHMultiblocks {
	public static class Multiblock{
		public static IETemplateMultiblock GENERATOR_T1 = new T1GeneratorMultiblock();
		public static IETemplateMultiblock GENERATOR_T2 = new T2GeneratorMultiblock();
		public static IETemplateMultiblock RADIATOR = new RadiatorMultiblock();
		public static void init() {
			MultiblockHandler.registerMultiblock(GENERATOR_T1);
			MultiblockHandler.registerMultiblock(RADIATOR);
			MultiblockHandler.registerMultiblock(GENERATOR_T2);
		}

	}
	public static class Logic{
		public static final MultiblockRegistration<T1GeneratorState> GENERATOR_T1 = stone(new T1GeneratorLogic(), "generator_t1",false)
			.structure(() -> FHMultiblocks.Multiblock.GENERATOR_T1)
			.notMirrored()
			.component(FHMenuTypes.GENERATOR_T1.createComponent())
			.build();
		public static final MultiblockRegistration<T2GeneratorState> GENERATOR_T2 = metal(new T2GeneratorLogic(), "generator_t2")
			.structure(() -> FHMultiblocks.Multiblock.GENERATOR_T1)
			.notMirrored()
			.component(FHMenuTypes.GENERATOR_T2.createComponent())
			.build();
		public static final MultiblockRegistration<RadiatorState> RADIATOR = metal(new RadiatorLogic(), "radiator")
			.structure(() -> FHMultiblocks.Multiblock.RADIATOR)
			.notMirrored()
			.build();
		
		
		private static <S extends IMultiblockState> IEMultiblockBuilder<S> stone(IMultiblockLogic<S> logic, String name, boolean solid) {
			Properties properties = Properties.of()
				.mapColor(MapColor.STONE)
				.instrument(NoteBlockInstrument.BASEDRUM)
				.strength(2, 20);
			if (!solid)
				properties.noOcclusion();
			return new IEMultiblockBuilder<>(logic, name)
				.notMirrored()
				.customBlock(
					FHBlocks.BLOCKS, FHItems.ITEMS,
					r -> new NonMirrorableWithActiveBlock<>(properties, r),
					MultiblockItem::new)
				.defaultBEs(FHBlockEntityTypes.REGISTER);
		}

		private static <S extends IMultiblockState> IEMultiblockBuilder<S> metal(IMultiblockLogic<S> logic, String name) {
			return new IEMultiblockBuilder<>(logic, name)
				.defaultBEs(FHBlockEntityTypes.REGISTER)
				.customBlock(
					FHBlocks.BLOCKS, FHItems.ITEMS,
					r -> new NonMirrorableWithActiveBlock<>(IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get(), r),
					MultiblockItem::new);
		}

	}


}