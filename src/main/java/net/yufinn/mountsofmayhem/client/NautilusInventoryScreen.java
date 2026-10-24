package net.yufinn.mountsofmayhem.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.yufinn.mountsofmayhem.menu.NautilusInventoryMenu;

public class NautilusInventoryScreen extends AbstractContainerScreen<NautilusInventoryMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/horse.png");

    public NautilusInventoryScreen(NautilusInventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        graphics.blit(TEXTURE, x + 7, y + 17, 18, imageHeight + 54, 18, 18);
        graphics.blit(TEXTURE, x + 7, y + 35, 0, imageHeight + 54, 18, 18);
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, x + 51, y + 60, 17, (float) (x + 51) - mouseX, (float) (y + 25) - mouseY, menu.getNautilus());
    }
}
