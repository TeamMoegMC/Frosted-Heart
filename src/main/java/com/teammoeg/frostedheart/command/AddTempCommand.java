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

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.climate.chunkdata.ChunkData;
import com.teammoeg.frostedheart.climate.chunkdata.ITemperatureAdjust;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.text.StringTextComponent;

public class AddTempCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> add = Commands.literal("set")
                .then(Commands.argument("position", BlockPosArgument.blockPos()).executes((ct) -> {
                    ChunkData.removeTempAdjust(ct.getSource().getLevel(), BlockPosArgument.getOrLoadBlockPos(ct, "position"));
                    return Command.SINGLE_SUCCESS;
                }).then(Commands.argument("range", IntegerArgumentType.integer())
                        .then(Commands.argument("temperature", IntegerArgumentType.integer()).executes((ct) -> {
                            ChunkData.addCubicTempAdjust(ct.getSource().getLevel(),
                                    BlockPosArgument.getOrLoadBlockPos(ct, "position"),
                                    IntegerArgumentType.getInteger(ct, "range"),
                                    IntegerArgumentType.getInteger(ct, "temperature"));
                            return Command.SINGLE_SUCCESS;
                        }))));
        LiteralArgumentBuilder<CommandSource> get = Commands.literal("get")
                .executes((ct) -> {
                    Collection<ITemperatureAdjust> adjs = ChunkData.getAdjust(ct.getSource().getLevel(), ct.getSource().getPlayerOrException().blockPosition());
                    if (adjs.size() == 0) {
                        ct.getSource().sendSuccess(new StringTextComponent("No Active Adjust!"), true);
                    } else {
                        ct.getSource().sendSuccess(new StringTextComponent("Active Adjusts:"), true);
                        for (ITemperatureAdjust adj : adjs) {
                            ct.getSource().sendSuccess(new StringTextComponent("center:" + adj.getCenterX() + " " + adj.getCenterY() + " " + adj.getCenterZ() + ",radius:" + adj.getRadius() + ",temperature:" + adj.getValueAt(ct.getSource().getPlayerOrException().blockPosition())), true);
                        }
                    }
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("position", BlockPosArgument.blockPos())
                        .executes((ct) -> {
                            Collection<ITemperatureAdjust> adjs = ChunkData.getAdjust(ct.getSource().getLevel(), BlockPosArgument.getOrLoadBlockPos(ct, "position"));
                            if (adjs.size() == 0) {
                                ct.getSource().sendSuccess(new StringTextComponent("No Active Adjust!"), true);
                            } else {
                                ct.getSource().sendSuccess(new StringTextComponent("Active Adjusts:"), true);
                                for (ITemperatureAdjust adj : adjs) {
                                    ct.getSource().sendSuccess(new StringTextComponent("center:" + adj.getCenterX() + " " + adj.getCenterY() + " " + adj.getCenterZ() + ",radius:" + adj.getRadius() + ",temperature:" + adj.getValueAt(BlockPosArgument.getOrLoadBlockPos(ct, "position"))), true);
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }));
        dispatcher.register(Commands.literal(FHMain.MODID).requires(s -> s.hasPermission(2)).then(Commands.literal("temperature").then(add).then(get)));
    }
}
