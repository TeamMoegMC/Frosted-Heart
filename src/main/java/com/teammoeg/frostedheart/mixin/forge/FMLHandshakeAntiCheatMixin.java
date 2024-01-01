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

package com.teammoeg.frostedheart.mixin.forge;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teammoeg.frostedheart.FHConfig;
import com.teammoeg.frostedheart.FHMain;

import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import net.minecraftforge.fml.network.NetworkEvent;

@Mixin(FMLHandshakeHandler.class)
public class FMLHandshakeAntiCheatMixin {
	@Inject(at = @At("TAIL"), method = "handleClientModListOnServer", remap = false,cancellable=true)
	void fh$handleClientModListOnServer(FMLHandshakeMessages.C2SModListReply clientModList,
			Supplier<NetworkEvent.Context> c, CallbackInfo cbi) {
		Set<String> cli=clientModList.getModList().stream().map(String::toLowerCase).collect(Collectors.toSet());
		for(String s:FHConfig.COMMON.blackmods.get()) {
			if(cli.contains(s)) {
				FHMain.LOGGER.warn("Rejected Connection: Blacklisted mods ");
				TextComponent t=new TextComponent("警告：你有被认为是作弊的mod。");
				c.get().getNetworkManager()
				.sendPacket(new ClientboundLoginDisconnectPacket(t), (p_211391_2_) -> {
					c.get().getNetworkManager().closeChannel(t);
                 });
				
				
				cbi.cancel();
			}
		}
	}
}
