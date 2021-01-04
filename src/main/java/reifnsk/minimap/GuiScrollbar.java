package reifnsk.minimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL11;

public class GuiScrollbar extends Button {
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    private long repeatStart;
    private long repeatInterval;
    int orientation;
    private float value;
    private float extent;
    private float min;
    private float max;
    private float unitIncrement;
    private float blockIncrement;
    private int draggingPos;
    private float draggingValue;
    private int dragging;
    private long draggingTimer;
    private int minBarSize;

    public GuiScrollbar(int i, int j, int k, int l, int i1)
    {
        super(i, j, k, l, i1, "");
        repeatStart = 0x1dcd6500L;
        repeatInterval = 0x2625a00L;
        value = 0.0F;
        extent = 0.0F;
        min = 0.0F;
        max = 0.0F;
        unitIncrement = 1.0F;
        blockIncrement = 9F;
        minBarSize = 6;
    }

    public void render(Minecraft mc, int i, int j) {
        if (this.value > this.max - this.extent) {
            this.value = this.max - this.extent;
        }

        if (this.value < this.min) {
            this.value = this.min;
        }

        if (this.orientation == 0) {
            this.drawVertical(mc, i, j);
        } else if (this.orientation == 1) {
            this.drawHorizontal(mc, i, j);
        }

    }

