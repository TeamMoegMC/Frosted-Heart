/*
 * Copyright (c) 2022 TeamMoeg
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

package com.teammoeg.frostedheart.util;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class RankineRemap {

    public RankineRemap() {
    }

    public static HashMap<ResourceLocation, ResourceLocation> rankineremap = new HashMap<>();

    static {
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_bricks_stairs"), new ResourceLocation("rankine:arkose_stairs"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_bricks_vertical_slab"), new ResourceLocation("rankine:arkose_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_button"), new ResourceLocation("rankine:arkose_button"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_pressure_plate"), new ResourceLocation("rankine:arkose_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_wall"), new ResourceLocation("rankine:arkose_wall"));
        rankineremap.put(new ResourceLocation("rankine:bamboo_wall"), new ResourceLocation("rankine:bamboo_culms_fence"));
        rankineremap.put(new ResourceLocation("rankine:breccia_bricks_vertical_slab"), new ResourceLocation("rankine:breccia_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:breccia_bricks_wall"), new ResourceLocation("rankine:breccia_wall"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale"), new ResourceLocation("rankine:shale"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_bricks"), new ResourceLocation("rankine:shale_bricks"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_bricks_pressure_plate"), new ResourceLocation("rankine:shale_bricks_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_bricks_slab"), new ResourceLocation("rankine:shale_bricks_slab"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_bricks_stairs"), new ResourceLocation("rankine:shale_bricks_stairs"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_bricks_vertical_slab"), new ResourceLocation("rankine:shale_bricks_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_bricks_wall"), new ResourceLocation("rankine:shale_bricks_wall"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_button"), new ResourceLocation("rankine:shale_button"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_pressure_plate"), new ResourceLocation("rankine:shale_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_slab"), new ResourceLocation("rankine:shale_slab"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_stairs"), new ResourceLocation("rankine:shale_stairs"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_vertical_slab"), new ResourceLocation("rankine:shale_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:carbonaceous_shale_wall"), new ResourceLocation("rankine:shale_wall"));
        rankineremap.put(new ResourceLocation("rankine:cast_iron_alloy"), new ResourceLocation("rankine:cast_iron"));
        rankineremap.put(new ResourceLocation("rankine:checkered_dacite"), new ResourceLocation("rankine:black_dacite_bricks"));
        rankineremap.put(new ResourceLocation("rankine:checkered_dacite_slab"), new ResourceLocation("rankine:black_dacite_bricks_slab"));
        rankineremap.put(new ResourceLocation("rankine:checkered_dacite_stairs"), new ResourceLocation("rankine:black_dacite_bricks_stairs"));
        rankineremap.put(new ResourceLocation("rankine:checkered_dacite_vertical_slab"), new ResourceLocation("rankine:black_dacite_bricks_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:checkered_porphyry"), new ResourceLocation("rankine:red_porphyry_bricks"));
        rankineremap.put(new ResourceLocation("rankine:checkered_porphyry_slab"), new ResourceLocation("rankine:red_porphyry_bricks_slab"));
        rankineremap.put(new ResourceLocation("rankine:checkered_porphyry_stairs"), new ResourceLocation("rankine:red_porphyry_bricks_stairs"));
        rankineremap.put(new ResourceLocation("rankine:checkered_porphyry_vertical_slab"), new ResourceLocation("rankine:red_porphyry_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:halite_ore"), new ResourceLocation("rankine:evaporite"));
        rankineremap.put(new ResourceLocation("rankine:high_beehive_oven_pit"), new ResourceLocation("rankine:beehive_oven_pit"));
        rankineremap.put(new ResourceLocation("rankine:limestone_nodule"), new ResourceLocation("minecraft:gravel"));
        rankineremap.put(new ResourceLocation("rankine:polished_arkose_sandstone"), new ResourceLocation("rankine:polished_arkose"));
        rankineremap.put(new ResourceLocation("rankine:polished_arkose_sandstone_slab"), new ResourceLocation("rankine:polished_arkose_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_arkose_sandstone_stairs"), new ResourceLocation("rankine:polished_arkose_stairs"));
        rankineremap.put(new ResourceLocation("rankine:polished_arkose_sandstone_vertical_slab"), new ResourceLocation("rankine:polished_arkose_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_arkose_sandstone_wall"), new ResourceLocation("rankine:polished_arkose_wall"));
        rankineremap.put(new ResourceLocation("rankine:polished_breccia"), new ResourceLocation("rankine:breccia"));
        rankineremap.put(new ResourceLocation("rankine:polished_breccia_slab"), new ResourceLocation("rankine:breccia_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_breccia_stairs"), new ResourceLocation("rankine:breccia_stairs"));
        rankineremap.put(new ResourceLocation("rankine:polished_breccia_vertical_slab"), new ResourceLocation("rankine:breccia_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_breccia_wall"), new ResourceLocation("rankine:breccia_wall"));
        rankineremap.put(new ResourceLocation("rankine:polished_carbonaceous_shale"), new ResourceLocation("rankine:polished_shale"));
        rankineremap.put(new ResourceLocation("rankine:polished_carbonaceous_shale_slab"), new ResourceLocation("rankine:polished_shale_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_carbonaceous_shale_stairs"), new ResourceLocation("rankine:polished_shale_stairs"));
        rankineremap.put(new ResourceLocation("rankine:polished_carbonaceous_shale_vertical_slab"), new ResourceLocation("rankine:polished_shale_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_carbonaceous_shale_wall"), new ResourceLocation("rankine:polished_shale_wall"));
        rankineremap.put(new ResourceLocation("rankine:polished_pyroxene_gabbro"), new ResourceLocation("rankine:polished_pyroxenite"));
        rankineremap.put(new ResourceLocation("rankine:polished_pyroxene_gabbro_slab"), new ResourceLocation("rankine:polished_pyroxenite_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_pyroxene_gabbro_stairs"), new ResourceLocation("rankine:polished_pyroxenite_stairs"));
        rankineremap.put(new ResourceLocation("rankine:polished_pyroxene_gabbro_vertical_slab"), new ResourceLocation("rankine:polished_pyroxenite_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_pyroxene_gabbro_wall"), new ResourceLocation("rankine:polished_pyroxenite_wall"));
        rankineremap.put(new ResourceLocation("rankine:polished_quartz_sandstone"), new ResourceLocation("rankine:smooth_desert_sandstone"));
        rankineremap.put(new ResourceLocation("rankine:polished_quartz_sandstone_vertical_slab"), new ResourceLocation("rankine:smooth_desert_sandstone_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_skarn"), new ResourceLocation("rankine:skarn"));
        rankineremap.put(new ResourceLocation("rankine:polished_skarn_slab"), new ResourceLocation("rankine:skarn_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_skarn_stairs"), new ResourceLocation("rankine:skarn_stairs"));
        rankineremap.put(new ResourceLocation("rankine:polished_skarn_vertical_slab"), new ResourceLocation("rankine:skarn_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_skarn_wall"), new ResourceLocation("rankine:skarn_wall"));
        rankineremap.put(new ResourceLocation("rankine:polished_tufa_limestone_stairs"), new ResourceLocation("rankine:polished_limestone_stairs"));
        rankineremap.put(new ResourceLocation("rankine:polished_tufa_limestone_vertical_slab"), new ResourceLocation("rankine:polished_limestone_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_tufa_limestone_wall"), new ResourceLocation("rankine:polished_limestone_wall"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro"), new ResourceLocation("rankine:pyroxenite"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_bricks"), new ResourceLocation("rankine:pyroxenite_bricks"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_bricks_slab"), new ResourceLocation("rankine:pyroxenite_bricks_slab"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_bricks_stairs"), new ResourceLocation("rankine:pyroxenite_bricks_stairs"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_bricks_vertical_slab"), new ResourceLocation("rankine:pyroxenite_bricks_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_wall"), new ResourceLocation("rankine:pyroxenite_wall"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_slab"), new ResourceLocation("rankine:pyroxenite_slab"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_stairs"), new ResourceLocation("rankine:pyroxenite_stairs"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone"), new ResourceLocation("rankine:desert_sandstone"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_bricks"), new ResourceLocation("rankine:cut_desert_sandstone"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_bricks_slab"), new ResourceLocation("rankine:cut_desert_sandstone_slab"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_bricks_stairs"), new ResourceLocation("rankine:cut_desert_sandstone_stairs"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_slab"), new ResourceLocation("rankine:desert_sandstone_slab"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_stairs"), new ResourceLocation("rankine:desert_sandstone_stairs"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_vertical_slab"), new ResourceLocation("rankine:desert_sandstone_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_wall"), new ResourceLocation("rankine:desert_sandstone_wall"));
        rankineremap.put(new ResourceLocation("rankine:salt"), new ResourceLocation("rankine:sodium_chloride"));
        rankineremap.put(new ResourceLocation("rankine:salt_block"), new ResourceLocation("rankine:sodium_chloride_block"));
        rankineremap.put(new ResourceLocation("rankine:skarn_bricks"), new ResourceLocation("rankine:skarn"));
        rankineremap.put(new ResourceLocation("rankine:skarn_bricks_stairs"), new ResourceLocation("rankine:skarn_stairs"));
        rankineremap.put(new ResourceLocation("rankine:skarn_bricks_vertical_slab"), new ResourceLocation("rankine:skarn_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:skarn_bricks_wall"), new ResourceLocation("rankine:skarn_wall"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_bricks_pressure_plate"), new ResourceLocation("rankine:limestone_bricks_preessure_plate"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_bricks_slab"), new ResourceLocation("rankine:limestone_bricks_slab"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_bricks_stairs"), new ResourceLocation("rankine:limestone_bricks_stairs"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_bricks_vertical_slab"), new ResourceLocation("rankine:limestone_bricks_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_bricks_wall"), new ResourceLocation("rankine:limestone_bricks_wall"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_button"), new ResourceLocation("rankine:limestone_button"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_pressure_plate"), new ResourceLocation("rankine:limestone_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_slab"), new ResourceLocation("rankine:limestone_slab"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_vertical_slab"), new ResourceLocation("rankine:limestone_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_wall"), new ResourceLocation("rankine:limestone_wall"));
        rankineremap.put(new ResourceLocation("rankine:ultra_high_beehive_oven_pit"), new ResourceLocation("rankine:beehive_oven_pit"));
        rankineremap.put(new ResourceLocation("rankine:kaolinite_block"), new ResourceLocation("rankine:kaolin"));
        rankineremap.put(new ResourceLocation("rankine:kaolinite_ball"), new ResourceLocation("rankine:kaolinite"));
        rankineremap.put(new ResourceLocation("rankine:cast_iron_rod"), new ResourceLocation("kubejs:cast_iron_rod"));
        rankineremap.put(new ResourceLocation("rankine:brass_alloy"), new ResourceLocation("creat:brass_ingot"));
        rankineremap.put(new ResourceLocation("rankine:native_copper_ore"), new ResourceLocation("create:copper_ore"));
        rankineremap.put(new ResourceLocation("rankine:aluminum_bars"), new ResourceLocation("immersiveengineering:alu_fence"));
        rankineremap.put(new ResourceLocation("rankine:steel_alloy"), new ResourceLocation("immersiveengineering:ingot_steel"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone"), new ResourceLocation("minecraft:sandstone"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_bricks"), new ResourceLocation("minecraft:sandstone"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_bricks_slab"), new ResourceLocation("minecraft:sandstone_slab"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_slab"), new ResourceLocation("minecraft:sandstone_slab"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_vertical_slab"), new ResourceLocation("minecraft:sandstone_slab"));
        rankineremap.put(new ResourceLocation("rankine:polished_quartz_sandstone_slab"), new ResourceLocation("minecraft:sandstone_slab"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_bricks_vertical_slab"), new ResourceLocation("minecraft:sandstone_slab"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_stairs"), new ResourceLocation("minecraft:sandstone_stairs"));
        rankineremap.put(new ResourceLocation("rankine:polished_quartz_sandstone_stairs"), new ResourceLocation("minecraft:sandstone_stairs"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_bricks_wall"), new ResourceLocation("minecraft:sandstone_wall"));
        rankineremap.put(new ResourceLocation("rankine:polished_quartz_sandstone_wall"), new ResourceLocation("minecraft:sandstone_wall"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_bricks_wall"), new ResourceLocation("minecraft:sandstone_wall"));
        rankineremap.put(new ResourceLocation("rankine:breccia_button"), new ResourceLocation("minecraft:stone_button"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_button"), new ResourceLocation("minecraft:stone_button"));
        rankineremap.put(new ResourceLocation("rankine:skarn_button"), new ResourceLocation("minecraft:stone_button"));
        rankineremap.put(new ResourceLocation("rankine:arkose_sandstone_bricks_pressure_plate"), new ResourceLocation("minecraft:stone_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:breccia_bricks_pressure_plate"), new ResourceLocation("minecraft:stone_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:breccia_pressure_plate"), new ResourceLocation("minecraft:stone_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_bricks_pressure_plate"), new ResourceLocation("minecraft:stone_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:quartz_sandstone_pressure_plate"), new ResourceLocation("minecraft:stone_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:skarn_bricks_pressure_plate"), new ResourceLocation("minecraft:stone_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:skarn_pressure_plate"), new ResourceLocation("minecraft:stone_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:antimony_ingot"), new ResourceLocation("rankine:antimony"));
        rankineremap.put(new ResourceLocation("rankine:asparagus_root"), new ResourceLocation("rankine:asparagus"));
        rankineremap.put(new ResourceLocation("rankine:bamboo_culms_wall"), new ResourceLocation("rankine:bamboo_culms_fence"));
        rankineremap.put(new ResourceLocation("rankine:native_aluminum_ore"), new ResourceLocation("rankine:bauxite_ore"));
        rankineremap.put(new ResourceLocation("rankine:breccia_bricks"), new ResourceLocation("rankine:breccia"));
        rankineremap.put(new ResourceLocation("rankine:breccia_bricks_slab"), new ResourceLocation("rankine:breccia_slab"));
        rankineremap.put(new ResourceLocation("rankine:breccia_bricks_stairs"), new ResourceLocation("rankine:breccia_stairs"));
        rankineremap.put(new ResourceLocation("rankine:bridgmanite_bricks_pressure_plate"), new ResourceLocation("rankine:bridgmanite_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:bronze_alloy"), new ResourceLocation("rankine:bronze_ingot"));
        rankineremap.put(new ResourceLocation("rankine:dry_mortar"), new ResourceLocation("rankine:cement_mix"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_bricks_pressure_plate"), new ResourceLocation("rankine:gabbro_bricks_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_bricks_wall"), new ResourceLocation("rankine:gabbro_bricks_wall"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_button"), new ResourceLocation("rankine:gabbro_button"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_pressure_plate"), new ResourceLocation("rankine:gabbro_pressure_plate"));
        rankineremap.put(new ResourceLocation("rankine:pyroxene_gabbro_vertical_slab"), new ResourceLocation("rankine:gabbro_vertical_slab"));
        rankineremap.put(new ResourceLocation("rankine:invar_alloy"), new ResourceLocation("rankine:invar_ingot"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone"), new ResourceLocation("rankine:limestone"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_bricks"), new ResourceLocation("rankine:limestone_bricks"));
        rankineremap.put(new ResourceLocation("rankine:tufa_limestone_stairs"), new ResourceLocation("rankine:limestone_bricks_stairs"));
        rankineremap.put(new ResourceLocation("rankine:polished_tufa_limestone"), new ResourceLocation("rankine:polished_limestone"));
        rankineremap.put(new ResourceLocation("rankine:polished_tufa_limestone_slab"), new ResourceLocation("rankine:polished_limestone_slab"));
        rankineremap.put(new ResourceLocation("rankine:skarn_bricks_slab"), new ResourceLocation("rankine:skarn_slab"));
        rankineremap.put(new ResourceLocation("modularrouters:item_router"), new ResourceLocation("immersiveengineering:storage_aluminum"));
    }
}
