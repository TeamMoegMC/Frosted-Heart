package com.teammoeg.frostedheart.content.foods.DailyKitchen;


import java.util.*;

import static com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe.extractFluid;

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.research.inspire.EnergyCore;
import com.teammoeg.frostedheart.util.client.GuiUtils;
import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.api.IDietTracker;
import top.theillusivec4.diet.common.capability.DietTrackerCapability;

public class DailyKitchen {
    @CapabilityInject(IWantedFoodCapability.class)
    public static Capability<IWantedFoodCapability> WANTED_FOOD_CAPABILITY;

    public static void setupWantedFoodCapability(){
        CapabilityManager.INSTANCE.register(IWantedFoodCapability.class,
                new Capability.IStorage<IWantedFoodCapability>() {
                    @Nullable
                    @Override
                    public INBT writeNBT(Capability<IWantedFoodCapability> capability, IWantedFoodCapability instance, Direction side) {
                        return instance.serializeNBT();
                    }

                    @Override
                    public void readNBT(Capability<IWantedFoodCapability> capability, IWantedFoodCapability instance, Direction side, INBT nbt) {
                        instance.deserializeNBT((CompoundNBT)nbt);
                    }
                },
                () -> null
        );
    }


    /**
     * This function generates 1-3 foods that player wants to eat.It should be called once every morning(in frostedheart.events.PlayerEvents.sendForecastMessages).
     * It records how many kinds of foods the player have eaten in wantedFoodCapability(It seems that diet mod doesn't record this), eatenFoodsAmount WON'T be changed until this function is called again. So the player will get same effect in one day.
     */
    public static void generateWantedFood(PlayerEntity player){
        LazyOptional<IDietTracker> dietTracker = DietCapability.get(player);
        if(!dietTracker.isPresent()){
            return;
        }
        WantedFoodCapability wantedFoodCapability = (WantedFoodCapability)player.getCapability(WANTED_FOOD_CAPABILITY).orElse(new WantedFoodCapability());
        Set<Item> foodsEaten = DietCapability.get(player).orElse(new DietTrackerCapability.EmptyDietTracker()).getEaten();
        /*为了避免重复，每日厨房从diet存储的数据中获取foodsEaten。
        但若diet存储的foodsEaten会在死亡时候清空的话，则此处获取的foodsEaten也会是空的。
        在runClient测试中死亡会清空包括foodsEaten在内的所有数据(在TWR环境中不应如此)，此处暂且保留。
        *如果在整合包测试中也存在死亡后清空foodsEaten数据的话，则需要在fh里面保存foodsEaten数据
        艹了，为什么我的build文件夹里没有libs文件夹*/

        int eatenFoodsAmount = foodsEaten.size();
        int wantedFoodsAmount = Math.min(eatenFoodsAmount / 10, 3);
        if(wantedFoodsAmount==0) return;

        wantedFoodCapability.setEatenFoodsAmount(eatenFoodsAmount);

        WantedFoodsGenerator generator = new WantedFoodsGenerator(foodsEaten, eatenFoodsAmount);

        wantedFoodCapability.setWantedFoods(generator.generate());
        player.sendStatusMessage(generator.getWantedFoodsText(), false);
    }


    public static void tryGiveBenefits(ServerPlayerEntity player, ItemStack foodItemStack){
        Benefits benefits = new Benefits(player);
        benefits.tryGive(foodItemStack);
    }

     //copy data from old capability to new capability
    public static void copyData(LazyOptional<IWantedFoodCapability> oldCapability, LazyOptional<IWantedFoodCapability> newCapability){
        newCapability.ifPresent((newCap) -> oldCapability.ifPresent((oldCap) -> newCap.deserializeNBT(oldCap.serializeNBT())));
    }
}

class WantedFoodsGenerator {
    private final Random random;
    private final Set<Item> foodsEaten;
    private TextComponent wantedFoodsText = GuiUtils.translateMessage("wanted_foods");
    private final int eatenFoodsAmount;
    private final int maxGenerateAmount;
    private HashSet<Item> wantedFoods = new HashSet<>();

    public WantedFoodsGenerator(Set<Item> foodsEaten, int eatenFoodsAmount){
        random = new Random();
        this.foodsEaten = foodsEaten;
        this.eatenFoodsAmount = eatenFoodsAmount;
        maxGenerateAmount = Math.min(eatenFoodsAmount/10, 3);

    }

    private static boolean isNotBadFood(Item food){
        Set<ResourceLocation> tags = food.getTags();
        for(ResourceLocation tag : tags){
            String path = tag.getPath();
            if(path.equals("raw_food") || path.equals("bad_food")) return false;
        }
        return true;
    }