    private void drawVertical(Minecraft mc, int mx, int my) {
        if (dragging != 0)
        {
            postRender(mc, mx, my);
        }

        double d = (double)x + (double)width * 0.5D;
        int k = y;
        int l = y + height;
        Tessellator tessellator = Tessellator.INSTANCE;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        boolean flag = (double)mx >= d - 4D && (double)mx <= d + 4D;

        if (flag && my >= k && my <= k + 8 && (dragging == 0 || dragging == 1))
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
        }
        else
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
        }

        tessellator.start();
        tessellator.pos(d, k, 0.0D);
        tessellator.pos(d, k, 0.0D);
        tessellator.pos(d - 4D, k + 8, 0.0D);
        tessellator.pos(d + 4D, k + 8, 0.0D);
        tessellator.draw();

        if (min < max - extent)
        {
            double d1 = height - 20;
            double d3 = extent / (max - min);

            if (d3 * d1 < (double)minBarSize)
            {
                d3 = (double)minBarSize / d1;
            }

            double d5 = (double)(value / (max - min - extent)) * (1.0D - d3);
            double d6 = d5 + d3;
            d5 = (double)k + d5 * d1 + 10D;
            d6 = (double)k + d6 * d1 + 10D;

            if (dragging == 5 || flag && (double)my >= d5 && (double)my <= d6 && dragging == 0)
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
            }
            else
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
            }

            tessellator.start();
            tessellator.pos(d + 4D, d5, 0.0D);
            tessellator.pos(d - 4D, d5, 0.0D);
            tessellator.pos(d - 4D, d6, 0.0D);
            tessellator.pos(d + 4D, d6, 0.0D);
            tessellator.draw();
        }
        else
        {
            double d2 = k + 10;
            double d4 = l - 10;

            if (dragging == 5 || flag && (double)my >= d2 && (double)my <= d4 && dragging == 0)
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
            }
            else
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
            }

            tessellator.start();
            tessellator.pos(d + 4D, d2, 0.0D);
            tessellator.pos(d - 4D, d2, 0.0D);
            tessellator.pos(d - 4D, d4, 0.0D);
            tessellator.pos(d + 4D, d4, 0.0D);
            tessellator.draw();
        }

        if (flag && my >= l - 8 && my <= l && (dragging == 0 || dragging == 2))
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
        }
        else
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
        }

        tessellator.start();
        tessellator.pos(d, l, 0.0D);
        tessellator.pos(d, l, 0.0D);
        tessellator.pos(d + 4D, l - 8, 0.0D);
        tessellator.pos(d - 4D, l - 8, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawHorizontal(Minecraft mc, int mx, int my) {
        if (dragging != 0)
        {
            postRender(mc, mx, my);
        }

        double d = (double)y + (double)height * 0.5D;
        int k = x;
        int l = x + width;
        Tessellator tessellator = Tessellator.INSTANCE;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        boolean flag = (double)my >= d - 4D && (double)my <= d + 4D;

        if (flag && mx >= k && mx <= k + 8 && (dragging == 0 || dragging == 1))
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
        }
        else
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
        }

        tessellator.start();
        tessellator.pos(k, d, 0.0D);
        tessellator.pos(k, d, 0.0D);
        tessellator.pos(k + 8, d + 4D, 0.0D);
        tessellator.pos(k + 8, d - 4D, 0.0D);
        tessellator.draw();

        if (min < max - extent)
        {
            double d1 = width - 20;
            double d3 = extent / (max - min);

            if (d3 * d1 < (double)minBarSize)
            {
                d3 = (double)minBarSize / d1;
            }

            double d5 = (double)(value / (max - min - extent)) * (1.0D - d3);
            double d6 = d5 + d3;
            d5 = (double)k + d5 * d1 + 10D;
            d6 = (double)k + d6 * d1 + 10D;

            if (dragging == 6 || flag && (double)mx >= d5 && (double)mx <= d6 && dragging == 0)
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
            }
            else
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
            }

            tessellator.start();
            tessellator.pos(d5, d - 4D, 0.0D);
            tessellator.pos(d5, d + 4D, 0.0D);
            tessellator.pos(d6, d + 4D, 0.0D);
            tessellator.pos(d6, d - 4D, 0.0D);
            tessellator.draw();
        }
        else
        {
            double d2 = k + 10;
            double d4 = l - 10;

            if (dragging == 6 || flag && (double)mx >= d2 && (double)mx <= d4 && dragging == 0)
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
            }
            else
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
            }

            tessellator.start();
            tessellator.pos(d2, d - 4D, 0.0D);
            tessellator.pos(d2, d + 4D, 0.0D);
            tessellator.pos(d4, d + 4D, 0.0D);
            tessellator.pos(d4, d - 4D, 0.0D);
            tessellator.draw();
        }

        if (flag && mx >= l - 8 && mx <= l && (dragging == 0 || dragging == 2))
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
        }
        else
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
        }

        tessellator.start();
        tessellator.pos(l, d, 0.0D);
        tessellator.pos(l, d, 0.0D);
        tessellator.pos(l - 8, d - 4D, 0.0D);
        tessellator.pos(l - 8, d + 4D, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public boolean isMouseOver(Minecraft mc, int mx, int my) {
        if (super.isMouseOver(mc, mx, my)) {
            if (this.orientation == 0) {
                return this.mousePressedVertical(mc, mx, my);
            } else {
                return this.orientation == 1 ? this.mousePressedHorizontal(mc, mx, my) : false;
            }
        } else {
            return false;
        }
    }

    private boolean mousePressedVertical(Minecraft mc, int mx, int my) {
        double centerX = (double)this.x + (double)this.width * 0.5D;
        int top = this.y;
        int bottom = this.y + this.height;
        if ((double)mx >= centerX - 4.0D && (double)mx <= centerX + 4.0D) {
            if (this.max == this.min) {
                return true;
            } else {
                if (this.dragging == 0) {
                    this.draggingTimer = System.nanoTime() + this.repeatStart;
                }

                if (my < top || my > top + 8 || this.dragging != 0 && this.dragging != 1) {
                    if (my < bottom - 8 || my > bottom || this.dragging != 0 && this.dragging != 2) {
                        double boxsize = (double)(this.height - 20);
                        double barsize = (double)(this.extent / (this.max - this.min));
                        if (barsize * boxsize < 3.0D) {
                            barsize = 3.0D / boxsize;
                        }

                        double minY = (double)(this.value / (this.max - this.min - this.extent)) * (1.0D - barsize);
                        double maxY = minY + barsize;
                        minY = (double)top + minY * boxsize + 10.0D;
                        maxY = (double)top + maxY * boxsize + 10.0D;
                        if ((double)my >= minY || this.dragging != 0 && this.dragging != 3) {
                            if ((double)my > maxY && (this.dragging == 0 || this.dragging == 4)) {
                                this.dragging = 4;
                                this.blockIncrement();
                                return true;
                            } else {
                                if (this.dragging == 0) {
                                    this.dragging = 5;
                                    this.draggingPos = my;
                                    this.draggingValue = this.value;
                                }

                                return true;
                            }
                        } else {
                            this.dragging = 3;
                            this.blockDecrement();
                            return true;
                        }
                    } else {
                        this.dragging = 2;
                        this.unitIncrement();
                        return true;
                    }
                } else {
                    this.dragging = 1;
                    this.unitDecrement();
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    private boolean mousePressedHorizontal(Minecraft mc, int mx, int my) {
        double centerY = (double)this.y + (double)this.height * 0.5D;
        int left = this.x;
        int right = this.x + this.width;
        if ((double)my >= centerY - 4.0D && (double)my <= centerY + 4.0D) {
            if (this.max == this.min) {
                return true;
            } else {
                if (this.dragging == 0) {
                    this.draggingTimer = System.nanoTime() + this.repeatStart;
                }

                if (mx < left || mx > left + 8 || this.dragging != 0 && this.dragging != 1) {
                    if (mx < right - 8 || mx > right || this.dragging != 0 && this.dragging != 2) {
                        double boxsize = (double)(this.width - 20);
                        double barsize = (double)(this.extent / (this.max - this.min));
                        if (barsize * boxsize < 3.0D) {
                            barsize = 3.0D / boxsize;
                        }

                        double minX = (double)(this.value / (this.max - this.min - this.extent)) * (1.0D - barsize);
                        double maxX = minX + barsize;
                        minX = (double)left + minX * boxsize + 10.0D;
                        maxX = (double)left + maxX * boxsize + 10.0D;
                        if ((double)mx >= minX || this.dragging != 0 && this.dragging != 3) {
                            if ((double)mx > maxX && (this.dragging == 0 || this.dragging == 4)) {
                                this.dragging = 4;
                                this.blockIncrement();
                                return true;
                            } else {
                                if (this.dragging == 0) {
                                    this.dragging = 6;
                                    this.draggingPos = mx;
                                    this.draggingValue = this.value;
                                }

                                return true;
                            }
                        } else {
                            this.dragging = 3;
                            this.blockDecrement();
                            return true;
                        }
                    } else {
                        this.dragging = 2;
                        this.unitIncrement();
                        return true;
                    }
                } else {
                    this.dragging = 1;
                    this.unitDecrement();
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    protected void postRender(Minecraft minecraft, int mx, int my) {
        float boxsize;
        float barsize;
        float newValue;
        if (this.dragging == 5) {
            boxsize = (float)(this.height - 20);
            barsize = this.extent / (this.max - this.min);
            if (barsize * boxsize < 3.0F) {
                barsize = 3.0F / boxsize;
            }

            newValue = this.draggingValue + (this.max - this.min - this.extent) / (1.0F - barsize) * (float)(my - this.draggingPos) / boxsize;
            this.value = Math.max(this.min, Math.min(this.max - this.extent, newValue));
        }

        if (this.dragging == 6) {
            boxsize = (float)(this.width - 20);
            barsize = this.extent / (this.max - this.min);
            if (barsize * boxsize < 3.0F) {
                barsize = 3.0F / boxsize;
            }

            newValue = this.draggingValue + (this.max - this.min - this.extent) / (1.0F - barsize) * (float)(mx - this.draggingPos) / boxsize;
            this.value = Math.max(this.min, Math.min(this.max - this.extent, newValue));
        }

        long time = System.nanoTime();
        if (this.draggingTimer < time) {
            this.isMouseOver(minecraft, mx, my);
            this.draggingTimer = time + this.repeatInterval;
        }

    }

    public void mouseReleased(int i, int j) {
        this.dragging = 0;
    }

    public void setValue(float value) {
        if (value < this.min) {
            value = this.min;
        }

        if (value > this.max - this.extent) {
            value = this.max - this.extent;
        }

        this.value = value;
    }

    public float getValue() {
        return this.value;
    }

    public void setMaximum(float max) {
        if (this.min > max) {
            throw new IllegalArgumentException("min > max");
        } else {
            this.max = max;
            this.value = Math.min(this.value, this.max);
        }
    }

    public float getMaximum() {
        return this.max;
    }

    public void setMinimum(float min) {
        if (min > this.max) {
            throw new IllegalArgumentException("min > max");
        } else {
            this.min = min;
            this.value = Math.max(this.value, this.min);
        }
    }

    public float getMinimum() {
        return this.min;
    }

    public void setVisibleAmount(float extent) {
        if (this.max - this.min < extent) {
            throw new IllegalArgumentException("max - min < extent");
        } else {
            this.extent = Math.min(this.max - this.min, extent);
        }
    }

    public float getVisibleAmount() {
        return this.extent;
    }

    public void unitIncrement() {
        this.value = Math.min(this.max - this.extent, this.value + this.unitIncrement);
    }

    public void unitDecrement() {
        this.value = Math.max(this.min, this.value - this.unitIncrement);
    }

    public void blockIncrement() {
        this.value = Math.min(this.max - this.extent, this.value + this.blockIncrement);
    }

    public void blockDecrement() {
        this.value = Math.max(this.min, this.value - this.blockIncrement);
    }
}
