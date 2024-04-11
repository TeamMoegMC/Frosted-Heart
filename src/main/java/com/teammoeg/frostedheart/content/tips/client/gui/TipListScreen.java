package com.teammoeg.frostedheart.content.tips.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.content.tips.client.TipElement;
import com.teammoeg.frostedheart.content.tips.client.TipHandler;
import com.teammoeg.frostedheart.content.tips.client.gui.widget.IconButton;
import com.teammoeg.frostedheart.content.tips.client.util.AnimationUtil;
import com.teammoeg.frostedheart.content.tips.client.util.GuiUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;


public class TipListScreen extends Screen {
    private final boolean background;

    public static String select = "";

    private List<String> tipList;
    private TipElement selectEle = null;
    private int GuiHeight = 0;
    private int listHeight = 0;
    private int textHeight = 0;
    private double lastMouseY = 0;
    private double listScroll = 0;
    private double textScroll = 0;
    private double displayListScroll = 0;
    private double displayTextScroll = 0;

    public TipListScreen(boolean background) {
        super(new StringTextComponent(""));
        this.background = background;
    }

    @Override
    public void init() {
        minecraft.keyboardListener.enableRepeatEvents(true);

        this.addButton(new IconButton(0, 0, IconButton.ICON_CROSS, 0xFFC6FCFF, new TranslationTextComponent("tips." + FHMain.MODID + ".gui.close"), (button) -> {
            closeScreen();
        }));
        this.addButton(new IconButton(0, 0, IconButton.ICON_LOCK, 0xFFC6FCFF, new TranslationTextComponent("tips." + FHMain.MODID + ".gui.pin"), (button) -> {
            TipHandler.forceAdd(select, true);
        }));

        tipList = TipHandler.getVisibleUnlocked();

        if (tipList.isEmpty()) {
            tipList.add("empty");
        }

        GuiHeight = (int)(height*0.8F);
        listHeight = tipList.size()*16;
        textHeight = 0;
        setSelect(select);

        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        float fadeIn = AnimationUtil.calcFadeIn(400, "TipListGuiFading", false);
        int BGColor = (int)(fadeIn * (background ? 128 : 77)) << 24;
        int x = width - (int)(width*0.6F*fadeIn);
        int y = height - (int)(height*0.9F*fadeIn);
        int lx = (int)(width*0.99F);
        int ly = (int)(height*0.9F);

        if (background) {
            renderBackground(ms);
        }

        fill(ms, x, y-16, lx, y-2, BGColor);
        fill(ms, lx, y-16, lx+1, y-2, 0xFFC6FCFF);
        fill(ms, x, y, lx, ly, BGColor);
        fill(ms, lx, ly, lx+1, y, 0xFFC6FCFF);
        if (fadeIn == 1.0F && !select.isEmpty()) {
            renderTipContent(ms, lx, y);
        }

        IconButton closeButton = (IconButton)this.buttons.get(0);
        closeButton.setAlpha(fadeIn);
        closeButton.setXY(lx-12, y-14);

        IconButton lockButton = (IconButton)this.buttons.get(1);
        lockButton.setAlpha(fadeIn);
        lockButton.setXY(lx-27, y-14);

        if (fadeIn > 0.5F && !tipList.isEmpty()) {
            double scale = minecraft.getMainWindow().getGuiScaleFactor();
            ms.push();
            ms.translate(0, displayListScroll, 0);
            RenderSystem.enableScissor(0, (int)((int)(height*0.1F)*scale), (int)(width*scale), (int)((GuiHeight +16)*scale));
            renderList(ms, tipList, (int)(width*0.05F), (int)(height*0.1F)-16, mouseX, mouseY);
            RenderSystem.disableScissor();
            ms.pop();
            //TODO widget
            if (listHeight > GuiHeight +16) {
                renderScrollBar(ms, mouseX, mouseY, (int)(width*0.05F)-8, (int)(height*0.1F)-16, 4, GuiHeight +16, listHeight);
            } else {
                setListScroll(0);
            }
        }
        
        displayListScroll = displayListScroll + (listScroll - displayListScroll)*0.1;
        displayTextScroll = displayTextScroll + (textScroll - displayTextScroll)*0.1;
        lastMouseY = mouseY;

        super.render(ms, mouseX, mouseY, partialTicks);
    }

