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

package com.teammoeg.frostedheart.mixin.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.teammoeg.frostedheart.util.mixin.ContraptionCostUtils;
import com.teammoeg.frostedheart.util.mixin.IStressContraption;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

@Mixin(AbstractContraptionEntity.class)
public abstract class MixinAbstractContraption extends Entity implements IStressContraption {
    boolean shoulddisb = false;

    float calculatedCost = -1;
    float actorCost = -1;
    float calculatedRotationCost = -1;
    @Shadow(remap = false)
    protected Contraption contraption;
    public MixinAbstractContraption(EntityType<?> p_i48580_1_, World p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    @Shadow(remap = false)
    public abstract void disassemble();

    @Override
    public float getActorCost() {
        if (!this.isAlive()) return 0;
        if (actorCost == -1)
            actorCost = ContraptionCostUtils.calculateActorStressApply(contraption);
        return actorCost;
    }

    @Override
    public float getRotationStressCost() {
        if (!this.isAlive()) return 0;
        if (actorCost == -1)
            actorCost = ContraptionCostUtils.calculateActorStressApply(contraption);
        if (calculatedRotationCost == -1)
            calculatedRotationCost = ContraptionCostUtils.calculateRotationStressApply(contraption);
        return calculatedRotationCost + actorCost;
    }

    @Override
    public float getStressCost() {
        if (!this.isAlive()) return 0;
        if (actorCost == -1)
            actorCost = ContraptionCostUtils.calculateActorStressApply(contraption);
        if (calculatedCost == -1)
            calculatedCost = ContraptionCostUtils.calculateStressApply(contraption);
        return calculatedCost + actorCost;
    }

    /**
     * @author khjxiaogu
     * @reason force reset contraptions for mod propose
     */
    @Inject(at = @At("TAIL"), method = "readAdditional(Lnet/minecraft/nbt/CompoundNBT;Z)V", remap = false)
    protected void readAdditional(CompoundNBT compound, boolean spawnPacket, CallbackInfo cbi) {
        if (!world.isRemote)
            if (compound.getInt("spinst") != 2) {
                shoulddisb = true;
            }
    }

    /**
     * @author khjxiaogu
     * @reason force reset contraptions for mod propose
     */
    @Inject(at = @At("TAIL"), method = "tick")
    protected void tick(CallbackInfo cbi) {
        if (!world.isRemote)
            if (this.shoulddisb)
                this.disassemble();
    }

    /**
     * @author khjxiaogu
     * @reason force reset contraptions for mod propose
     */
    @Inject(at = @At("TAIL"), method = "writeAdditional(Lnet/minecraft/nbt/CompoundNBT;Z)V", remap = false)
    protected void writeAdditional(CompoundNBT compound, boolean spawnPacket, CallbackInfo cbi) {
        if (!world.isRemote)
            compound.putInt("spinst", 2);
    }
}
