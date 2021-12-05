package com.notaflyingoose.goosemod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.notaflyingoose.goosemod.GooseMod;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MixerScreen extends AbstractContainerScreen<MixerMenu> {
    private static final ResourceLocation MIXER_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/gui/container/mixer.png");
    private boolean widthTooNarrow;

    public MixerScreen(MixerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.titleLabelX = 8;
    }

    public void render(PoseStack stack, int width, int height, float decimal) {
        this.renderBackground(stack);
        super.render(stack, width, height, decimal);
        this.renderTooltip(stack, width, height);
    }

    protected void renderBg(PoseStack p_98474_, float p_98475_, int p_98476_, int p_98477_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, MIXER_LOCATION);
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(p_98474_, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    protected boolean isHovering(int p_98462_, int p_98463_, int p_98464_, int p_98465_, double p_98466_, double p_98467_) {
        return (!this.widthTooNarrow) && super.isHovering(p_98462_, p_98463_, p_98464_, p_98465_, p_98466_, p_98467_);
    }

    protected boolean hasClickedOutside(double p_98456_, double p_98457_, int p_98458_, int p_98459_, int p_98460_) {
        return p_98456_ < p_98458_ || p_98457_ < p_98459_ || p_98456_ >= (p_98458_ + this.imageWidth) || p_98457_ >= (p_98459_ + this.imageHeight);
    }

}
