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
import java.util.Map;
import java.util.Random;
import java.util.function.IntBinaryOperator;

import com.teammoeg.frostedheart.base.item.FHBaseItem;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.compat.tetra.TetraCompat;
import com.teammoeg.frostedheart.content.tools.FHLeveledTool;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import se.mickelus.tetra.properties.IToolProvider;

import net.minecraft.world.item.Item.Properties;

public class ProspectorPick extends FHLeveledTool {
    public static ResourceLocation tag = new ResourceLocation("forge:ores");

    public ProspectorPick(String name, int lvl, Properties properties) {
        super(name,lvl, properties);
    }
    public static int getHorizonalRange(ItemStack item) {
        return getLevel(item)+8;
    }

    public static int getVerticalRange(ItemStack item) {
        return getLevel(item)+3;
    }
    public static int getLevel(ItemStack item) {
    	if(item.getItem() instanceof FHLeveledTool)
    		return ((FHLeveledTool)item.getItem()).getLevel();
    	
    	return ((IToolProvider)item.getItem()).getToolLevel(item,TetraCompat.proPick);
    }
    public static float getCorrectness(ItemStack item) {
    	if(item.getItem() instanceof FHLeveledTool)
    		return 1;
    	
    	return ((IToolProvider)item.getItem()).getToolEfficiency(item,TetraCompat.proPick)+1;
    }
    public static InteractionResult doProspect(Player player,Level world,BlockPos blockpos,ItemStack is,InteractionHand h) {
          if (player != null && (!(player instanceof FakePlayer))) {//fake players does not deserve XD
              if (world.getBlockState(blockpos).getBlock().getTags().contains(tag)) {//early exit 'cause ore found
                  player.displayClientMessage(new TranslatableComponent(world.getBlockState(blockpos).getBlock().getDescriptionId()).withStyle(ChatFormatting.GOLD), false);
                  return InteractionResult.SUCCESS;
              }
              int x = blockpos.getX();
              int y = blockpos.getY();
              int z = blockpos.getZ();
              is.hurtAndBreak(1, player, (player2) -> player2.broadcastBreakEvent(h));
              if (!world.isClientSide) {
                  Random rnd = new Random(BlockPos.asLong(x, y, z) ^ 0xf64128086dd425ffL);//randomize
                  //This is predictable, but not any big problem. Cheaters can use x-ray or other things rather then hacking in this.
                  float corr=getCorrectness(is);
                  if (rnd.nextInt((int) (10*corr)) != 0) {//mistaken rate 10%
                      BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(x, y, z);
                      Block ore;
                      HashMap<String, Integer> founded = new HashMap<>();
                      int rseed = 0;
                      int hrange = getHorizonalRange(is);
                      int vrange = getVerticalRange(is);
                      for (int x2 = -hrange; x2 < hrange; x2++)
                          for (int y2 = -Math.min(y, vrange); y2 < vrange; y2++)
                              for (int z2 = -hrange; z2 < hrange; z2++) {
                                  int BlockX = x + x2;
                                  int BlockY = y + y2;
                                  int BlockZ = z + z2;
                                  ore = world.getBlockState(mutable.set(BlockX, BlockY, BlockZ)).getBlock();
                                  if (ore.getTags().contains(tag)) {
                                      founded.merge(ore.getDescriptionId(), 1, Integer::sum);
                                      rseed++;
                                  }
                              }

                      if (!founded.isEmpty()) {
                      	int cnt=founded.values().stream().reduce(0,(a,b)->a+b);
                          rseed = rnd.nextInt(cnt);
                          String ore_name = null;
                          int count = 0;
                          for (Map.Entry<String, Integer> me : founded.entrySet()) {
                          	rseed-=me.getValue();
                              if (rseed <= 0) {
                                  ore_name = me.getKey();
                                  count = me.getValue();
                                  
                              }
                          }
                          if (ore_name != null) {
                              if (count < 20)
                                  player.displayClientMessage(GuiUtils.translateMessage("vein_size.small").append(new TranslatableComponent(ore_name)).withStyle(ChatFormatting.GOLD), false);
                              else if (count < 40)
                                  player.displayClientMessage(GuiUtils.translateMessage("vein_size.medium").append(new TranslatableComponent(ore_name)).withStyle(ChatFormatting.GOLD), false);
                              else {
                                  player.displayClientMessage(GuiUtils.translateMessage("vein_size.large").append(new TranslatableComponent(ore_name)).withStyle(ChatFormatting.GOLD), false);
                              }
                              return InteractionResult.SUCCESS;
                          }
                      }
                  }
                  player.displayClientMessage(GuiUtils.translateMessage("vein_size.nothing").withStyle(ChatFormatting.GOLD), false);
              }
          }
          return InteractionResult.SUCCESS;
    }
    @SuppressWarnings("resource")
    @Override
    public InteractionResult useOn(UseOnContext context) {
    	return doProspect(context.getPlayer(),context.getLevel(),context.getClickedPos(),context.getItemInHand(),context.getHand());
    }
}
