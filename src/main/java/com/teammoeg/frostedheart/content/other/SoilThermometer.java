package com.teammoeg.frostedheart.content.other;

import com.teammoeg.frostedheart.base.item.FHBaseItem;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.climate.chunkdata.ChunkData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SoilThermometer extends FHBaseItem {
    public SoilThermometer(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        PlayerEntity entityplayer = entityLiving instanceof PlayerEntity ? (PlayerEntity) entityLiving : null;
        if (entityplayer instanceof ServerPlayerEntity) {
            entityplayer.sendMessage(GuiUtils.translateMessage("info.soil_thermometerbody", ChunkData.getTemperature(entityplayer.world, entityplayer.getPosition())), entityplayer.getUniqueID());
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 100;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }
}