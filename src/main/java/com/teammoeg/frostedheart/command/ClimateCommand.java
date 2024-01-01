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

package com.teammoeg.frostedheart.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.climate.ClimateData;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;

public class ClimateCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> get = Commands.literal("get")
                .executes((ct) -> {
                	try {
                           ct.getSource().sendSuccess(new TextComponent(String.valueOf(ClimateData.get(ct.getSource().getLevel()))),true);
                	}catch(Exception ex) {
                		ex.printStackTrace();
                	}
                            return Command.SINGLE_SUCCESS;
                        });
        LiteralArgumentBuilder<CommandSourceStack> rebuild = Commands.literal("rebuild")
                .executes((ct) -> {
                	
                    ClimateData.get(ct.getSource().getLevel()).resetTempEvent(ct.getSource().getLevel());
                    ct.getSource().sendSuccess(new TextComponent("Succeed!").withStyle(ChatFormatting.GREEN), false);
                    return Command.SINGLE_SUCCESS;
        });
        LiteralArgumentBuilder<CommandSourceStack> init = Commands.literal("init")
        .executes((ct) -> {
            ClimateData.get(ct.getSource().getLevel()).addInitTempEvent(ct.getSource().getLevel());
            ct.getSource().sendSuccess(new TextComponent("Succeed!").withStyle(ChatFormatting.GREEN), false);
            return Command.SINGLE_SUCCESS;
        });
        LiteralArgumentBuilder<CommandSourceStack> reset = Commands.literal("resetVanilla")
                .executes((ct) -> {
                	ct.getSource().getLevel().serverLevelData.setThunderTime(0);
                	ct.getSource().getLevel().serverLevelData.setRainTime(0);
                	ct.getSource().getLevel().serverLevelData.setClearWeatherTime(0);
                    return Command.SINGLE_SUCCESS;
                });
        
        dispatcher.register(Commands.literal(FHMain.MODID).requires(s -> s.hasPermission(2)).then(Commands.literal("climate").then(get).then(init).then(rebuild).then(reset)));
    }
}