    private void renderList(MatrixStack ms, List<String> list, int x, int y, int mouseX, int mouseY) {
        int BGOutline = -4;

        for (int i = 0; i < list.size(); i++) {
            if (y+i*16+ displayListScroll > height*0.9F || y+i*16+ displayListScroll +32 < height*0.1F) {
                continue; //超出绘制区域
            }
            int BGWidth = (int)(width*0.3);
            float progress = 0;

            if (i == 0) {
                progress = AnimationUtil.calcFadeIn(300, "TipListGuiList" + list.get(i), false);
            } else if (AnimationUtil.getProgress("TipListGuiList" + list.get(i-1)) > 0.075F || y+i*16+ displayListScroll < height*0.1F+16) {
                progress = AnimationUtil.calcFadeIn(300, "TipListGuiList" + list.get(i), false);
            }

            if (progress != 0) {
                int fontColor = MathHelper.clamp((int)(progress * 255), 0x04, 0xFF) << 24 | 0xFFC6FCFF & 0x00FFFFFF;
                int BGAlpha = background ? 128 : 77;
                int BGColor = MathHelper.clamp((int) (progress * BGAlpha), 0x04, 0xFF) << 24;
                float selOffset;

                if (list.get(i).equals(select)) {
                    AnimationUtil.removeAnimation("TipListListSelD" + list.get(i));
                    selOffset = AnimationUtil.calcFadeIn(100, "TipListListSel" + list.get(i), false) * 10;
                    if (selOffset == 0) {
                        AnimationUtil.removeAnimation("TipListSelColor");
                    }

                } else if (GuiUtil.isMouseIn(mouseX, mouseY, x, (int)(y + i*16 + displayListScroll), BGWidth+3, 9-BGOutline)) {
                    AnimationUtil.removeAnimation("TipListListSelD" + list.get(i));
                    selOffset = AnimationUtil.calcFadeIn(100, "TipListListSel" + list.get(i), false) * 10;
                    if (GuiUtil.isLeftClicked()) {
                        setSelect(list.get(i));
                        AnimationUtil.removeAnimation("TipListSelColor");
                    }

                } else {
                    float last = AnimationUtil.getFadeIn("TipListListSel" + list.get(i));
                    float cal = last-(AnimationUtil.calcFadeOut(100, "TipListListSelD" + list.get(i), false));
                    selOffset = Math.max(0, cal*10);
                    if (selOffset == 0) {
                        AnimationUtil.removeAnimation("TipListListSel" + list.get(i));
                        AnimationUtil.removeAnimation("TipListListSelD" + list.get(i));
                    }
                }

                ms.push();
                ms.translate(x*progress + selOffset-BGOutline, y-BGOutline + i*16, 0);
                if (list.get(i).equals(select)) {
                    float selColorP = AnimationUtil.calcFadeIn(200, "TipListSelColor", false);
                    int selColor = MathHelper.clamp((int)(selColorP * BGAlpha), 0x04, 0xFF) << 24 | 0xFFC6FCFF & 0x00FFFFFF;
                    fill(ms, BGOutline, BGOutline, BGWidth, 10, selColor);
                } else {
                    fill(ms, BGOutline, BGOutline, BGWidth, 10, BGColor);
                }
                fill(ms, BGOutline, BGOutline, BGOutline+1, BGOutline + 10-BGOutline, fontColor);

                String text = "tips." + FHMain.MODID + "." + list.get(i) + ".title";
                text = I18n.format(text);
                if (font.getStringWidth(text) > BGWidth) {
                    text = text.substring(0, Math.min(text.length(), BGWidth/6)) + "...";
                }

                if (list.get(i).equals(select)) {
                    font.drawStringWithShadow(ms, text, 0, 0, fontColor);
                } else {
                    font.drawString(ms, text, 0, 0, fontColor);
                }
                ms.pop();
            }
        }
    }

    private void renderTipContent(MatrixStack ms, int x, int y) { //TODO 搜索和分组
        if (selectEle == null || !selectEle.ID.equals(select)) {
            selectEle = TipHandler.getTipEle(select);
        }

        //移除不应该存在的提示
        if (selectEle.hide) {
            TipHandler.removeUnlocked(selectEle.ID);
            tipList.remove(select);
            setSelect("");
            listHeight = tipList.size()*16;
            selectEle = null;
            return;
        }

        float textFading = AnimationUtil.calcFadeIn(200, "TipListTextFading", false);
        int textColor = Math.max((int)(textFading * 255), 0x04) << 24 | selectEle.fontColor & 0x00FFFFFF;
        int boxWidth = (int)(width*0.4F);
        double scale = minecraft.getMainWindow().getGuiScaleFactor();

        ms.push();
        if (font.getStringWidth(selectEle.contents.get(0).getString()) > x-32 - boxWidth) {
            ms.translate(0, displayTextScroll, 0);
            RenderSystem.enableScissor(0, (int)((int)(height*0.1F+4)*scale), (int)(width*scale), (int)((GuiHeight -8)*scale));
            int line = 0;
            for (int i = 0; i < selectEle.contents.size(); i++) {
                line += 1 + GuiUtil.formatAndDraw(selectEle.contents.get(i), ms, font, boxWidth + 4, y+4 + line*12,
                        x-8 - boxWidth, textColor, 12, false);
            }
            textHeight = line*12;

        } else if (selectEle.contents.size() > 1) {
            font.drawText(ms, selectEle.contents.get(0), boxWidth + 4, y - 12, textColor);
            ms.translate(0, displayTextScroll, 0);
            RenderSystem.enableScissor(0, (int)((int)(height*0.1F+4)*scale), (int)(width*scale), (int)((GuiHeight -8)*scale));
            int line = 0;
            for (int i = 1; i < selectEle.contents.size(); i++) {
                line += 1 + GuiUtil.formatAndDraw(selectEle.contents.get(i), ms, font, boxWidth + 4, y+4 + line*12,
                        x-8 - boxWidth, textColor, 12, false);
            }
            textHeight = line*12;

        } else {
            font.drawText(ms, selectEle.contents.get(0), boxWidth + 4, y - 12, textColor);
        }
        RenderSystem.disableScissor();
        ms.pop();

        if (textHeight > GuiHeight && -displayTextScroll < textHeight-GuiHeight-1) {
            float animation = AnimationUtil.calcBounce(1000, "TipListDownArrow", true)*2;
            ms.push();
            ms.translate(width*0.99F-14, height*0.9F-16-animation, 0);
            GuiUtil.renderIcon(ms, IconButton.ICON_DOWN, 0, 0, 0xFFC6FCFF);
            ms.pop();
        }
    }

