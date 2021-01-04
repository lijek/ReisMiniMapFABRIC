package reifnsk.minimap;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.class_214;
import org.lwjgl.opengl.GL11;

public class GLTextureBufferedImage extends BufferedImage {
    private static final ByteBuffer buffer = class_214.method_744(4194304);
    private static final IntBuffer singleIntBuffer = class_214.method_745(1);
    private static final HashMap<Integer, GLTextureBufferedImage> registerImage = new HashMap();
    private static final Lock lock = new ReentrantLock();
    byte[] data;
    private int register;
    private boolean magFiltering;
    private boolean minFiltering;
    private boolean clampTexture;

    private GLTextureBufferedImage(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied, Hashtable<?, ?> properties) {
        super(cm, raster, isRasterPremultiplied, properties);
        this.data = ((DataBufferByte)raster.getDataBuffer()).getData();
    }

    public static GLTextureBufferedImage create(int w, int h) {
        ColorSpace colorspace1 = ColorSpace.getInstance(1000);
        int[] bits = new int[]{8, 8, 8, 8};
        int[] bandOffsets = new int[]{0, 1, 2, 3};
        ColorModel colorModel = new ComponentColorModel(colorspace1, bits, true, false, 3, 0);
        WritableRaster raster = Raster.createInterleavedRaster(0, w, h, w * 4, 4, bandOffsets, (Point)null);
        return new GLTextureBufferedImage(colorModel, raster, false, (Hashtable)null);
    }

    public int register() {
        lock.lock();

        int var2;
        try {
            if (this.register != 0) {
                this.unregister();
            }

            singleIntBuffer.clear();
            class_214.method_742(singleIntBuffer);
            this.register = singleIntBuffer.get(0);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.register);
            GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10241 /*GL_TEXTURE_MIN_FILTER*/, this.minFiltering ? 9729 /*GL_LINEAR*/ : 9728 /*GL_NEAREST*/);
            GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10240 /*GL_TEXTURE_MAG_FILTER*/, this.magFiltering ? 9729 /*GL_LINEAR*/ : 9728 /*GL_NEAREST*/);
            int clamp = this.clampTexture ? 10496 /*GL_CLAMP*/ : 10497 /*GL_REPEAT*/;
            GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10242 /*GL_TEXTURE_WRAP_S*/, clamp);
            GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10243 /*GL_TEXTURE_WRAP_T*/, clamp);
            buffer.clear();
            buffer.put(this.data);
            buffer.flip();
            GL11.glTexImage2D(3553 /*GL_TEXTURE_2D*/, 0, 6408 /*GL_RGBA*/, this.getWidth(), this.getHeight(), 0, 6408 /*GL_RGBA*/, 5121 /*GL_UNSIGNED_BYTE*/, buffer);
            registerImage.put(this.register, this);
            var2 = this.register;
        } finally {
            lock.unlock();
        }

        return var2;
    }

    public boolean bind() {
        lock.lock();

        boolean var1;
        try {
            if (this.register != 0) {
                GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.register);
                var1 = true;
                return var1;
            }

            var1 = false;
        } finally {
            lock.unlock();
        }

        return var1;
    }

    public void unregister() {
        lock.lock();

        try {
            if (this.register == 0) {
                return;
            }

            singleIntBuffer.clear();
            singleIntBuffer.put(this.register);
            singleIntBuffer.flip();
            this.register = 0;
            GL11.glDeleteTextures(singleIntBuffer);
            registerImage.remove(this.register);
        } finally {
            lock.unlock();
        }

    }

    public static void unregister(int id) {
        lock.lock();

        try {
            GLTextureBufferedImage image = (GLTextureBufferedImage)registerImage.get(id);
            if (image != null) {
                image.unregister();
            }
        } finally {
            lock.unlock();
        }

    }

    public void setMagFilter(boolean b) {
        this.magFiltering = b;
    }

    public void setMinFilter(boolean b) {
        this.minFiltering = b;
    }

    public int getId() {
        return this.register;
    }

    public boolean getMagFilter() {
        return this.magFiltering;
    }

    public boolean getMinFilter() {
        return this.minFiltering;
    }

    public void setClampTexture(boolean b) {
        this.clampTexture = b;
    }

    public boolean isClampTexture() {
        return this.clampTexture;
    }

    public void setRGBA(int x, int y, byte r, byte g, byte b, byte a) {
        int i = (y * this.getWidth() + x) * 4;
        this.data[i++] = r;
        this.data[i++] = g;
        this.data[i++] = b;
        this.data[i] = a;
    }

    public void setRGB(int x, int y, byte r, byte g, byte b) {
        int i = (y * this.getWidth() + x) * 4;
        this.data[i++] = r;
        this.data[i++] = g;
        this.data[i++] = b;
        this.data[i] = -1;
    }

    public void slide(int slideX, int slideY) {
        if (slideX > -this.getWidth() && slideX < this.getWidth()) {
            if (slideY > -this.getHeight() && slideY < this.getHeight()) {
                int slide = (slideX + slideY * this.getWidth()) * 4;
                if (slide != 0) {
                    int i;
                    if (slide < 0) {
                        i = slide;

                        for(int j = this.data.length; i < j; ++i) {
                            this.data[i + slide] = this.data[i];
                        }
                    } else {
                        for(i = this.data.length - slide - 1; i >= 0; --i) {
                            this.data[i + slide] = this.data[i];
                        }
                    }

                }
            }
        }
    }
}
