package com.teammoeg.frostedheart.content.steamenergy.capabilities;

import com.teammoeg.frostedheart.FHMain;
import net.minecraft.world.item.ItemStack;

public class HeatStorageCapability {
    private static final String STEAM_KEY = "steam";
    int maxstore;
    ItemStack container;

    public HeatStorageCapability(ItemStack stack, int maxstorage) {
    }

    protected float getEnergy() {
        return container != null ? container.getOrCreateTag().getFloat(FHMain.MODID + ":" + STEAM_KEY) : 0;
    }

    // TODO: Check
    protected void setEnergy(float energy) {
        if (container != null)
            container.getOrCreateTag().putFloat(FHMain.MODID + ":" + STEAM_KEY, energy);
    }

    public float getEnergyStored() {
        return getEnergy();
    }

    public float receiveEnergy(float value, boolean simulate) {
        float current = getEnergy();
        float actual = Math.min(maxstore - current, value);
        if (!simulate) {
            setEnergy(actual + current);
        }
        return actual;
    }

    public float extractEnergy(float value, boolean simulate) {
        float current = getEnergy();
        float extracted = Math.min(value, current);
        if (!simulate) {
            setEnergy(current - extracted);
        }
        return extracted;
    }
}
