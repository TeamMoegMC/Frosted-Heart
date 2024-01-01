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

package com.teammoeg.frostedheart.recipe;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.teammoeg.frostedheart.compat.jei.JEICompat;
import com.teammoeg.frostedheart.content.generator.GeneratorRecipe;
import com.teammoeg.frostedheart.content.generator.GeneratorSteamRecipe;
import com.teammoeg.frostedheart.content.incubator.IncubateRecipe;
import com.teammoeg.frostedheart.content.recipes.CampfireDefrostRecipe;
import com.teammoeg.frostedheart.content.recipes.DefrostRecipe;
import com.teammoeg.frostedheart.content.recipes.DietValueRecipe;
import com.teammoeg.frostedheart.content.recipes.InspireRecipe;
import com.teammoeg.frostedheart.content.recipes.ResearchPaperRecipe;
import com.teammoeg.frostedheart.content.recipes.InstallInnerRecipe;
import com.teammoeg.frostedheart.content.recipes.SmokingDefrostRecipe;
import com.teammoeg.frostedheart.content.steamenergy.charger.ChargerRecipe;
import com.teammoeg.frostedheart.content.steamenergy.sauna.SaunaRecipe;
import com.teammoeg.frostedheart.trade.policy.TradePolicy;
import com.teammoeg.frostedheart.trade.policy.TradePolicy.Weighted;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.utils.TagUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class FHRecipeReloadListener implements ResourceManagerReloadListener {
    private final ServerResources dataPackRegistries;

    public FHRecipeReloadListener(ServerResources dataPackRegistries) {
        this.dataPackRegistries = dataPackRegistries;
    }

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
        if (dataPackRegistries != null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                Iterator<ServerLevel> it = server.getAllLevels().iterator();
                // Should only be false when no players are loaded, so the data will be synced on login
                if (it.hasNext())
                    ApiUtils.addFutureServerTask(it.next(),
                            () -> StaticTemplateManager.syncMultiblockTemplates(PacketDistributor.ALL.noArg(), true)
                    );
            }
        }
    }

    RecipeManager clientRecipeManager;

    @SubscribeEvent
    public void onTagsUpdated(TagsUpdatedEvent event) {
        if (clientRecipeManager != null)
            TagUtils.setTagCollectionGetters(ItemTags::getAllTags, BlockTags::getAllTags);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRecipesUpdated(RecipesUpdatedEvent event) {
        clientRecipeManager = event.getRecipeManager();
        if (!Minecraft.getInstance().hasSingleplayerServer())
            buildRecipeLists(clientRecipeManager);
        
    }

    public static void buildRecipeLists(RecipeManager recipeManager) {
        Collection<Recipe<?>> recipes = recipeManager.getRecipes();
        if (recipes.size() == 0)
            return;
        GeneratorRecipe.recipeList = filterRecipes(recipes, GeneratorRecipe.class, GeneratorRecipe.TYPE);
        ChargerRecipe.recipeList = filterRecipes(recipes, ChargerRecipe.class, ChargerRecipe.TYPE);
        GeneratorSteamRecipe.recipeList = filterRecipes(recipes, GeneratorSteamRecipe.class, GeneratorSteamRecipe.TYPE);
        InstallInnerRecipe.recipeList = recipes.stream()
                .filter(iRecipe -> iRecipe.getClass() == InstallInnerRecipe.class)
                .map(e -> (InstallInnerRecipe) e)
                .collect(Collectors.<InstallInnerRecipe, ResourceLocation, InstallInnerRecipe>toMap(recipe -> recipe.getBuffType(), recipe -> recipe));
        CampfireDefrostRecipe.recipeList = recipes.stream()
                .filter(iRecipe -> iRecipe.getClass() == CampfireDefrostRecipe.class)
                .map(e -> (CampfireDefrostRecipe) e)
                .collect(Collectors.toMap(recipe -> recipe.getId(), recipe -> recipe));
        SmokingDefrostRecipe.recipeList = recipes.stream()
                .filter(iRecipe -> iRecipe.getClass() == SmokingDefrostRecipe.class)
                .map(e -> (DefrostRecipe) e)
                .collect(Collectors.toMap(recipe -> recipe.getId(), recipe -> recipe));
        DietValueRecipe.recipeList = recipes.stream()
                .filter(iRecipe -> iRecipe.getClass() == DietValueRecipe.class)
                .map(e -> (DietValueRecipe) e)
                .collect(Collectors.toMap(recipe -> recipe.item, recipe -> recipe));
        InspireRecipe.recipes=filterRecipes(recipes, InspireRecipe.class, InspireRecipe.TYPE).values().stream().collect(Collectors.toList());
        ResearchPaperRecipe.recipes = filterRecipes(recipes, ResearchPaperRecipe.class, ResearchPaperRecipe.TYPE).values().stream().collect(Collectors.toList());
        SaunaRecipe.recipeList = filterRecipes(recipes, SaunaRecipe.class, SaunaRecipe.TYPE);
        IncubateRecipe.recipeList=filterRecipes(recipes, IncubateRecipe.class, IncubateRecipe.TYPE);
        TradePolicy.policies=filterRecipes(recipes,TradePolicy.class,TradePolicy.TYPE).values().stream().collect(Collectors.toMap(t->t.getName(),t->t));
        //System.out.println(TradePolicy.policies.size());
        TradePolicy.items=TradePolicy.policies.values().stream().map(TradePolicy::asWeight).filter(Objects::nonNull).collect(Collectors.toList());
        //System.out.println(TradePolicy.items.size());
        TradePolicy.totalW=TradePolicy.items.stream().mapToInt(w->w.weight).sum();
        //System.out.println(TradePolicy.totalW);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> JEICompat::scheduleSyncJEI);
        
    }

    static <R extends Recipe<?>> Map<ResourceLocation, R> filterRecipes(Collection<Recipe<?>> recipes, Class<R> recipeClass, RecipeType<R> recipeType) {
        return recipes.stream()
                .filter(iRecipe -> iRecipe.getType() == recipeType)
                .map(recipeClass::cast)
                .collect(Collectors.toMap(recipe -> recipe.getId(), recipe -> recipe));
    }

    static <R extends Recipe<?>> Map<ResourceLocation, R> filterRecipes(Collection<Recipe<?>> recipes, Class<R> recipeClass) {
        return recipes.stream()
                .filter(iRecipe -> iRecipe.getClass() == recipeClass)
                .map(recipeClass::cast)
                .collect(Collectors.toMap(recipe -> recipe.getId(), recipe -> recipe));
    }
}
