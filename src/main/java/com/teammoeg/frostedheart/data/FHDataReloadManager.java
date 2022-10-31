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

package com.teammoeg.frostedheart.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teammoeg.frostedheart.climate.WorldClimate;
import com.teammoeg.frostedheart.data.FHDataManager.FHDataType;
import com.teammoeg.frostedheart.util.StructureUtils;

import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;


@SuppressWarnings("deprecation")
public class FHDataReloadManager implements IResourceManagerReloadListener {
    public static final FHDataReloadManager INSTANCE = new FHDataReloadManager();
    private static final JsonParser parser = new JsonParser();

    @Override
    public void onResourceManagerReload(IResourceManager manager) {
        FHDataManager.reset();
        StructureUtils.addBanedBlocks();
        WorldClimate.clear();
        for (FHDataType dat : FHDataType.values()) {
            for (ResourceLocation rl : manager.getAllResourceLocations(dat.type.getLocation(), (s) -> s.endsWith(".json"))) {
                try {
                    try (IResource rc = manager.getResource(rl);
                         InputStream stream = rc.getInputStream();
                         InputStreamReader reader = new InputStreamReader(stream)) {
                        JsonObject object = parser.parse(reader).getAsJsonObject();
                        FHDataManager.register(dat, object);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}