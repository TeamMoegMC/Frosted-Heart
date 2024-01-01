/*
 * Copyright (c) 2022-2024 TeamMoeg
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

package com.teammoeg.frostedheart.research.gui;

import java.util.OptionalDouble;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Matrix4f;

public class FHGuiHelper {
	public static final RenderType BOLD_LINE_TYPE = RenderType.create("fh_line_bold",
			DefaultVertexFormat.POSITION_COLOR, GL11.GL_LINES, 128, RenderStateAccess.getLineState(4));

	// hack to access render state protected members
	public static class RenderStateAccess extends RenderStateShard {
		public static RenderType.CompositeState getLineState(double width) {
			return RenderType.CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(width)))// this is
																											// line
					// width
					.setLayeringState(VIEW_OFFSET_Z_LAYERING).setOutputState(MAIN_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(true);
		}

		public static RenderType.CompositeState getRectState() {
			return RenderType.CompositeState.builder()
					// width
					.setLayeringState(VIEW_OFFSET_Z_LAYERING).setOutputState(MAIN_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(true);
		}

		public RenderStateAccess(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
			super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
		}

	}

	// draw a line from start to end by color, ABSOLUTE POSITION
	public static void drawLine(PoseStack matrixStack, Color4I color, int startX, int startY, int endX, int endY) {
		VertexConsumer vertexBuilderLines = Minecraft.getInstance().renderBuffers().bufferSource()
				.getBuffer(BOLD_LINE_TYPE);
		drawLine(matrixStack.last().pose(), vertexBuilderLines, color, startX, startY, endX, endY);
	}

	// draw a rectangle
	public static void fillGradient(PoseStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
        fillGradient(matrixStack.last().pose(), bufferbuilder, x1, y1, x2, y2, colorFrom, colorTo);
        tessellator.end();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
     }

	private static void fillGradient(Matrix4f matrix, BufferBuilder builder, int x1, int y1, int x2, int y2, int colorB,
			int colorA) {
		float f = (colorA >> 24 & 255) / 255.0F;
		float f1 = (colorA >> 16 & 255) / 255.0F;
		float f2 = (colorA >> 8 & 255) / 255.0F;
		float f3 = (colorA & 255) / 255.0F;
		float f4 = (colorB >> 24 & 255) / 255.0F;
		float f5 = (colorB >> 16 & 255) / 255.0F;
		float f6 = (colorB >> 8 & 255) / 255.0F;
		float f7 = (colorB & 255) / 255.0F;
		builder.vertex(matrix, x2, y2, 0f).color(f1, f2, f3, f).endVertex();
		builder.vertex(matrix, x2, y1, 0f).color(f1, f2, f3, f).endVertex();
		builder.vertex(matrix, x1, y1, 0f).color(f5, f6, f7, f4).endVertex();
		builder.vertex(matrix, x1, y2, 0f).color(f5, f6, f7, f4).endVertex();
	}

	private static void drawLine(Matrix4f mat, VertexConsumer renderBuffer, Color4I color, int startX, int startY,
			int endX, int endY) {
		RenderSystem.enableColorMaterial();
		renderBuffer.vertex(mat, startX, startY, 0F).color(color.redi(), color.greeni(), color.bluei(), color.alphai())
				.endVertex();
		renderBuffer.vertex(mat, endX, endY, 0F).color(color.redi(), color.greeni(), color.bluei(), color.alphai())
				.endVertex();
	}

	private static void drawRect(Matrix4f mat, VertexConsumer renderBuffer, Color4I color, int x, int y, int w, int h) {
		renderBuffer.vertex(mat, x, y, 0F).color(color.redi(), color.greeni(), color.bluei(), color.alphai()).endVertex();
		renderBuffer.vertex(mat, x + w, y, 0F).color(color.redi(), color.greeni(), color.bluei(), color.alphai())
				.endVertex();
		renderBuffer.vertex(mat, x, y + h, 0F).color(color.redi(), color.greeni(), color.bluei(), color.alphai())
				.endVertex();
		renderBuffer.vertex(mat, x + w, y + h, 0F).color(color.redi(), color.greeni(), color.bluei(), color.alphai())
				.endVertex();
	}
}
