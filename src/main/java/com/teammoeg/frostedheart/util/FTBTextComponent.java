/*
 * Copyright (c) 2024 TeamMoeg
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

package com.teammoeg.frostedheart.util;

import dev.ftb.mods.ftblibrary.util.ClientTextComponentUtils;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.BaseComponent;

public class FTBTextComponent extends BaseComponent {
	String comp;

	public FTBTextComponent(String comp) {
		super();
		this.comp = comp;
	}
	MutableComponent it;
	protected MutableComponent intern() {
		if(it==null)
			it=(MutableComponent) ClientTextComponentUtils.parse(comp);
		return it;
	}
	@Override
	public BaseComponent plainCopy() {
		return new FTBTextComponent(comp);
	}


	@Override
	public FormattedCharSequence getVisualOrderText() {
		return intern().getVisualOrderText();
	}



}
