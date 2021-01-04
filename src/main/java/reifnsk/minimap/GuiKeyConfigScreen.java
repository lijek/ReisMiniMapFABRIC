package reifnsk.minimap;

import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.render.TextRenderer;

public class GuiKeyConfigScreen extends ScreenBase implements GuiScreenInterface {
    private int top;
    private int bottom;
    private int left;
    private int right;
    private GuiSimpleButton backButton;
    private GuiSimpleButton saveButton;
    private GuiSimpleButton defaultButton;
    private GuiKeyConfigButton edit;

    public void init() {
        int label = this.calcLabelWidth();
        int button = this.calcButtonWidth();
        this.left = (this.width - label - button - 12) / 2;
        this.right = (this.width + label + button + 12) / 2;
        this.top = (this.height - KeyInput.values().length * 10) / 2;
        this.bottom = (this.height + KeyInput.values().length * 10) / 2;
        int y = this.top;
        KeyInput[] arr$ = KeyInput.values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            KeyInput ki = arr$[i$];
            GuiKeyConfigButton gkcb = new GuiKeyConfigButton(this, 0, this.left, y, label, button, ki);
            this.buttons.add(gkcb);
            y += 10;
        }

        int centerX = this.width / 2;
        this.backButton = new GuiSimpleButton(0, centerX - 74, this.bottom + 7, 46, 14, "Back");
        this.buttons.add(this.backButton);
        this.saveButton = new GuiSimpleButton(0, centerX - 23, this.bottom + 7, 46, 14, "Save");
        this.buttons.add(this.saveButton);
        this.defaultButton = new GuiSimpleButton(0, centerX + 28, this.bottom + 7, 46, 14, "Default");
        this.buttons.add(this.defaultButton);
    }

    private int calcLabelWidth() {
        TextRenderer fr = this.minecraft.textRenderer;
        int width = -1;
        KeyInput[] arr$ = KeyInput.values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            KeyInput ki = arr$[i$];
            width = Math.max(width, fr.getTextWidth(ki.name()));
        }

        return width;
    }

    private int calcButtonWidth() {
        TextRenderer fr = this.minecraft.textRenderer;
        int width = 30;
        KeyInput[] arr$ = KeyInput.values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            KeyInput ki = arr$[i$];
            width = Math.max(width, fr.getTextWidth(">" + ki.getKeyName() + "<"));
        }

        return width + 2;
    }

    public void render(int i, int j, float f) {
        String title = "Key Config";
        int titleWidth = this.textManager.getTextWidth(title);
        int titleLeft = this.width - titleWidth >> 1;
        int titleRight = this.width + titleWidth >> 1;
        this.fill(titleLeft - 2, this.top - 22, titleRight + 2, this.top - 8, -1610612736);
        this.drawTextWithShadowCentred(this.textManager, title, this.width / 2, this.top - 19, -1);
        this.fill(this.left - 2, this.top - 2, this.right + 2, this.bottom + 1, -1610612736);
        super.render(i, j, f);
    }

    GuiKeyConfigButton getEditKeyConfig() {
        return this.edit;
    }

    protected void buttonClicked(Button guibutton) {
        if (guibutton instanceof GuiKeyConfigButton) {
            this.edit = (GuiKeyConfigButton)guibutton;
        }

        if (guibutton == this.saveButton) {
            if (KeyInput.saveKeyConfig()) {
                this.minecraft.overlay.addChatMessage("\u00a7E[Rei's Minimap] Keyconfig Saved.");
            } else {
                this.minecraft.overlay.addChatMessage("\u00a7E[Rei's Minimap] Error Keyconfig Saving.");
            }
        }

        if (guibutton == this.defaultButton) {
            KeyInput[] arr$ = KeyInput.values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                KeyInput ki = arr$[i$];
                ki.setDefault();
            }

            this.buttons.clear();
            this.init();
        }

        if (guibutton == this.backButton) {
            this.minecraft.openScreen(new GuiOptionScreen());
        }

    }

    protected void keyTyped(char c, int i) {
        if (this.edit != null) {
            this.edit.getKeyInput().setKey(i);
            this.edit = null;
            this.buttons.clear();
            this.init();
        } else if (i == 1) {
            this.minecraft.openScreen((ScreenBase) null);
        }

    }
}