    public HashSet<Item> generate(){
        ArrayList<Integer> wantedFoodsNumber = new ArrayList<>();
        for(int i=0; i<maxGenerateAmount;){
            int randomNumber = random.nextInt(eatenFoodsAmount);
            if(!wantedFoodsNumber.contains(randomNumber)) {
                wantedFoodsNumber.add(randomNumber);
                i++;
            }
        }
        int i = 0;
        for(Item food : foodsEaten) {
            if(wantedFoodsNumber.contains(i) && (isNotBadFood(food)) && !(food instanceof ItemFluidContainer/*Don't eat thermos!*/) ){
                wantedFoods.add(food);
                wantedFoodsText.appendSibling(food.getName()).appendSibling(new StringTextComponent("  "));
            }
            i++;
        }
        if(wantedFoods.isEmpty()){
            wantedFoods = this.generate();
        }
        return wantedFoods;
    }

    public TextComponent getWantedFoodsText() {
        return wantedFoodsText;
    }
}

class Benefits {
    private final Effect[] basicEffects = new Effect[]{Effects.STRENGTH, Effects.SPEED, Effects.HASTE};
    private final ServerPlayerEntity player;
    private final WantedFoodCapability capability;
    private final int eatenFoodsAmount;
    private final int benefitLevel;
    private final Random random = new Random();
    private final int basicEffectDuration;

    public Benefits(ServerPlayerEntity player){
        this.player = player;
        this.capability = (WantedFoodCapability)player.getCapability(DailyKitchen.WANTED_FOOD_CAPABILITY).orElse(new WantedFoodCapability());
        this.eatenFoodsAmount = capability.getEatenFoodsAmount();
        this.benefitLevel = Math.min((eatenFoodsAmount/10), 7);
        this.basicEffectDuration = Math.min(3600+150*eatenFoodsAmount, 20000);//can't be more than one day
    }


    public void giveEnergy(){
        EnergyCore.addEnergy(player, (int)((5000+EnergyCore.getEnergy(player))*Math.min((float)eatenFoodsAmount/200, 0.5)));
    }

    /**only used when amount<3
     * if amount = 3, use giveBasicEffects(int amount, int[] potionLevel)
     */
    private void giveBasicEffects(int amount){
        if(amount == 1){
            player.addPotionEffect(new EffectInstance(basicEffects[random.nextInt(2)], basicEffectDuration, 0));

        }
        else if(amount == 2) {
            int notGiveEffect = random.nextInt(2);
            for (int i = 0; i < 2; i++) {
                if (i != notGiveEffect) {
                    player.addPotionEffect(new EffectInstance(basicEffects[i], basicEffectDuration, 0));
                }
            }
        }
    }

    private void giveBasicEffects(int amount, int[] potionLevel){
        if(amount <3) {
            this.giveBasicEffects(amount);
        }
        else if(amount == 3)
            for(int i=0; i<3; i++){
                player.addPotionEffect(new EffectInstance(basicEffects[i], basicEffectDuration, potionLevel[i]));
            }
        else FHMain.LOGGER.error("Invalid effect amount input!");
    }

    private void giveHealthRegen(int duration){
        this.player.addPotionEffect(new EffectInstance(Effects.REGENERATION, duration));
    }


    private void giveEffects(){
        switch (benefitLevel){
            case 0: {
                this.giveBasicEffects(1);
                break;
            }
            case 1: {
                this.giveBasicEffects(1);
                this.giveHealthRegen(60);
                break;
            }
            case 2:{
                this.giveBasicEffects(2);
                this.giveHealthRegen(60);
                break;
            }
            case 3:{
                this.giveBasicEffects(2);
                this.giveHealthRegen(100);
                break;
            }
            case 4:{
                this.giveBasicEffects(3, new int[]{0, 0, 0});
                this.giveHealthRegen(100);
                break;
            }
            case 5:{
                this.giveBasicEffects(3, new int[]{0, 1, 0});
                this.giveHealthRegen(120);
                break;
            }
            case 6:{
                this.giveBasicEffects(3, new int[]{0, 1, 1});
                this.giveHealthRegen(160);
                break;
            }
            case 7: {
                this.giveBasicEffects(3, new int[]{0, 1, 1});
                this.giveHealthRegen(200);
                break;
            }
        }
    }

    public void give(){
        this.giveEffects();
        this.giveEnergy();
        capability.countEatenTimes();

        player.sendStatusMessage(GuiUtils.translateMessage("eat_wanted_food"), false);
    }

    public void tryGive(Item food){
        if(capability.getWantedFoods().contains(food)){
            this.give();
        }
    }

    public void tryGive(ItemStack foodItemStack){
        if(capability.getEatenTimes() > 3) return;
        Item foodOrSoupContainer = foodItemStack.getItem();
        if(foodOrSoupContainer instanceof ItemFluidContainer){
            assert foodItemStack.getTag() != null;
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(foodItemStack.getTag().getCompound("Fluid").getString("FluidName")));
            BowlContainingRecipe recipe = BowlContainingRecipe.recipes.get(fluid);
            if(recipe != null){
                tryGive(recipe.handle(fluid).getItem());
            }
        }
        else tryGive(foodOrSoupContainer);
    }
}