    private void renderScrollBar(MatrixStack ms, int mouseX, int mouseY, int x, int y, int w, int h, int totalHeight){
        float maxHeight = totalHeight-h;
        int barHeight = (int)Math.max(32, h/(maxHeight+h) * h);
        float barY = (float)((-(displayListScroll-1)/maxHeight)*(h-barHeight));

        if (isDragging() || GuiUtil.isMouseIn(mouseX, mouseY, x, y+(int)barY, w, barHeight) && GuiUtil.isLeftClicked()) {
            setDragging(GuiUtil.isLeftDown());
            if (GuiUtil.isMouseIn(mouseX, mouseY, 0, y, width, h)) {
                setListScroll(listScroll - (mouseY-lastMouseY)*(maxHeight/(h-barHeight)));
            }
        }

        fill(ms, x, y, x+w, y+h, (background ? 128 : 77) << 24);

        ms.push();
        ms.translate(0, barY, 0);
        fill(ms, x, y, x+w, y+barHeight, 0xFFC6FCFF);
        ms.pop();
    }

    private void setSelect(String s) {
        if (!s.isEmpty()) {
            int target = tipList.indexOf(s) * 16;
            if (target >= -16) {
                if (target + listScroll < 0) {
                    setListScroll(listScroll + (-listScroll - target));
                } else if (target + listScroll > GuiHeight) {
                    setListScroll(listScroll + (-listScroll - target + GuiHeight));
                }
            }
        } else {
            setListScroll(listScroll);
        }
        select = s;
        setTextScroll(0);

        IconButton button = (IconButton)this.buttons.get(1);
        button.visible = !select.isEmpty();
    }

    private void setListScroll(double listScroll) {
        this.listScroll = listScroll == 0 ? 0 : MathHelper.clamp(listScroll, -listHeight + GuiHeight +16, 0);
    }
    
    private void setTextScroll(double textScroll) {
        this.textScroll = textScroll == 0 ? 0 : MathHelper.clamp(textScroll, -textHeight + GuiHeight, 0);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (listHeight > GuiHeight +16) {
            if (GuiUtil.isMouseIn((int)mouseX, (int)mouseY, 0, (int)(height*0.1F)-16, (int)(width*0.4F), GuiHeight +16)) {
                setListScroll(listScroll + delta*48);
            }
        }
        if (textHeight > GuiHeight) {
            if (GuiUtil.isMouseIn((int)mouseX, (int)mouseY, (int)(width*0.4F), (int)(height*0.1F), (int)(width*0.59F), GuiHeight)) {
                setTextScroll(textScroll + delta*32);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (tipList.isEmpty()) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        int sel = tipList.indexOf(select);
        switch (keyCode) {
            case 258:
                if (modifiers == 1) {
                    if (sel > 0) {
                        setSelect(tipList.get(sel-1));
                    } else {
                        setSelect(tipList.get(tipList.size()-1));
                    }
                } else {
                    if (sel < tipList.size()-1) {
                        setSelect(tipList.get(sel+1));
                    } else {
                        setSelect(tipList.get(0));
                    }
                }
                return true;
            case 83:
            case 264:
                if (sel < tipList.size()-1) {
                    setSelect(tipList.get(sel+1));
                } else {
                    setSelect(tipList.get(0));
                }
                return true;
            case 87:
            case 265:
                if (sel > 0) {
                    setSelect(tipList.get(sel-1));
                } else {
                    setSelect(tipList.get(tipList.size()-1));
                }
                return true;
            case 69:
                closeScreen();
                return true;
            default:
                return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onClose() {
        AnimationUtil.removeAnimation("TipListSelColor");
        AnimationUtil.removeAnimation("TipListDownArrow");
        AnimationUtil.removeAnimation("TipListGuiFading");
        AnimationUtil.removeAnimation("TipListTextFading");
        tipList.forEach((name) -> {
            AnimationUtil.removeAnimation("TipListGuiList" + name);
            AnimationUtil.removeAnimation("TipListListSel" + name);
            AnimationUtil.removeAnimation("TipListListSelD" + name);
        });

        TipHandler.resetTipAnimation();
    }

    @Override
    public boolean isPauseScreen() {
        return background;
    }
}