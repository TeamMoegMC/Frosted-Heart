/*
 * Copyright (c) 2021-2024 TeamMoeg
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

package com.teammoeg.frostedheart.content.tools.oredetect;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.properties.IToolProvider;

import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.compat.tetra.TetraCompat;
import com.teammoeg.frostedheart.content.tools.FHLeveledTool;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;

import net.minecraft.world.item.Item.Properties;

public class CoreSpade extends FHLeveledTool {
    public static ResourceLocation otag = new ResourceLocation("forge:ores");
    public static ResourceLocation stag = new ResourceLocation("forge:stone");

    public CoreSpade(String name, int lvl, Properties properties) {
        super(name,lvl, properties);
    }

    public static int getHorizonalRange(ItemStack item) {
        return Math.max(3,getLevel(item));
    }

    public static int getVerticalRange(ItemStack item) {
        return getLevel(item)==1?32:(48+(getLevel(item)-1)*16);
    }
    public static int getLevel(ItemStack item) {
    	if(item.getItem() instanceof FHLeveledTool)
    		return ((FHLeveledTool)item.getItem()).getLevel();
    	
    	return ((IToolProvider)item.getItem()).getToolLevel(item,TetraCompat.coreSpade);
    }
    public static float getCorrectness(ItemStack item) {
    	if(item.getItem() instanceof FHLeveledTool)
    		return 1;
    	
    	return ((IToolProvider)item.getItem()).getToolEfficiency(item,TetraCompat.coreSpade)+1;
    }
    public static InteractionResult doProspect(Player player,Level world,BlockPos blockpos,ItemStack is,InteractionHand h) {
         if (player != null && (!(player instanceof FakePlayer))) {// fake players does not deserve XD
             if (!world.isClientSide&&world.getBlockState(blockpos).getBlock().getTags().contains(otag)) {// early exit 'cause ore found
                 player.displayClientMessage(
                         new TranslatableComponent(world.getBlockState(blockpos).getBlock().getDescriptionId())
                                 .withStyle(ChatFormatting.GOLD),
                         false);
                 return InteractionResult.SUCCESS;
             }
             int x = blockpos.getX();
             int y = blockpos.getY();
             int z = blockpos.getZ();
  
             is.hurtAndBreak(1, player, (player2) -> player2.broadcastBreakEvent(h));
             if (!world.isClientSide) {
                 Random rnd = new Random(BlockPos.asLong(x, y, z) ^ 0x9a6dc5270b92313dL);// randomize
                 // This is predictable, but not any big problem. Cheaters can use x-ray or other
                 // things rather then hacking in this.

                 Predicate<Set<ResourceLocation>> tagdet;
                 float corr=getCorrectness(is);
                 if (rnd.nextInt((int) (20*corr)) != 0) {
                     tagdet = ts -> (ts.contains(otag)) || ts.contains(stag);
                 } else
                     tagdet = ts -> ts.contains(stag);
                 BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(x, y, z);
                 Block ore;
                 HashMap<String, Integer> founded = new HashMap<>();
                 final int hrange = getHorizonalRange(is);
                 int vrange = getVerticalRange(is);
                 vrange = Math.min(y, (rnd.nextInt(vrange) + vrange) / 2);

                 
                 for (int y2 = -vrange; y2 < 0; y2++)
                	 for (int x2 = -hrange; x2 < hrange; x2++)
                         for (int z2 = -hrange; z2 < hrange; z2++) {
                             int BlockX = x + x2;
                             int BlockY = y + y2;
                             int BlockZ = z + z2;
                             ore = world.getBlockState(mutable.set(BlockX, BlockY, BlockZ)).getBlock();
                             if (!ore.getRegistryName().getNamespace().equals("minecraft") && tagdet.test(ore.getTags())) {
                                 founded.merge(ore.getDescriptionId(), 1, Integer::sum);
                             }
                         }

                 if (!founded.isEmpty()) {
                     int count = 0;
                     MutableComponent s = GuiUtils.translateMessage("corespade.ore");
                     for (Entry<String, Integer> f : founded.entrySet()) {
                         if (rnd.nextInt((int) (f.getValue()*corr)) != 0) {
                             s = s.append(new TranslatableComponent(f.getKey())
                                     .withStyle(ChatFormatting.GREEN).append(","));
                             count++;
                         }
                     }
                     if (count > 0) {
                         player.displayClientMessage(s, false);
                         return InteractionResult.SUCCESS;
                     }
                 }
                 player.displayClientMessage(GuiUtils.translateMessage("corespade.nothing").withStyle(ChatFormatting.GRAY),
                         false);
             }
         }
         return InteractionResult.SUCCESS;
    }
    @SuppressWarnings("resource")
    @Override
    public InteractionResult useOn(UseOnContext context) {
       return doProspect(context.getPlayer(),context.getLevel(),context.getClickedPos(),context.getItemInHand(),context.getHand());
        
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(GuiUtils.translateTooltip("meme.core_spade").withStyle(ChatFormatting.GRAY));
    }
}
