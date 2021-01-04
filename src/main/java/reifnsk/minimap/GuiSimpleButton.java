package reifnsk.minimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.render.TextRenderer;

public class GuiSimpleButton extends Button {
    public GuiSimpleButton(int i, int j, int k, int l, int i1, String s) {
        super(i, j, k, l, i1, s);
    }

    public void render(Minecraft minecraft, int i, int j) {
        if (this.visible) {
            TextRenderer fontrenderer = minecraft.textRenderer;
            boolean flag = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
            int color = flag ? -932813210 : -1610612736;
            this.fill(this.x, this.y, this.x + this.width, this.y + this.height, color);
            this.drawTextWithShadowCentred(fontrenderer, this.text, this.x + this.width / 2, this.y + (this.height - 8) / 2, -1);
        }
    }
}
