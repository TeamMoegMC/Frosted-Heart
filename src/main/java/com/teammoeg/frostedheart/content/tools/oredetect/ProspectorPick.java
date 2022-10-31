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

package com.teammoeg.frostedheart.content.tools.oredetect;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.teammoeg.frostedheart.base.item.FHBaseItem;
import com.teammoeg.frostedheart.client.util.GuiUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class ProspectorPick extends FHBaseItem {
    public static ResourceLocation tag = new ResourceLocation("forge:ores");

    public ProspectorPick(String name, int hrange, int vrange, Properties properties) {
        super(name, properties);
        this.vrange = vrange;
        this.hrange = hrange;
    }

    private int vrange;
    private int hrange;

    public int getHorizonalRange(ItemStack item) {
        return hrange;
    }

    public int getVerticalRange(ItemStack item) {
        return vrange;
    }

    @SuppressWarnings("resource")
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && (!(player instanceof FakePlayer))) {//fake players does not deserve XD
            World world = context.getWorld();
            BlockPos blockpos = context.getPos();
            if (world.getBlockState(blockpos).getBlock().getTags().contains(tag)) {//early exit 'cause ore found
                player.sendStatusMessage(new TranslationTextComponent(world.getBlockState(blockpos).getBlock().getTranslationKey()).mergeStyle(TextFormatting.GOLD), false);
                return ActionResultType.SUCCESS;
            }
            int x = blockpos.getX();
            int y = blockpos.getY();
            int z = blockpos.getZ();
            context.getItem().damageItem(1, player, (player2) -> player2.sendBreakAnimation(context.getHand()));
            if (!world.isRemote) {
                Random rnd = new Random(BlockPos.pack(x, y, z) ^ 0xf64128086dd425ffL);//randomize
                //This is predictable, but not any big problem. Cheaters can use x-ray or other things rather then hacking in this.
                if (rnd.nextInt(10) != 0) {//mistaken rate 10%
                    BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);
                    Block ore;
                    HashMap<String, Integer> founded = new HashMap<>();
                    int rseed = 0;
                    int hrange = this.getHorizonalRange(context.getItem());
                    int vrange = this.getVerticalRange(context.getItem());
                    for (int x2 = -hrange; x2 < hrange; x2++)
                        for (int y2 = -Math.min(y, vrange); y2 < vrange; y2++)
                            for (int z2 = -hrange; z2 < hrange; z2++) {
                                int BlockX = x + x2;
                                int BlockY = y + y2;
                                int BlockZ = z + z2;
                                ore = world.getBlockState(mutable.setPos(BlockX, BlockY, BlockZ)).getBlock();
                                if (ore.getTags().contains(tag)) {
                                    founded.merge(ore.getTranslationKey(), 1, Integer::sum);
                                    rseed++;
                                }
                            }

                    if (!founded.isEmpty()) {
                        rseed = rnd.nextInt(founded.size());
                        String ore_name = null;
                        int count = 0;
                        for (Map.Entry<String, Integer> me : founded.entrySet()) {
                            if (rseed <= 0) {
                                ore_name = me.getKey();
                                count = me.getValue();
                            }
                            rseed--;
                        }
                        if (ore_name != null) {
                            if (count < 20)
                                player.sendStatusMessage(GuiUtils.translateMessage("vein_size.small").appendSibling(new TranslationTextComponent(ore_name)).mergeStyle(TextFormatting.GOLD), false);
                            else if (count < 40)
                                player.sendStatusMessage(GuiUtils.translateMessage("vein_size.medium").appendSibling(new TranslationTextComponent(ore_name)).mergeStyle(TextFormatting.GOLD), false);
                            else {
                                player.sendStatusMessage(GuiUtils.translateMessage("vein_size.large").appendSibling(new TranslationTextComponent(ore_name)).mergeStyle(TextFormatting.GOLD), false);
                            }
                            return ActionResultType.SUCCESS;
                        }
                    }
                }
                player.sendStatusMessage(GuiUtils.translateMessage("vein_size.nothing").mergeStyle(TextFormatting.GOLD), false);
            }
        }
        return ActionResultType.SUCCESS;
    }
}
