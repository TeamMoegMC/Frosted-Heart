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
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.research.FHResearch;
import com.teammoeg.frostedheart.research.api.ResearchDataAPI;
import com.teammoeg.frostedheart.research.data.ResearchData;
import com.teammoeg.frostedheart.research.data.TeamResearchData;
import com.teammoeg.frostedheart.research.inspire.EnergyCore;
import com.teammoeg.frostedheart.research.research.Research;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;

public class ResearchCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> add = Commands.literal("research")
                .then(Commands.literal("complete").then(Commands.argument("name", StringArgumentType.string()).suggests((ct, s) -> {
                    for (Research r : FHResearch.getAllResearch())
                        if (r.getId().startsWith(s.getRemaining())) 
                            s.suggest(r.getId(),r.getName());
                    return s.buildFuture();
                }).executes(ct -> {
                    String rsn = ct.getArgument("name", String.class).toString();
            
                    Research rs = FHResearch.getResearch(rsn).get();
                    if (rs == null) {
                        ct.getSource().sendFailure(new TextComponent("Research not found").withStyle(ChatFormatting.RED));
                        return Command.SINGLE_SUCCESS;
                    }
                    ResearchData rd = ResearchDataAPI.getData(ct.getSource().getPlayerOrException()).getData(rs);
                    rd.setFinished(true);
                    rd.announceCompletion();
                    
                    ct.getSource().sendSuccess(new TextComponent("Succeed!").withStyle(ChatFormatting.GREEN), false);
                    return Command.SINGLE_SUCCESS;
                })).then(Commands.literal("all").executes(ct->{
                	TeamResearchData trd = ResearchDataAPI.getData(ct.getSource().getPlayerOrException());
                    for (Research r : FHResearch.getAllResearch()) {
                    	if(r.isInCompletable())continue;
                        ResearchData rd = trd.getData(r);
                        rd.setFinished(true);
                        rd.announceCompletion();
                    }
                    ct.getSource().sendSuccess(new TextComponent("Succeed!").withStyle(ChatFormatting.GREEN), false);
                    return Command.SINGLE_SUCCESS;
                })))
                .then(Commands.literal("edit").then(Commands.argument("enable", BoolArgumentType.bool()).executes(ct -> {
                    FHResearch.editor = ct.getArgument("enable", Boolean.class);
                    ct.getSource().sendSuccess(new TextComponent("Editing mode set " + String.valueOf(FHResearch.editor)).withStyle(ChatFormatting.GREEN), false);
                    return Command.SINGLE_SUCCESS;
                })))
                .then(Commands.literal("default").executes(ct -> {
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("energy").executes(ct -> {
                    EnergyCore.reportEnergy(ct.getSource().getPlayerOrException());
                    return Command.SINGLE_SUCCESS;
                }).then(Commands.literal("add").then(Commands.argument("amount",IntegerArgumentType.integer(0)).executes(ct -> {
                    EnergyCore.addExtraEnergy(ct.getSource().getPlayerOrException(),ct.getArgument("amount",Integer.class));
                    return Command.SINGLE_SUCCESS;
                }))))
                
                .then(Commands.literal("attribute").then(Commands.argument("name",StringArgumentType.string()).suggests((ct,s)->{
                	CompoundTag cnbt=ResearchDataAPI.getVariants(ct.getSource().getPlayerOrException());
                	cnbt.getAllKeys().forEach(st->s.suggest(st));
                    return s.buildFuture();
                	
                }).executes(ct -> {
                	CompoundTag cnbt=ResearchDataAPI.getVariants(ct.getSource().getPlayerOrException());
                	String rsn = ct.getArgument("name", String.class).toString();
                		ct.getSource().sendSuccess(GuiUtils.str(String.valueOf(cnbt.get(rsn))), false);
                    return Command.SINGLE_SUCCESS;
                })).then(Commands.literal("all").executes(ct->{
                	
                	CompoundTag cnbt=ResearchDataAPI.getVariants(ct.getSource().getPlayerOrException());
                	ct.getSource().sendSuccess(GuiUtils.str(cnbt.toString()), false);
                    return Command.SINGLE_SUCCESS;
                	
                }))
                		
                		.then(Commands.literal("set").then(Commands.argument("name",StringArgumentType.string()).suggests((ct,s)->{
                	CompoundTag cnbt=ResearchDataAPI.getVariants(ct.getSource().getPlayerOrException());
                	cnbt.getAllKeys().forEach(st->s.suggest(st));
                	
                    if("all".startsWith(s.getRemaining()))
                    	s.suggest("all");
                    return s.buildFuture();
                	
                }).then(Commands.argument("value",NbtTagArgument.nbtTag()).executes(ct->{
                	CompoundTag cnbt=ResearchDataAPI.getVariants(ct.getSource().getPlayerOrException());
                	String rsn = ct.getArgument("name", String.class).toString();
                	Tag nbt=ct.getArgument("value", Tag.class);
                	cnbt.put(rsn, nbt);
                	return Command.SINGLE_SUCCESS;
                })))))
                .then(Commands.literal("reset").then(Commands.argument("name", StringArgumentType.string()).suggests((ct, s) -> {
                    for (Research r : FHResearch.getAllResearch())
                        if (r.getId().startsWith(s.getRemaining()))
                            s.suggest(r.getId(),r.getName());
                    return s.buildFuture();
                }).executes(ct -> {
                	String rsn = ct.getArgument("name", String.class).toString();
                    ResearchDataAPI.getData(ct.getSource().getPlayerOrException()).resetData(FHResearch.getResearch(rsn).get(),true);
                    return Command.SINGLE_SUCCESS;
                })).then(Commands.literal("all").executes(ct->{
                    TeamResearchData trd = ResearchDataAPI.getData(ct.getSource().getPlayerOrException());
                    for (Research r : FHResearch.getAllResearch()) {
                        trd.resetData(r,true);
                    }
                    return Command.SINGLE_SUCCESS;
                })));
        dispatcher.register(Commands.literal(FHMain.MODID).requires(s -> s.hasPermission(2)).then(add));
    }
}
