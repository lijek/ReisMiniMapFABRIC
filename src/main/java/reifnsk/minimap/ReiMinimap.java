package reifnsk.minimap;

import java.awt.Desktop;
import java.awt.Point;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.class_255;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.InGame;
import net.minecraft.client.gui.ingame.ChatMessage;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.screen.ingame.Chat;
import net.minecraft.client.gui.screen.ingame.Death;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.entity.EntityBase;
import net.minecraft.entity.animal.AnimalBase;
import net.minecraft.entity.monster.MonsterBase;
import net.minecraft.entity.monster.Slime;
import net.minecraft.entity.player.ClientPlayer;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.entity.swimming.Squid;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.util.CharacterUtils;
import net.minecraft.util.maths.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import reifnsk.minimap.mixin.ClientPlayNetworkHandlerMixin;
import reifnsk.minimap.mixin.ServerPlayNetworkHandlerMixin;

public class ReiMinimap implements Runnable {
    private static final String MC_B173 = "Beta 1.7.3";
    private static final String MC_B166 = "Beta 1.6.6";
    private static final String MC_B151 = "Beta 1.5_01";
    public static final String MOD_VERSION = "v1.8";
    public static final String MC_VERSION = "Beta 1.7.3";
    public static final String version = String.format("%s [%s]", "v1.8", "Beta 1.7.3");
    public boolean useModloader;
    private static final double renderZ = 1.0D;
    private static final boolean noiseAdded = false;
    private static final float noiseAlpha = 0.1F;
    static final File directory;
    private float[] lightBrightnessTable = this.generateLightBrightnessTable(0.125F);
    private static final int[] updateFrequencys;
    public static final ReiMinimap instance;
    private static final int TEXTURE_SIZE = 256;
    private Minecraft theMinecraft;
    private Tessellator tessellator;
    private Level theWorld;
    private PlayerBase thePlayer;
    private InGame ingameGUI;
    private ScreenScaler ScreenScaler;
    private String errorString;
    private boolean multiplayer;
    private SocketAddress currentServer;
    private String currentLevelName;
    private int currentDimension;
    private int scWidth;
    private int scHeight;
    private GLTextureBufferedImage texture;
    private ChunkCache chunkCache;
    private Thread thread;
    private Lock lock;
    private Condition condition;
    private StripCounter stripCounter;
    private int stripCountMax1;
    private int stripCountMax2;
    private ScreenBase guiScreen;
    private int x;
    private int y;
    private int z;
    private int chunkCoordX;
    private int chunkCoordZ;
    private float sin;
    private float cos;
    private int lastX;
    private int lastY;
    private int lastZ;
    private int skylightSubtracted;
    private boolean isUpdateImage;
    private boolean isCompleteImage;
    private boolean enable;
    private boolean showMenuKey;
    private boolean filtering;
    private int mapPosition;
    private boolean mapTransparency;
    private int lightmap;
    private boolean undulate;
    private boolean transparency;
    private boolean environmentColor;
    private boolean omitHeightCalc;
    private int updateFrequencySetting;
    private boolean threading;
    private int threadPriority;
    private boolean hideSnow;
    private boolean showSlimeChunk;
    private boolean heightmap;
    private boolean showCoordinate;
    private boolean visibleWaypoints;
    private boolean deathPoint;
    private boolean roundmap;
    private boolean fullmap;
    private boolean forceUpdate;
    private long currentTime;
    private long previousTime;
    private int renderType;
    private static final byte[] EMPTY_CHUNK_BYTES;
    private static final class_255 EMPTY_CHUNK;
    private HashMap<Integer, ArrayList<Waypoint>> wayPtsMap;
    private ArrayList<Waypoint> wayPts;
    private int waypointDimension;
    private static final double[] ZOOM_LIST;
    private int defaultZoom;
    private int flagZoom;
    private double targetZoom;
    private double currentZoom;
    private float zoomVisible;
    private boolean chatWelcomed;
    private List<ChatMessage> ChatMessageList;
    private ChatMessage ChatMessageLast;
    private long chatTime;
    private boolean configEntitiesRadar;
    private boolean configEntityPlayer;
    private boolean configEntityAnimal;
    private boolean configEntityMob;
    private boolean configEntitySquid;
    private boolean configEntitySlime;
    private boolean configEntityDirection;
    private boolean allowCavemap;
    private boolean allowEntitiesRadar;
    private boolean allowEntityPlayer;
    private boolean allowEntityAnimal;
    private boolean allowEntityMob;
    private boolean allowEntitySquid;
    private boolean allowEntitySlime;
    private boolean visibleEntitiesRadar;
    private boolean visibleEntityPlayer;
    private boolean visibleEntityAnimal;
    private boolean visibleEntityMob;
    private boolean visibleEntitySquid;
    private boolean visibleEntitySlime;
    static float[] temp;
    private static final Map<String, String> obfascatorFieldMap;

    boolean getAllowCavemap() {
        return this.allowCavemap;
    }

    boolean getAllowEntitiesRadar() {
        return this.allowEntitiesRadar;
    }

    private ReiMinimap() {
        this.tessellator = Tessellator.INSTANCE;
        this.texture = GLTextureBufferedImage.create(256, 256);
        this.chunkCache = new ChunkCache(6);
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
        this.stripCounter = new StripCounter(289);
        this.stripCountMax1 = 0;
        this.stripCountMax2 = 0;
        this.enable = true;
        this.showMenuKey = true;
        this.filtering = true;
        this.mapPosition = 2;
        this.mapTransparency = false;
        this.lightmap = 0;
        this.undulate = true;
        this.transparency = true;
        this.environmentColor = true;
        this.omitHeightCalc = true;
        this.updateFrequencySetting = 2;
        this.threading = false;
        this.threadPriority = 1;
        this.hideSnow = false;
        this.showSlimeChunk = false;
        this.heightmap = true;
        this.showCoordinate = true;
        this.visibleWaypoints = true;
        this.deathPoint = false;
        this.roundmap = false;
        this.fullmap = false;
        this.renderType = 0;
        this.wayPtsMap = new HashMap<>();
        this.wayPts = new ArrayList<>();
        this.defaultZoom = 1;
        this.flagZoom = 1;
        this.targetZoom = 1.0D;
        this.currentZoom = 1.0D;
        this.chatTime = 0L;
        this.configEntitiesRadar = false;
        this.configEntityPlayer = true;
        this.configEntityAnimal = true;
        this.configEntityMob = true;
        this.configEntitySquid = true;
        this.configEntitySlime = true;
        this.configEntityDirection = false;
        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (!directory.isDirectory()) {
            throw new Error();
        } else {
            this.loadOptions();
        }
    }

    public void onTickInGame(Minecraft mc) {
        GL11.glPushAttrib(1048575);
        GL11.glPushClientAttrib(-1);

        try {
            if (mc == null) {
                return;
            }

            if (this.errorString != null) {
                this.ScreenScaler = new ScreenScaler(mc.options, mc.actualWidth, mc.actualHeight);
                mc.textRenderer.drawTextWithShadow(this.errorString, this.ScreenScaler.getScaledWidth() - mc.textRenderer.getTextWidth(this.errorString) - 2, 2, -65536);
                return;
            }

            if (this.theMinecraft == null) {
                this.theMinecraft = mc;
                this.ingameGUI = this.theMinecraft.overlay;
                this.ChatMessageList = (List<ChatMessage>)getField(this.ingameGUI, "chatMessageList");
                this.ChatMessageList = (this.ChatMessageList == null ? new ArrayList() : this.ChatMessageList);
            }

            this.thePlayer = this.theMinecraft.player;
            int y;
            int scale;
            int x;
            if (this.theWorld != this.theMinecraft.level) {
                this.theWorld = this.theMinecraft.level;
                this.multiplayer = this.thePlayer instanceof ClientPlayer;
                if (this.theWorld != null) {
                    Environment.setWorldSeed(this.theWorld.getSeed());
                    boolean changeWorld;
                    String levelName;
                    if (this.multiplayer) {
                        levelName = null;
                        //SocketAddress addr = (SocketAddress)getFields(this.thePlayer, "sendQueue", "netManager", "remoteSocketAddress"); original code that didn't work
                        SocketAddress addr = ((ServerPlayNetworkHandlerMixin)((ClientPlayNetworkHandlerMixin)((ClientPlayer)this.thePlayer).networkHandler).getNetHandler()).getSocketAddress();
                        //giant accessor castings :D, I know that it looks very bad
                        if (addr == null) {
                            this.errorString = "[Rei's Minimap] ERROR: SMP ADDRESS ACQUISITION FAILURE";
                            throw new MinimapException(this.errorString);
                        }

                        changeWorld = this.currentServer != addr;
                        if (changeWorld) {
                            Matcher matcher = Pattern.compile("(.*)/(.*):([0-9]+)").matcher(addr.toString());
                            if (matcher.matches()) {
                                levelName = matcher.group(1);
                                if (levelName.isEmpty()) {
                                    levelName = matcher.group(2);
                                }

                                if (!matcher.group(3).equals("25565")) {
                                    levelName = levelName + "[" + matcher.group(3) + "]";
                                }
                            }

                            char[] arr$ = CharacterUtils.field_299;
                            y = arr$.length;

                            for(scale = 0; scale < y; ++scale) {
                                char c = arr$[scale];
                                levelName = levelName.replace(c, '_');
                            }

                            this.currentLevelName = levelName;
                            this.currentServer = addr;
                        }
                    } else {
                        levelName = this.theWorld.getProperties().getName();//(String)getFields(this.theWorld, "worldInfo", "levelName");
                        if (levelName == null) {
                            this.errorString = "[Rei's Minimap] ERROR: WORLD_NAME ACQUISITION FAILURE";
                            throw new MinimapException(this.errorString);
                        }

                        char[] arr$ = CharacterUtils.field_299;
                        x = arr$.length;

                        for(x = 0; x < x; ++x) {
                            char c = arr$[x];
                            levelName = levelName.replace(c, '_');
                        }

                        changeWorld = !levelName.equals(this.currentLevelName) || this.currentServer != null;
                        if (changeWorld) {
                            this.currentLevelName = levelName;
                            changeWorld = true;
                        }

                        this.currentServer = null;
                    }

                    Integer dimension = this.thePlayer.dimensionId;//(Integer)getField(this.thePlayer, "dimension");
                    if (dimension == null) {
                        this.errorString = "[Rei's Minimap] ERROR: DIMENSION ACQUISITION FAILURE";
                        throw new MinimapException(this.errorString);
                    }

                    this.currentDimension = dimension;
                    this.waypointDimension = this.currentDimension;
                    if (changeWorld) {
                        this.chatTime = System.currentTimeMillis();
                        this.chatWelcomed = !this.multiplayer;
                        this.allowCavemap = !this.multiplayer;
                        this.allowEntitiesRadar = !this.multiplayer;
                        this.allowEntityPlayer = !this.multiplayer;
                        this.allowEntityAnimal = !this.multiplayer;
                        this.allowEntityMob = !this.multiplayer;
                        this.allowEntitySlime = !this.multiplayer;
                        this.allowEntitySquid = !this.multiplayer;
                        this.loadWaypoints();
                    }

                    this.wayPts = (ArrayList<Waypoint>)this.wayPtsMap.get(this.waypointDimension);
                    if (this.wayPts == null) {
                        this.wayPts = new ArrayList<>();
                        this.wayPtsMap.put(this.waypointDimension, this.wayPts);
                    }
                }

                this.stripCounter.reset();
            }

            if (!this.chatWelcomed && System.currentTimeMillis() < this.chatTime + 10000L) {
                Iterator<ChatMessage> i$ = this.ChatMessageList.iterator();

                while(i$.hasNext()) {
                    ChatMessage cl = i$.next();
                    if (cl == null || this.ChatMessageLast == cl) {
                        break;
                    }

                    Matcher matcher = Pattern.compile("\u00a70\u00a70((?:\u00a7[1-9a-d])+)\u00a7e\u00a7f").matcher(cl.field_2469);

                    while(matcher.find()) {
                        this.chatWelcomed = true;
                        char[] arr$ = matcher.group(1).toCharArray();
                        x = arr$.length;

                        for(y = 0; y < x; ++y) {
                            char ch = arr$[y];
                            switch(ch) {
                            case '1':
                                this.allowCavemap = true;
                                break;
                            case '2':
                                this.allowEntityPlayer = true;
                                break;
                            case '3':
                                this.allowEntityAnimal = true;
                                break;
                            case '4':
                                this.allowEntityMob = true;
                                break;
                            case '5':
                                this.allowEntitySlime = true;
                                break;
                            case '6':
                                this.allowEntitySquid = true;
                            }
                        }
                    }
                }

                this.ChatMessageLast = this.ChatMessageList.isEmpty() ? null : (ChatMessage)this.ChatMessageList.get(0);
                if (this.chatWelcomed) {
                    this.allowEntitiesRadar = this.allowEntityPlayer || this.allowEntityAnimal || this.allowEntityMob || this.allowEntitySlime || this.allowEntitySquid;
                    if (this.allowCavemap) {
                        this.chatInfo("\u00a7E[Rei's Minimap] enabled: cavemapping.");
                    }

                    if (this.allowEntitiesRadar) {
                        StringBuilder sb = new StringBuilder("\u00a7E[Rei's Minimap] enabled: entities radar (");
                        if (this.allowEntityPlayer) {
                            sb.append("Player, ");
                        }

                        if (this.allowEntityAnimal) {
                            sb.append("Animal, ");
                        }

                        if (this.allowEntityMob) {
                            sb.append("Mob, ");
                        }

                        if (this.allowEntitySlime) {
                            sb.append("Slime, ");
                        }

                        if (this.allowEntitySquid) {
                            sb.append("Squid, ");
                        }

                        sb.setLength(sb.length() - 2);
                        sb.append(")");
                        this.chatInfo(sb.toString());
                    }
                }
            } else {
                this.chatWelcomed = true;
            }

            this.visibleEntitiesRadar = this.allowEntitiesRadar && this.configEntitiesRadar;
            this.visibleEntityPlayer = this.allowEntityPlayer && this.configEntityPlayer;
            this.visibleEntityAnimal = this.allowEntityAnimal && this.configEntityAnimal;
            this.visibleEntityMob = this.allowEntityMob && this.configEntityMob;
            this.visibleEntitySlime = this.allowEntitySlime && this.configEntitySlime;
            this.visibleEntitySquid = this.allowEntitySquid && this.configEntitySquid;
            int displayWidth = this.theMinecraft.actualWidth;
            int displayHeight = this.theMinecraft.actualHeight;
            this.ScreenScaler = new ScreenScaler(this.theMinecraft.options, displayWidth, displayHeight);
            this.scWidth = this.ScreenScaler.getScaledWidth();
            this.scHeight = this.ScreenScaler.getScaledHeight();
            KeyInput.update();
            if (mc.currentScreen != null) {
                if (this.fullmap) {
                    this.fullmap = false;
                    this.forceUpdate = true;
                    this.stripCounter.reset();
                }
            } else {
                if (!this.fullmap) {
                    if (KeyInput.TOGGLE_ZOOM.isKeyPush()) {
                        if (Keyboard.isKeyDown(this.theMinecraft.options.sneakKey.key)) {
                            this.flagZoom = (this.flagZoom == 0 ? ZOOM_LIST.length : this.flagZoom) - 1;
                        } else {
                            this.flagZoom = (this.flagZoom + 1) % ZOOM_LIST.length;
                        }
                    } else if (KeyInput.ZOOM_IN.isKeyPush() && this.flagZoom < ZOOM_LIST.length - 1) {
                        ++this.flagZoom;
                    } else if (KeyInput.ZOOM_OUT.isKeyPush() && this.flagZoom > 0) {
                        --this.flagZoom;
                    }

                    this.targetZoom = ZOOM_LIST[this.flagZoom];
                }

                if (KeyInput.TOGGLE_ENABLE.isKeyPush()) {
                    this.enable = !this.enable;
                    this.stripCounter.reset();
                    this.forceUpdate = true;
                }

                if (this.allowCavemap && KeyInput.TOGGLE_CAVE_MAP.isKeyPush()) {
                    this.renderType = (this.renderType + 1) % 2;
                    this.stripCounter.reset();
                    this.forceUpdate = true;
                }

                if (KeyInput.TOGGLE_WAYPOINTS.isKeyPush()) {
                    this.visibleWaypoints = !this.visibleWaypoints;
                }

                if (KeyInput.TOGGLE_LARGE_MAP.isKeyPush()) {
                    this.fullmap = !this.fullmap;
                    this.forceUpdate = true;
                    this.stripCounter.reset();
                    if (this.threading) {
                        this.lock.lock();

                        try {
                            this.stripCounter.reset();
                            this.mapCalc(false);
                        } finally {
                            this.lock.unlock();
                        }
                    }
                }

                if (this.allowEntitiesRadar && KeyInput.TOGGLE_ENTITIES_RADAR.isKeyPush()) {
                    this.configEntitiesRadar = !this.configEntitiesRadar;
                }

                if (KeyInput.SET_WAYPOINT.isKeyPushUp()) {
                    mc.openScreen(new GuiWaypointEditorScreen(mc, (Waypoint)null));
                }

                if (KeyInput.MENU_KEY.isKeyPush()) {
                    mc.openScreen(new GuiOptionScreen());
                }
            }

            float scalef;
            if (this.deathPoint && this.theMinecraft.currentScreen instanceof Death && !(this.guiScreen instanceof Death)) {
                String name = "Death Point";
                x = MathHelper.floor(this.thePlayer.x);
                x = MathHelper.floor(this.thePlayer.y);
                y = MathHelper.floor(this.thePlayer.z);
                Random rng = new Random();
                float r = rng.nextFloat();
                scalef = rng.nextFloat();
                float b = rng.nextFloat();
                boolean contains = false;
                Iterator<Waypoint> i$ = this.wayPts.iterator();

                while(true) {
                    if (i$.hasNext()) {
                        Waypoint wp = i$.next();
                        if (wp.type != 1 || wp.x != x || wp.y != x || wp.z != y || !wp.enable) {
                            continue;
                        }

                        contains = true;
                    }

                    if (!contains) {
                        this.wayPts.add(new Waypoint(name, x, x, y, true, r, scalef, b, 1));
                        this.saveWaypoints();
                    }
                    break;
                }
            }

            this.guiScreen = this.theMinecraft.currentScreen;
            if (!this.enable || !checkGuiScreen(mc.currentScreen)) {
                return;
            }

            if (this.threading) {
                if (this.thread == null || !this.thread.isAlive()) {
                    this.thread = new Thread(this);
                    this.thread.setPriority(3 + this.threadPriority);
                    this.thread.setDaemon(true);
                    this.thread.start();
                }
            } else {
                this.mapCalc(true);
            }

            if (this.lock.tryLock()) {
                try {
                    if (this.isUpdateImage) {
                        this.isUpdateImage = false;
                        this.texture.setMinFilter(this.filtering);
                        this.texture.setMagFilter(this.filtering);
                        this.texture.setClampTexture(true);
                        this.texture.register();
                    }

                    this.condition.signal();
                } finally {
                    this.lock.unlock();
                }
            }

            this.currentTime = System.nanoTime();
            double elapseTime = (double)(this.currentTime - this.previousTime) * 1.0E-9D;
            this.zoomVisible = (float)((double)this.zoomVisible - elapseTime);
            if (this.currentZoom != this.targetZoom) {
                double d = Math.max(0.0D, Math.min(1.0D, elapseTime * 4.0D));
                this.currentZoom += (this.targetZoom - this.currentZoom) * d;
                if (Math.abs(this.currentZoom - this.targetZoom) < 5.0E-4D) {
                    this.currentZoom = this.targetZoom;
                }

                this.zoomVisible = 3.0F;
            }

            this.previousTime = this.currentTime;
            if (this.texture.getId() != 0) {
                switch(this.mapPosition) {
                case 0:
                    x = 37;
                    y = 37;
                    break;
                case 1:
                    x = 37;
                    y = this.scHeight - 37;
                    scale = this.ScreenScaler.scale + 1 >> 1;
                    y -= scale * ((this.showMenuKey | this.showCoordinate ? 2 : 0) + (this.showMenuKey ? 9 : 0) + (this.showCoordinate ? 18 : 0)) / this.ScreenScaler.scale;
                    break;
                case 2:
                default:
                    x = this.scWidth - 37;
                    y = 37;
                    break;
                case 3:
                    x = this.scWidth - 37;
                    y = this.scHeight - 37;
                    scale = this.ScreenScaler.scale + 1 >> 1;
                    y -= scale * ((this.showMenuKey | this.showCoordinate ? 2 : 0) + (this.showMenuKey ? 9 : 0) + (this.showCoordinate ? 18 : 0)) / this.ScreenScaler.scale;
                }

                if (this.fullmap) {
                    this.renderFullMap(x, y);
                } else if (this.roundmap) {
                    this.renderRoundMap(x, y);
                } else {
                    this.renderSquareMap(x, y);
                }

                int temp = this.ScreenScaler.scale + 1 >> 1;
                scalef = 1.0F / (float)this.ScreenScaler.scale * (float)temp;
                GL11.glPushMatrix();
                GL11.glScalef(scalef, scalef, 1.0F);
                GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
                GL11.glDepthMask(false);
                int alpha = (int)(this.zoomVisible * 255.0F);
                String str;
                int width;
                if (!this.fullmap && alpha > 0) {
                    str = String.format("%2.2fx", this.currentZoom);
                    width = mc.textRenderer.getTextWidth(str);
                    if (alpha > 255) {
                        alpha = 255;
                    }

                    int _x = (int)((float)(x + 30) / scalef - (float)width);
                    int _y = (int)((float)(y + 26) / scalef);
                    int argb = alpha << 24 | 16777215;
                    mc.textRenderer.drawTextWithShadow(str, _x, _y, argb);
                }

                if (this.showCoordinate) {
                    int posX = MathHelper.floor(this.thePlayer.x);
                    int posY = MathHelper.floor(this.thePlayer.boundingBox.minY);
                    int posZ = MathHelper.floor(this.thePlayer.z);
                    float _x = (this.fullmap ? (this.scWidth * 0.5F) : x) / scalef;
                    float _y = (this.fullmap ? (this.scHeight * 0.5F + 16.0F) : (y + 32)) / scalef;
                    str = String.format("%+d, %+d", new Object[] { Integer.valueOf(posX), Integer.valueOf(posZ) });
                    float width1 = mc.textRenderer.getTextWidth(str) * 0.5F;
                    mc.textRenderer.drawTextWithShadow(str, (int)(_x - width1), (int)(_y + 2.0F), 16777215);
                    str = Integer.toString(posY);
                    width1 = mc.textRenderer.getTextWidth(str) * 0.5F;
                    mc.textRenderer.drawTextWithShadow(str, (int)(_x - width1), (int)(_y + 11.0F), 16777215);
                }

                if (this.showMenuKey && !this.fullmap) {
                    str = String.format("Menu: %s key", KeyInput.MENU_KEY.getKeyName());
                    width = this.theMinecraft.textRenderer.getTextWidth(str);
                    int _x = (int)((float)(x + 32) / scalef - (float)width);
                    int _y = (int)((float)(y + 32) / scalef);
                    this.theMinecraft.textRenderer.drawTextWithShadow(str, _x, _y + (this.showCoordinate ? 20 : 2), -1);
                }

                GL11.glPopMatrix();
                GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
                GL11.glDepthMask(true);
            }
        } catch (MinimapException var34) {
            var34.printStackTrace();
        } finally {
            GL11.glPopClientAttrib();
            GL11.glPopAttrib();
        }

        Thread.yield();
    }

    public void run() {
        if (this.theMinecraft != null) {
            Thread currentThread = Thread.currentThread();

            while(true) {
                while(!this.enable || currentThread != this.thread || !this.threading) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var20) {
                        return;
                    }

                    this.lock.lock();

                    label194: {
                        try {
                            this.condition.await();
                            break label194;
                        } catch (InterruptedException var21) {
                        } finally {
                            this.lock.unlock();
                        }

                        return;
                    }

                    if (currentThread != this.thread) {
                        return;
                    }
                }

                try {
                    Thread.sleep((long)(updateFrequencys[updateFrequencys.length - this.updateFrequencySetting - 1] * 2));
                } catch (InterruptedException var19) {
                    return;
                }

                this.lock.lock();

                try {
                    this.mapCalc(false);
                    if (this.isCompleteImage || this.isUpdateImage) {
                        this.condition.await();
                    }
                    continue;
                } catch (InterruptedException var23) {
                } catch (Exception var24) {
                    continue;
                } finally {
                    this.lock.unlock();
                }

                return;
            }
        }
    }

    private void startDrawingQuads() {
        this.tessellator.start();
    }

    private void draw() {
        this.tessellator.draw();
    }

    private void addVertexWithUV(double x, double y, double z, double u, double v) {
        this.tessellator.vertex(x, y, z, u, v);
    }

    private void mapCalc(boolean strip) {
        if (this.theWorld != null && this.thePlayer != null) {
            double d;
            if (this.stripCounter.count() == 0) {
                this.x = MathHelper.floor(this.thePlayer.x);
                this.y = MathHelper.floor(this.thePlayer.y);
                this.z = MathHelper.floor(this.thePlayer.z);
                this.chunkCoordX = this.thePlayer.chunkX;
                this.chunkCoordZ = this.thePlayer.chunkZ;
                this.skylightSubtracted = this.theWorld.field_202;
                d = Math.toRadians(this.roundmap && !this.fullmap ? (double)(45.0F - this.thePlayer.yaw) : -45.0D);
                this.sin = (float)Math.sin(d);
                this.cos = (float)Math.cos(d);
            }

            if (this.fullmap) {
                this.stripCountMax1 = 289;
                this.stripCountMax2 = 289;
            } else {
                d = Math.ceil(4.0D / this.currentZoom) * 2.0D + 1.0D;
                this.stripCountMax1 = (int)(d * d);
                d = Math.ceil(4.0D / this.targetZoom) * 2.0D + 1.0D;
                this.stripCountMax2 = (int)(d * d);
            }

            if (this.renderType == 1 && this.allowCavemap) {
                if (!this.forceUpdate && strip) {
                    this.caveCalcStrip();
                } else {
                    this.caveCalc();
                }
            } else if (!this.forceUpdate && strip) {
                this.surfaceCalcStrip();
            } else {
                this.surfaceCalc();
            }

            if (this.isCompleteImage) {
                this.forceUpdate = false;
                this.isCompleteImage = false;
                this.stripCounter.reset();
                this.lastX = this.x;
                this.lastY = this.y;
                this.lastZ = this.z;
            }

        }
    }

    private void surfaceCalc() {
        int limit = Math.max(this.stripCountMax1, this.stripCountMax2);

        while(this.stripCounter.count() < limit) {
            Point point = this.stripCounter.next();
            Chunk chunk = this.chunkCache.get(this.theWorld, this.chunkCoordX + point.x, this.chunkCoordZ + point.y);
            this.surfaceCalc(chunk);
        }

        this.isUpdateImage = this.stripCounter.count() >= this.stripCountMax1;
        this.isCompleteImage = this.isUpdateImage && this.stripCounter.count() >= this.stripCountMax2;
    }

    private void surfaceCalcStrip() {
        int limit = Math.max(this.stripCountMax1, this.stripCountMax2);
        int limit2 = updateFrequencys[this.updateFrequencySetting];

        for(int i = 0; i < limit2 && this.stripCounter.count() < limit; ++i) {
            Point point = this.stripCounter.next();
            Chunk chunk = this.chunkCache.get(this.theWorld, this.chunkCoordX + point.x, this.chunkCoordZ + point.y);
            this.surfaceCalc(chunk);
        }

        this.isUpdateImage = this.stripCounter.count() >= this.stripCountMax1;
        this.isCompleteImage = this.isUpdateImage && this.stripCounter.count() >= this.stripCountMax2;
    }

    private void surfaceCalc(Chunk chunk) {
        if (chunk != null && !(chunk instanceof class_255)) {
            int offsetX = 128 + chunk.x * 16 - this.x;
            int offsetZ = 128 + chunk.z * 16 - this.z;
            boolean slime = this.showSlimeChunk && this.currentDimension == 0 && this.chunkCache.isSlimeSpawn(chunk.x, chunk.z);
            PixelColor pixel = new PixelColor(this.transparency);
            Chunk chunkMinusX = null;
            Chunk chunkPlusX = null;
            Chunk chunkMinusZ = null;
            Chunk chunkPlusZ = null;
            Chunk cmx = null;
            Chunk cpx = null;
            Chunk cmz = null;
            Chunk cpz = null;
            if (this.undulate) {
                chunkMinusZ = this.getChunk(chunk.level, chunk.x, chunk.z - 1);
                chunkPlusZ = this.getChunk(chunk.level, chunk.x, chunk.z + 1);
                chunkMinusX = this.getChunk(chunk.level, chunk.x - 1, chunk.z);
                chunkPlusX = this.getChunk(chunk.level, chunk.x + 1, chunk.z);
            }

            for(int z = 0; z < 16; ++z) {
                int zCoord = offsetZ + z;
                if (zCoord >= 0) {
                    if (zCoord >= 256) {
                        break;
                    }

                    if (this.undulate) {
                        cmz = z == 0 ? chunkMinusZ : chunk;
                        cpz = z == 15 ? chunkPlusZ : chunk;
                    }

                    for(int x = 0; x < 16; ++x) {
                        int xCoord = offsetX + x;
                        if (xCoord >= 0) {
                            if (xCoord >= 256) {
                                break;
                            }

                            pixel.clear();
                            int height = !this.omitHeightCalc && !this.heightmap && !this.undulate ? 127 : Math.min(127, chunk.getHeight(x, z));
                            int y = this.omitHeightCalc ? height : 127;
                            this.surfaceCalc(chunk, x, y, z, pixel, (TintType)null);
                            float factor;
                            if (this.heightmap) {
                                factor = this.undulate ? 0.25F : 0.6F;
                                double d1 = (double)(height - this.y);
                                float d = (float)Math.log10(Math.abs(d1) * 0.125D + 1.0D) * factor;
                                if (d1 >= 0.0D) {
                                    pixel.red += d * (1.0F - pixel.red);
                                    pixel.green += d * (1.0F - pixel.green);
                                    pixel.blue += d * (1.0F - pixel.blue);
                                } else {
                                    pixel.red -= Math.abs(d) * pixel.red;
                                    pixel.green -= Math.abs(d) * pixel.green;
                                    pixel.blue -= Math.abs(d) * pixel.blue;
                                }
                            }

                            factor = 1.0F;
                            if (this.undulate) {
                                cmx = x == 0 ? chunkMinusX : chunk;
                                cpx = x == 15 ? chunkPlusX : chunk;
                                int mx = cmx.getHeight(x - 1 & 15, z);
                                int px = cpx.getHeight(x + 1 & 15, z);
                                int mz = cmz.getHeight(x, z - 1 & 15);
                                int pz = cpz.getHeight(x, z + 1 & 15);
                                factor += Math.max(-4.0F, Math.min(3.0F, (float)(mx - px) * this.sin + (float)(mz - pz) * this.cos)) * 0.14142136F * 0.8F;
                            }

                            if (slime) {
                                pixel.red = (float)((double)pixel.red * 1.2D);
                                pixel.green = (float)((double)pixel.green * 0.5D);
                                pixel.blue = (float)((double)pixel.blue * 0.5D);
                            }

                            byte red = ftob(pixel.red * factor);
                            byte green = ftob(pixel.green * factor);
                            byte blue = ftob(pixel.blue * factor);
                            if (this.transparency) {
                                this.texture.setRGBA(xCoord, zCoord, red, green, blue, ftob(pixel.alpha));
                            } else {
                                this.texture.setRGB(xCoord, zCoord, red, green, blue);
                            }
                        }
                    }
                }
            }

        }
    }

    private static final byte ftob(float f) {
        return (byte)Math.max(0, Math.min(255, (int)(f * 255.0F)));
    }

    private void surfaceCalc(Chunk chunk, int x, int y, int z, PixelColor pixel, TintType tintType) {
        int blockID = chunk.getTileId(x, y, z);
        if (blockID == 0 || this.hideSnow && blockID == 78) {
            if (y > 0) {
                this.surfaceCalc(chunk, x, y - 1, z, pixel, (TintType)null);
            }

        } else {
            int metadata = BlockColor.useMetadata(blockID) ? chunk.method_875(x, y, z) : 0;
            BlockColor color = BlockColor.getBlockColor(blockID, metadata);
            if (this.transparency) {
                if (color.alpha < 1.0F && y > 0) {
                    this.surfaceCalc(chunk, x, y - 1, z, pixel, color.tintType);
                    if (color.alpha == 0.0F) {
                        return;
                    }
                }
            } else if (color.alpha == 0.0F && y > 0) {
                this.surfaceCalc(chunk, x, y - 1, z, pixel, color.tintType);
                return;
            }

            int lightValue;
            switch(this.lightmap) {
            case 1:
                lightValue = y < 127 ? chunk.method_880(x, y + 1, z, 0) : 15;
                break;
            case 2:
                lightValue = y < 127 ? chunk.method_880(x, y + 1, z, 11) : 4;
                break;
            case 3:
                lightValue = 15;
                break;
            default:
                this.lightmap = 0;
            case 0:
                lightValue = y < 127 ? chunk.method_880(x, y + 1, z, this.skylightSubtracted) : 15 - this.skylightSubtracted;
            }

            float lightBrightness = this.lightBrightnessTable[lightValue];
            if (this.environmentColor) {
                int argb;
                switch(color.tintType) {
                case GRASS:
                    argb = Environment.getEnvironment(chunk, x, z).getGrassColor();
                    pixel.composite(color.alpha, argb, lightBrightness * 0.6F);
                    return;
                case FOLIAGE:
                    argb = Environment.getEnvironment(chunk, x, z).getFoliageColor();
                    pixel.composite(color.alpha, argb, lightBrightness * 0.5F);
                    return;
                case PINE:
                    argb = Environment.getEnvironment(chunk, x, z).getFoliageColorPine();
                    pixel.composite(color.alpha, argb, lightBrightness * 0.5F);
                    return;
                case BIRCH:
                    argb = Environment.getEnvironment(chunk, x, z).getFoliageColorBirch();
                    pixel.composite(color.alpha, argb, lightBrightness * 0.5F);
                    return;
                }
            }

            if (color.tintType != TintType.WATER || tintType != TintType.WATER) {
                if (color.tintType != TintType.GLASS || tintType != TintType.GLASS) {
                    if (color.tintType == TintType.REDSTONE) {
                        float level = (float)metadata * 0.06666667F;
                        float r = metadata == 0 ? 0.3F : level * 0.6F + 0.4F;
                        float g = Math.max(0.0F, level * level * 0.7F - 0.5F);
                        float b = 0.0F;
                        float a = color.alpha;
                        pixel.composite(a, r, g, b, lightBrightness);
                    } else {
                        pixel.composite(color.alpha, color.red, color.green, color.blue, lightBrightness);
                    }
                }
            }
        }
    }

    private void caveCalc() {
        int limit = Math.max(this.stripCountMax1, this.stripCountMax2);

        while(this.stripCounter.count() < limit) {
            Point point = this.stripCounter.next();
            Chunk chunk = this.chunkCache.get(this.theWorld, this.chunkCoordX + point.x, this.chunkCoordZ + point.y);
            this.caveCalc(chunk);
        }

        this.isUpdateImage = this.stripCounter.count() >= this.stripCountMax1;
        this.isCompleteImage = this.isUpdateImage && this.stripCounter.count() >= this.stripCountMax2;
    }

    private void caveCalcStrip() {
        int limit = Math.max(this.stripCountMax1, this.stripCountMax2);
        int limit2 = updateFrequencys[this.updateFrequencySetting];

        for(int i = 0; i < limit2 && this.stripCounter.count() < limit; ++i) {
            Point point = this.stripCounter.next();
            Chunk chunk = this.chunkCache.get(this.theWorld, this.chunkCoordX + point.x, this.chunkCoordZ + point.y);
            this.caveCalc(chunk);
        }

        this.isUpdateImage = this.stripCounter.count() >= this.stripCountMax1;
        this.isCompleteImage = this.isUpdateImage && this.stripCounter.count() >= this.stripCountMax2;
    }

    private void caveCalc(Chunk chunk) {
        if (chunk != null && !(chunk instanceof class_255)) {
            int offsetX = 128 + chunk.x * 16 - this.x;
            int offsetZ = 128 + chunk.z * 16 - this.z;

            for(int z = 0; z < 16; ++z) {
                int zCoord = offsetZ + z;
                if (zCoord >= 0) {
                    if (zCoord >= 256) {
                        break;
                    }

                    for(int x = 0; x < 16; ++x) {
                        int xCoord = offsetX + x;
                        if (xCoord >= 0) {
                            if (xCoord >= 256) {
                                break;
                            }

                            float f;
                            f = 0.0F;
                            int y;
                            int _y;
                            label135:
                            switch(this.getDimension()) {
                            case -1:
                                y = 0;

                                while(true) {
                                    if (y >= temp.length) {
                                        break label135;
                                    }

                                    _y = this.y - y;
                                    if (_y >= 0 && _y < 128 && chunk.getTileId(x, _y, z) == 0 && chunk.method_880(x, _y, z, 12) != 0) {
                                        f += temp[y];
                                    }

                                    _y = this.y + y + 1;
                                    if (_y >= 0 && _y < 128 && chunk.getTileId(x, _y, z) == 0 && chunk.method_880(x, _y, z, 12) != 0) {
                                        f += temp[y];
                                    }

                                    ++y;
                                }
                            case 0:
                                for(y = 0; y < temp.length; ++y) {
                                    _y = this.y - y;
                                    if (_y >= 128 || _y >= 0 && chunk.getTileId(x, _y, z) == 0 && chunk.method_880(x, _y, z, 12) != 0) {
                                        f += temp[y];
                                    }

                                    _y = this.y + y + 1;
                                    if (_y >= 128 || _y >= 0 && chunk.getTileId(x, _y, z) == 0 && chunk.method_880(x, _y, z, 12) != 0) {
                                        f += temp[y];
                                    }
                                }
                            case 1:
                            case 2:
                            default:
                                break;
                            case 3:
                                for(y = 0; y < temp.length; ++y) {
                                    _y = this.y - y;
                                    if (_y < 0 || _y >= 128 || chunk.getTileId(x, _y, z) == 0 && chunk.method_880(x, _y, z, 12) != 0) {
                                        f += temp[y];
                                    }

                                    _y = this.y + y + 1;
                                    if (_y < 0 || _y >= 128 || chunk.getTileId(x, _y, z) == 0 && chunk.method_880(x, _y, z, 12) != 0) {
                                        f += temp[y];
                                    }
                                }
                            }

                            f = 0.8F - f;
                            this.texture.setRGB(xCoord, zCoord, ftob(0.0F), ftob(f), ftob(0.0F));
                        }
                    }
                }
            }

        }
    }

    private void renderRoundMap(int x, int y) {
        GL11.glClear(256);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042 /*GL_BLEND*/);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(true);
        GL11.glColorMask(false, false, false, false);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, 0.0F);
        GL11.glRotatef(90.0F - this.thePlayer.yaw, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
        this.texture("/assets/reisminimap/textures/roundmap_mask.png");
        this.drawCenteringRectangle((double)x, (double)y, 1.01D, 64.0D, 64.0D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColorMask(true, true, true, true);
        double a = 0.25D / this.currentZoom;
        double slideX = (this.thePlayer.x - (double)this.lastX) * 0.00390625D;
        double slideY = (this.thePlayer.z - (double)this.lastZ) * 0.00390625D;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, this.mapTransparency ? 0.7F : 1.0F);
        this.texture.bind();
        this.startDrawingQuads();
        this.addVertexWithUV((double)(x - 32), (double)(y + 32), 1.0D, 0.5D + a + slideX, 0.5D + a + slideY);
        this.addVertexWithUV((double)(x + 32), (double)(y + 32), 1.0D, 0.5D + a + slideX, 0.5D - a + slideY);
        this.addVertexWithUV((double)(x + 32), (double)(y - 32), 1.0D, 0.5D - a + slideX, 0.5D - a + slideY);
        this.addVertexWithUV((double)(x - 32), (double)(y - 32), 1.0D, 0.5D - a + slideX, 0.5D + a + slideY);
        this.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
        double distance;
        if (this.visibleEntitiesRadar) {
            List<EntityBase> entityList = this.theWorld.entities;
            synchronized(entityList) {
                Iterator<EntityBase> i$ = entityList.iterator();

                while(i$.hasNext()) {
                    EntityBase entity = i$.next();
                    int color = this.getEntityColor(entity);
                    if (color != 0) {
                        double wayX = this.thePlayer.x - entity.x;
                        distance = this.thePlayer.z - entity.z;
                        float locate = (float)Math.toDegrees(Math.atan2(wayX, distance));
                        distance = Math.sqrt(wayX * wayX + distance * distance) * this.currentZoom * 0.5D;

                        try {
                            GL11.glPushMatrix();
                            if (distance < 29.0D) {
                                float r = (float)(color >> 16 & 255) * 0.003921569F;
                                float g = (float)(color >> 8 & 255) * 0.003921569F;
                                float b = (float)(color & 255) * 0.003921569F;
                                float alpha = (float)Math.max(0.20000000298023224D, 1.0D - Math.abs(this.thePlayer.y - entity.y) * 0.04D);
                                GL11.glColor4f(r, g, b, alpha);
                                GL11.glTranslatef((float)x, (float)y, 0.0F);
                                GL11.glRotatef(-locate - this.thePlayer.yaw + 180.0F, 0.0F, 0.0F, 1.0F);
                                GL11.glTranslated(0.0D, -distance, 0.0D);
                                GL11.glRotatef(-(-locate - this.thePlayer.yaw + 180.0F), 0.0F, 0.0F, 1.0F);
                                GL11.glTranslated(0.0D, distance, 0.0D);
                                GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
                                GL11.glTranslated(0.0D, -distance, 0.0D);
                                if (this.configEntityDirection) {
                                    GL11.glTranslatef((float)x, (float)y, 0.0F);
                                    GL11.glRotatef(entity.yaw - this.thePlayer.yaw, 0.0F, 0.0F, 1.0F);
                                    GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
                                    this.texture("/assets/reisminimap/textures/entity2.png");
                                    this.drawCenteringRectangle((double)x, (double)y, 1.0D, 8.0D, 8.0D);
                                } else {
                                    this.texture("/assets/reisminimap/textures/entity.png");
                                    this.drawCenteringRectangle((double)x, (double)y, 1.0D, 8.0D, 8.0D);
                                }
                            }
                        } finally {
                            GL11.glPopMatrix();
                        }
                    }
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, this.mapTransparency ? 0.7F : 1.0F);
        this.texture("/assets/reisminimap/textures/roundmap.png");
        this.drawCenteringRectangle((double)x, (double)y, 1.0D, 64.0D, 64.0D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        double wayX;
        if (this.visibleWaypoints) {
            Iterator<Waypoint> i$ = this.wayPts.iterator();

            while(i$.hasNext()) {
                Waypoint pt = i$.next();
                if (pt.enable) {
                    wayX = this.thePlayer.x - (double)pt.x - 0.5D;
                    double wayZ = this.thePlayer.z - (double)pt.z - 0.5D;
                    float locate = (float)Math.toDegrees(Math.atan2(wayX, wayZ));
                    distance = Math.sqrt(wayX * wayX + wayZ * wayZ) * this.currentZoom * 0.5D;

                    try {
                        GL11.glPushMatrix();
                        if (distance < 31.0D) {
                            GL11.glColor4f(pt.red, pt.green, pt.blue, (float)Math.min(1.0D, Math.max(0.4D, (distance - 1.0D) * 0.5D)));
                            this.texture(Waypoint.FILE[pt.type]);
                            GL11.glTranslatef((float)x, (float)y, 0.0F);
                            GL11.glRotatef(-locate - this.thePlayer.yaw + 180.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslated(0.0D, -distance, 0.0D);
                            GL11.glRotatef(-(-locate - this.thePlayer.yaw + 180.0F), 0.0F, 0.0F, 1.0F);
                            GL11.glTranslated(0.0D, distance, 0.0D);
                            GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
                            GL11.glTranslated(0.0D, -distance, 0.0D);
                            this.drawCenteringRectangle((double)x, (double)y, 1.0D, 8.0D, 8.0D);
                        } else {
                            GL11.glColor3f(pt.red, pt.green, pt.blue);
                            this.texture(Waypoint.MARKER[pt.type]);
                            GL11.glTranslatef((float)x, (float)y, 0.0F);
                            GL11.glRotatef(-locate - this.thePlayer.yaw + 180.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
                            GL11.glTranslated(0.0D, -34.0D, 0.0D);
                            this.drawCenteringRectangle((double)x, (double)y, 1.0D, 8.0D, 8.0D);
                        }
                    } finally {
                        GL11.glPopMatrix();
                    }
                }
            }
        }

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        double s = Math.sin(Math.toRadians((double)this.thePlayer.yaw)) * 28.0D;
        wayX = Math.cos(Math.toRadians((double)this.thePlayer.yaw)) * 28.0D;
        this.texture("/assets/reisminimap/textures/n.png");
        this.drawCenteringRectangle((double)x + wayX, (double)y - s, 1.0D, 8.0D, 8.0D);
        this.texture("/assets/reisminimap/textures/w.png");
        this.drawCenteringRectangle((double)x - s, (double)y - wayX, 1.0D, 8.0D, 8.0D);
        this.texture("/assets/reisminimap/textures/s.png");
        this.drawCenteringRectangle((double)x - wayX, (double)y + s, 1.0D, 8.0D, 8.0D);
        this.texture("/assets/reisminimap/textures/e.png");
        this.drawCenteringRectangle((double)x + s, (double)y + wayX, 1.0D, 8.0D, 8.0D);
        GL11.glDepthMask(true);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
    }

    private void renderSquareMap(int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042 /*GL_BLEND*/);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(true);
        GL11.glColorMask(false, false, false, false);
        this.texture("/reifnsk/minimap/squaremap_mask.png");
        this.drawCenteringRectangle((double)x, (double)y, 1.001D, 64.0D, 64.0D);
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.0F);
        GL11.glColorMask(true, true, true, true);
        double a = 0.25D / this.currentZoom;
        double slideX = (this.thePlayer.x - (double)this.lastX) * 0.00390625D;
        double slideY = (this.thePlayer.z - (double)this.lastZ) * 0.00390625D;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, this.mapTransparency ? 0.7F : 1.0F);
        this.texture.bind();
        this.startDrawingQuads();
        this.addVertexWithUV((double)(x - 32), (double)(y + 32), 1.0D, 0.5D + a + slideX, 0.5D + a + slideY);
        this.addVertexWithUV((double)(x + 32), (double)(y + 32), 1.0D, 0.5D + a + slideX, 0.5D - a + slideY);
        this.addVertexWithUV((double)(x + 32), (double)(y - 32), 1.0D, 0.5D - a + slideX, 0.5D - a + slideY);
        this.addVertexWithUV((double)(x - 32), (double)(y - 32), 1.0D, 0.5D - a + slideX, 0.5D + a + slideY);
        this.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        double wayZ;
        double t;
        if (this.visibleEntitiesRadar) {
            List<EntityBase> entityList = this.theWorld.entities;
            synchronized(entityList) {
                Iterator<EntityBase> i$ = entityList.iterator();

                while(i$.hasNext()) {
                    EntityBase entity = i$.next();
                    int color = this.getEntityColor(entity);
                    if (color != 0) {
                        double wayX = this.thePlayer.x - entity.x;
                        wayZ = this.thePlayer.z - entity.z;
                        wayX = wayX * this.currentZoom * 0.5D;
                        wayZ = wayZ * this.currentZoom * 0.5D;
                        t = Math.max(Math.abs(wayX), Math.abs(wayZ));

                        try {
                            GL11.glPushMatrix();
                            if (t < 31.0D) {
                                float r = (float)(color >> 16 & 255) * 0.003921569F;
                                float g = (float)(color >> 8 & 255) * 0.003921569F;
                                float b = (float)(color & 255) * 0.003921569F;
                                float alpha = (float)Math.max(0.20000000298023224D, 1.0D - Math.abs(this.thePlayer.y - entity.y) * 0.04D);
                                GL11.glColor4f(r, g, b, alpha);
                                if (this.configEntityDirection) {
                                    GL11.glTranslated((double)x + wayZ, (double)y - wayX, 0.0D);
                                    GL11.glRotatef(entity.yaw - 90.0F, 0.0F, 0.0F, 1.0F);
                                    GL11.glTranslated((double)(-x) - wayZ, (double)(-y) + wayX, 0.0D);
                                    this.texture("/assets/reisminimap/textures/entity2.png");
                                    this.drawCenteringRectangle((double)x + wayZ, (double)y - wayX, 1.0D, 8.0D, 8.0D);
                                } else {
                                    this.texture("/assets/reisminimap/textures/entity.png");
                                    this.drawCenteringRectangle((double)x + wayZ, (double)y - wayX, 1.0D, 8.0D, 8.0D);
                                }
                            }
                        } finally {
                            GL11.glPopMatrix();
                        }
                    }
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, this.mapTransparency ? 0.7F : 1.0F);
        this.texture("/assets/reisminimap/textures/squaremap.png");
        this.drawCenteringRectangle((double)x, (double)y, 1.0D, 64.0D, 64.0D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.visibleWaypoints) {
            Iterator<Waypoint> i$ = this.wayPts.iterator();

            while(i$.hasNext()) {
                Waypoint pt = i$.next();
                if (pt.enable) {
                    double wayX = this.thePlayer.x - (double)pt.x - 0.5D;
                    wayZ = this.thePlayer.z - (double)pt.z - 0.5D;
                    wayX = wayX * this.currentZoom * 0.5D;
                    wayZ = wayZ * this.currentZoom * 0.5D;
                    float locate = (float)Math.toDegrees(Math.atan2(wayX, wayZ));
                    wayZ = Math.max(Math.abs(wayX), Math.abs(wayZ));

                    try {
                        GL11.glPushMatrix();
                        if (wayZ < 31.0D) {
                            GL11.glColor4f(pt.red, pt.green, pt.blue, (float)Math.min(1.0D, Math.max(0.4D, (wayZ - 1.0D) * 0.5D)));
                            this.texture(Waypoint.FILE[pt.type]);
                            this.drawCenteringRectangle((double)x + wayZ, (double)y - wayX, 1.0D, 8.0D, 8.0D);
                        } else {
                            t = 34.0D / wayZ;
                            wayX *= t;
                            wayZ *= t;
                            double hypot = Math.sqrt(wayX * wayX + wayZ * wayZ);
                            GL11.glColor3f(pt.red, pt.green, pt.blue);
                            this.texture(Waypoint.MARKER[pt.type]);
                            GL11.glTranslatef((float)x, (float)y, 0.0F);
                            GL11.glRotatef(-locate + 90.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
                            GL11.glTranslated(0.0D, -hypot, 0.0D);
                            this.drawCenteringRectangle((double)x, (double)y, 1.0D, 8.0D, 8.0D);
                        }
                    } finally {
                        GL11.glPopMatrix();
                    }
                }
            }
        }

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.texture("/assets/reisminimap/textures/n.png");
        this.drawCenteringRectangle((double)x, (double)(y - 28), 1.0D, 8.0D, 8.0D);
        this.texture("/assets/reisminimap/textures/s.png");
        this.drawCenteringRectangle((double)x, (double)(y + 28), 1.0D, 8.0D, 8.0D);
        this.texture("/assets/reisminimap/textures/w.png");
        this.drawCenteringRectangle((double)(x - 28), (double)y, 1.0D, 8.0D, 8.0D);
        this.texture("/assets/reisminimap/textures/e.png");
        this.drawCenteringRectangle((double)(x + 28), (double)y, 1.0D, 8.0D, 8.0D);

        try {
            GL11.glPushMatrix();
            this.texture("/assets/reisminimap/textures/mmarrow.png");
            GL11.glTranslated((double)x, (double)y, 0.0D);
            GL11.glRotatef(this.thePlayer.yaw - 90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslated((double)(-x), (double)(-y), 0.0D);
            this.drawCenteringRectangle((double)x, (double)y, 1.0D, 4.0D, 4.0D);
        } catch (Exception var43) {
        } finally {
            GL11.glPopMatrix();
        }

        GL11.glDepthMask(true);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
    }

    private void renderFullMap(int x, int y) {
        double centerX = (double)this.scWidth * 0.5D;
        double centerY = (double)this.scHeight * 0.5D;
        double slideX = (this.thePlayer.x - (double)this.lastX) * 0.00390625D;
        double slideY = (this.thePlayer.z - (double)this.lastZ) * 0.00390625D;
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(false);
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.texture.bind();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, this.mapTransparency ? 0.7F : 1.0F);
        this.startDrawingQuads();
        this.addVertexWithUV(centerX - 120.0D, centerY + 120.0D, 1.0D, 0.96875D + slideX, 0.96875D + slideY);
        this.addVertexWithUV(centerX + 120.0D, centerY + 120.0D, 1.0D, 0.96875D + slideX, 0.03125D + slideY);
        this.addVertexWithUV(centerX + 120.0D, centerY - 120.0D, 1.0D, 0.03125D + slideX, 0.03125D + slideY);
        this.addVertexWithUV(centerX - 120.0D, centerY - 120.0D, 1.0D, 0.03125D + slideX, 0.96875D + slideY);
        this.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        double d;
        double t;
        if (this.visibleEntitiesRadar) {
            List<EntityBase> entityList = this.theWorld.entities;
            synchronized(entityList) {
                Iterator<EntityBase> i$ = entityList.iterator();

                while(i$.hasNext()) {
                    EntityBase entity = i$.next();
                    int color = this.getEntityColor(entity);
                    if (color != 0) {
                        double wayX = this.thePlayer.x - entity.x;
                        d = this.thePlayer.z - entity.z;
                        t = Math.max(Math.abs(wayX), Math.abs(d));

                        try {
                            GL11.glPushMatrix();
                            if (t < 114.0D) {
                                float r = (float)(color >> 16 & 255) * 0.003921569F;
                                float g = (float)(color >> 8 & 255) * 0.003921569F;
                                float b = (float)(color & 255) * 0.003921569F;
                                float alpha = (float)Math.max(0.20000000298023224D, 1.0D - Math.abs(this.thePlayer.y - entity.y) * 0.04D);
                                GL11.glColor4f(r, g, b, alpha);
                                if (this.configEntityDirection) {
                                    GL11.glTranslated(centerX + d, centerY - wayX, 0.0D);
                                    GL11.glRotatef(entity.yaw - 90.0F, 0.0F, 0.0F, 1.0F);
                                    GL11.glTranslated(-centerX - d, -centerY + wayX, 0.0D);
                                    this.texture("/assets/reisminimap/textures/entity2.png");
                                    this.drawCenteringRectangle(centerX + d, centerY - wayX, 1.0D, 8.0D, 8.0D);
                                } else {
                                    this.texture("/assets/reisminimap/textures/entity.png");
                                    this.drawCenteringRectangle(centerX + d, centerY - wayX, 1.0D, 8.0D, 8.0D);
                                }
                            }
                        } finally {
                            GL11.glPopMatrix();
                        }
                    }
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.texture("/assets/reisminimap/textures/n.png");
        this.drawCenteringRectangle(centerX, centerY - 104.0D, 1.0D, 16.0D, 16.0D);
        this.texture("/assets/reisminimap/textures/s.png");
        this.drawCenteringRectangle(centerX, centerY + 104.0D, 1.0D, 16.0D, 16.0D);
        this.texture("/assets/reisminimap/textures/w.png");
        this.drawCenteringRectangle(centerX - 104.0D, centerY, 1.0D, 16.0D, 16.0D);
        this.texture("/assets/reisminimap/textures/e.png");
        this.drawCenteringRectangle(centerX + 104.0D, centerY, 1.0D, 16.0D, 16.0D);

        try {
            GL11.glPushMatrix();
            this.texture("/assets/reisminimap/textures/mmarrow.png");
            GL11.glTranslated(centerX, centerY, 0.0D);
            GL11.glRotatef(this.thePlayer.yaw - 90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslated(-centerX, -centerY, 0.0D);
            this.drawCenteringRectangle(centerX, centerY, 1.0D, 8.0D, 8.0D);
        } catch (Exception var46) {
        } finally {
            GL11.glPopMatrix();
        }

        if (this.visibleWaypoints) {
            Iterator<Waypoint> i$ = this.wayPts.iterator();

            while(i$.hasNext()) {
                Waypoint pt = i$.next();
                if (pt.enable) {
                    double wayX = this.thePlayer.x - (double)pt.x - 0.5D;
                    double wayZ = this.thePlayer.z - (double)pt.z - 0.5D;
                    float locate = (float)Math.toDegrees(Math.atan2(wayX, wayZ));
                    d = Math.max(Math.abs(wayX), Math.abs(wayZ));

                    try {
                        GL11.glPushMatrix();
                        if (d < 114.0D) {
                            GL11.glColor4f(pt.red, pt.green, pt.blue, (float)Math.min(1.0D, Math.max(0.4D, (d - 1.0D) * 0.5D)));
                            this.texture(Waypoint.FILE[pt.type]);
                            this.drawCenteringRectangle(centerX + wayZ, centerY - wayX, 1.0D, 8.0D, 8.0D);
                            if (KeyInput.TOGGLE_ZOOM.isKeyDown() && pt.name != null && !pt.name.isEmpty()) {
                                GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
                                GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.627451F);
                                int width = this.theMinecraft.textRenderer.getTextWidth(pt.name);
                                int _x = (int)(centerX + wayZ);
                                int _y = (int)(centerY - wayX);
                                int x1 = _x - (width >> 1);
                                int x2 = x1 + width;
                                int y1 = _y - 15;
                                int y2 = _y - 5;
                                this.tessellator.start();
                                this.tessellator.pos((double)(x1 - 1), (double)y2, 1.0D);
                                this.tessellator.pos((double)(x2 + 1), (double)y2, 1.0D);
                                this.tessellator.pos((double)(x2 + 1), (double)y1, 1.0D);
                                this.tessellator.pos((double)(x1 - 1), (double)y1, 1.0D);
                                this.tessellator.draw();
                                GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
                                this.theMinecraft.textRenderer.drawTextWithShadow(pt.name, x1, y1 + 1, pt.type == 0 ? -1 : -65536);
                            }
                        } else {
                            t = 117.0D / d;
                            wayX *= t;
                            wayZ *= t;
                            double hypot = Math.sqrt(wayX * wayX + wayZ * wayZ);
                            GL11.glColor3f(pt.red, pt.green, pt.blue);
                            this.texture(Waypoint.MARKER[pt.type]);
                            GL11.glTranslated(centerX, centerY, 0.0D);
                            GL11.glRotatef(-locate + 90.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslated(-centerX, -centerY, 0.0D);
                            GL11.glTranslated(0.0D, -hypot, 0.0D);
                            this.drawCenteringRectangle(centerX, centerY, 1.0D, 8.0D, 8.0D);
                        }
                    } finally {
                        GL11.glPopMatrix();
                    }
                }
            }
        }

        GL11.glDepthMask(true);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
    }

    private void texture(String texture) {
        this.theMinecraft.textureManager.bindTexture(this.theMinecraft.textureManager.getTextureId(texture));
    }

    public void setOption(EnumOption option, EnumOptionValue value) {
        this.lock.lock();

        try {
            switch(option) {
            case MINIMAP:
                this.enable = EnumOptionValue.bool(value);
                break;
            case SHOW_MENU_KEY:
                this.showMenuKey = EnumOptionValue.bool(value);
                break;
            case MAP_SHAPE:
                this.roundmap = value == EnumOptionValue.ROUND;
                break;
            case MAP_POSITION:
                this.mapPosition = Math.max(0, option.getValue(value));
                break;
            case MAP_TRANSPARENCY:
                this.mapTransparency = EnumOptionValue.bool(value);
                break;
            case FILTERING:
                this.filtering = EnumOptionValue.bool(value);
                break;
            case SHOW_COORDINATES:
                this.showCoordinate = EnumOptionValue.bool(value);
                break;
            case UPDATE_FREQUENCY:
                this.updateFrequencySetting = Math.max(0, option.getValue(value));
                break;
            case THREADING:
                this.threading = EnumOptionValue.bool(value);
                break;
            case THREAD_PRIORITY:
                this.threadPriority = Math.max(0, option.getValue(value));
                if (this.thread != null && this.thread.isAlive()) {
                    this.thread.setPriority(3 + this.threadPriority);
                }
                break;
            case LIGHTING:
                this.lightmap = Math.max(0, option.getValue(value));
                break;
            case TERRAIN_UNDULATE:
                this.undulate = EnumOptionValue.bool(value);
                break;
            case TERRAIN_DEPTH:
                this.heightmap = EnumOptionValue.bool(value);
                break;
            case TRANSPARENCY:
                this.transparency = EnumOptionValue.bool(value);
                break;
            case ENVIRONMENT_COLOR:
                this.environmentColor = EnumOptionValue.bool(value);
                break;
            case OMIT_HEIGHT_CALC:
                this.omitHeightCalc = EnumOptionValue.bool(value);
                break;
            case HIDE_SNOW:
                this.hideSnow = EnumOptionValue.bool(value);
                break;
            case SHOW_SLIME_CHUNK:
                this.showSlimeChunk = EnumOptionValue.bool(value);
                break;
            case RENDER_TYPE:
                this.renderType = Math.max(0, option.getValue(value));
                break;
            case ENTITIES_RADAR:
                this.configEntitiesRadar = EnumOptionValue.bool(value);
                break;
            case MINIMAP_OPTION:
                this.theMinecraft.openScreen(new GuiOptionScreen(1));
                break;
            case SURFACE_MAP_OPTION:
                this.theMinecraft.openScreen(new GuiOptionScreen(2));
                break;
            case ABOUT_MINIMAP:
                this.theMinecraft.openScreen(new GuiOptionScreen(4));
                break;
            case ENTITIES_RADAR_OPTION:
                this.theMinecraft.openScreen(new GuiOptionScreen(3));
                break;
            case ENG_FORUM:
                try {
                    Desktop.getDesktop().browse(new URI("http://www.minecraftforum.net/index.php?showtopic=482147"));
                } catch (Exception var9) {
                    var9.printStackTrace();
                }
                break;
            case JP_FORUM:
                try {
                    Desktop.getDesktop().browse(new URI("http://forum.minecraftuser.jp/viewtopic.php?f=13&t=153"));
                } catch (Exception var8) {
                    var8.printStackTrace();
                }
                break;
            case DEATH_POINT:
                this.deathPoint = EnumOptionValue.bool(value);
                break;
            case ENTITY_PLAYER:
                this.configEntityPlayer = EnumOptionValue.bool(value);
                break;
            case ENTITY_ANIMAL:
                this.configEntityAnimal = EnumOptionValue.bool(value);
                break;
            case ENTITY_MOB:
                this.configEntityMob = EnumOptionValue.bool(value);
                break;
            case ENTITY_SLIME:
                this.configEntitySlime = EnumOptionValue.bool(value);
                break;
            case ENTITY_SQUID:
                this.configEntitySquid = EnumOptionValue.bool(value);
                break;
            case ENTITY_DIRECTION:
                this.configEntityDirection = EnumOptionValue.bool(value);
                break;
            case DEFAULT_ZOOM:
                this.defaultZoom = Math.max(0, option.getValue(value));
            }

            this.forceUpdate = true;
            this.stripCounter.reset();
            if (this.threading) {
                this.mapCalc(false);
                if (this.isCompleteImage) {
                    this.texture.register();
                }
            }
        } finally {
            this.lock.unlock();
        }

    }

    public EnumOptionValue getOption(EnumOption option) {
        switch(option) {
        case MINIMAP:
            return EnumOptionValue.bool(this.enable);
        case SHOW_MENU_KEY:
            return EnumOptionValue.bool(this.showMenuKey);
        case MAP_SHAPE:
            return this.roundmap ? EnumOptionValue.ROUND : EnumOptionValue.SQUARE;
        case MAP_POSITION:
            return option.getValue(this.mapPosition);
        case MAP_TRANSPARENCY:
            return EnumOptionValue.bool(this.mapTransparency);
        case FILTERING:
            return EnumOptionValue.bool(this.filtering);
        case SHOW_COORDINATES:
            return EnumOptionValue.bool(this.showCoordinate);
        case UPDATE_FREQUENCY:
            return option.getValue(this.updateFrequencySetting);
        case THREADING:
            return EnumOptionValue.bool(this.threading);
        case THREAD_PRIORITY:
            return option.getValue(this.threadPriority);
        case LIGHTING:
            return option.getValue(this.lightmap);
        case TERRAIN_UNDULATE:
            return EnumOptionValue.bool(this.undulate);
        case TERRAIN_DEPTH:
            return EnumOptionValue.bool(this.heightmap);
        case TRANSPARENCY:
            return EnumOptionValue.bool(this.transparency);
        case ENVIRONMENT_COLOR:
            return EnumOptionValue.bool(this.environmentColor);
        case OMIT_HEIGHT_CALC:
            return EnumOptionValue.bool(this.omitHeightCalc);
        case HIDE_SNOW:
            return EnumOptionValue.bool(this.hideSnow);
        case SHOW_SLIME_CHUNK:
            return EnumOptionValue.bool(this.showSlimeChunk);
        case RENDER_TYPE:
            return option.getValue(this.renderType);
        case ENTITIES_RADAR:
            return EnumOptionValue.bool(this.configEntitiesRadar);
        case MINIMAP_OPTION:
        case SURFACE_MAP_OPTION:
        case ABOUT_MINIMAP:
        case ENTITIES_RADAR_OPTION:
        case ENG_FORUM:
        case JP_FORUM:
        default:
            return option.getValue(0);
        case DEATH_POINT:
            return EnumOptionValue.bool(this.deathPoint);
        case ENTITY_PLAYER:
            return EnumOptionValue.bool(this.configEntityPlayer);
        case ENTITY_ANIMAL:
            return EnumOptionValue.bool(this.configEntityAnimal);
        case ENTITY_MOB:
            return EnumOptionValue.bool(this.configEntityMob);
        case ENTITY_SLIME:
            return EnumOptionValue.bool(this.configEntitySlime);
        case ENTITY_SQUID:
            return EnumOptionValue.bool(this.configEntitySquid);
        case ENTITY_DIRECTION:
            return EnumOptionValue.bool(this.configEntityDirection);
        case DEFAULT_ZOOM:
            return option.getValue(this.defaultZoom);
        }
    }

    void saveOptions() {
        File file = new File(directory, "option.txt");

        try {
            PrintWriter out = new PrintWriter(file);
            EnumOption[] arr$ = EnumOption.values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                EnumOption option = arr$[i$];
                if (this.getOption(option) != EnumOptionValue.SUB_OPTION && this.getOption(option) != EnumOptionValue.VERSION && this.getOption(option) != EnumOptionValue.AUTHER) {
                    out.printf("%s: %s%n", capitalize(option.toString()), capitalize(this.getOption(option).toString()));
                }
            }

            out.flush();
            out.close();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    private void loadOptions() {
        File file = new File(directory, "option.txt");
        if (file.exists()) {
            boolean error = false;

            try {
                Scanner in = new Scanner(file);

                while(in.hasNextLine()) {
                    try {
                        String[] strs = in.nextLine().split(":");
                        this.setOption(EnumOption.valueOf(toUpperCase(strs[0].trim())), EnumOptionValue.valueOf(toUpperCase(strs[1].trim())));
                    } catch (Exception var5) {
                        System.err.println(var5.getMessage());
                        error = true;
                    }
                }

                in.close();
            } catch (Exception var6) {
                var6.printStackTrace();
            }

            if (error) {
                this.saveOptions();
            }

            this.flagZoom = this.defaultZoom;
        }
    }

    public ArrayList<Waypoint> getWaypoints() {
        return this.wayPts;
    }

    void saveWaypoints() {
        File waypointFile = new File(directory, this.currentLevelName + ".DIM" + this.waypointDimension + ".points");
        if (waypointFile.isDirectory()) {
            this.chatInfo("\u00a7E[Rei's Minimap] Error Saving Waypoints");
        } else {
            try {
                PrintWriter out = new PrintWriter(waypointFile);
                Iterator<Waypoint> i$ = this.wayPts.iterator();

                while(i$.hasNext()) {
                    Waypoint pt = i$.next();
                    out.println(pt);
                }

                out.flush();
                out.close();
            } catch (Exception var5) {
                this.chatInfo("\u00a7E[Rei's Minimap] Error Saving Waypoints");
            }

        }
    }

    void loadWaypoints() {
        this.wayPts = null;
        this.wayPtsMap.clear();
        Pattern pattern = Pattern.compile(Pattern.quote(this.currentLevelName) + "\\.DIM(-?[0-9])\\.points");
        int load = 0;
        int dim = 0;
        String[] arr$ = directory.list();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String file = arr$[i$];
            Matcher m = pattern.matcher(file);
            if (m.matches()) {
                ++dim;
                int dimension = Integer.parseInt(m.group(1));
                ArrayList<Waypoint> list = new ArrayList<>();
                Scanner in = null;

                try {
                    in = new Scanner(new File(directory, file));

                    while(in.hasNextLine()) {
                        Waypoint wp = Waypoint.load(in.nextLine());
                        if (wp != null) {
                            list.add(wp);
                            ++load;
                        }
                    }
                } catch (Exception var16) {
                } finally {
                    if (in != null) {
                        in.close();
                    }

                }

                this.wayPtsMap.put(dimension, list);
                if (dimension == this.currentDimension) {
                    this.wayPts = list;
                }
            }
        }

        if (this.wayPts == null) {
            this.wayPts = new ArrayList<>();
        }

        if (load != 0) {
            this.chatInfo("\u00a7E[Rei's Minimap] " + load + " Waypoints loaded for " + this.currentLevelName);
        }

    }

    private void chatInfo(String s) {
        this.ingameGUI.addChatMessage(s);
    }

    private int getDimension() {
        return this.theMinecraft.player.dimensionId;
    }

    float[] generateLightBrightnessTable(float f) {
        float[] result = new float[16];

        for(int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float)i / 15.0F;
            result[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
        }

        return result;
    }

    float[] generateLightBrightnessTable2(float f) {
        float[] result = new float[16];

        for(int i = 0; i <= 15; ++i) {
            float f1 = (float)i / 15.0F;
            result[i] = f1 * (1.0F - f) + f;
        }

        return result;
    }

    float[] generateLightBrightnessTable3(float f1, float f2) {
        float[] a1 = this.generateLightBrightnessTable(f1);
        float[] a2 = this.generateLightBrightnessTable(f2);
        float[] result = new float[16];

        for(int i = 0; i <= 15; ++i) {
            result[i] = (a1[i] + a2[i]) * 0.5F;
        }

        return result;
    }

    private Chunk getChunk(Level world, int x, int z) {
        boolean b = Math.abs(this.chunkCoordX - x) <= 8 && Math.abs(this.chunkCoordZ - z) <= 8;
        Chunk chunk = b ? this.chunkCache.get(world, x, z) : EMPTY_CHUNK;
        return (Chunk)(chunk != null ? chunk : EMPTY_CHUNK);
    }

    private void drawCenteringRectangle(double centerX, double centerY, double z, double w, double h) {
        w *= 0.5D;
        h *= 0.5D;
        this.startDrawingQuads();
        this.addVertexWithUV(centerX - w, centerY + h, z, 0.0D, 1.0D);
        this.addVertexWithUV(centerX + w, centerY + h, z, 1.0D, 1.0D);
        this.addVertexWithUV(centerX + w, centerY - h, z, 1.0D, 0.0D);
        this.addVertexWithUV(centerX - w, centerY - h, z, 0.0D, 0.0D);
        this.draw();
    }

    public static String capitalize(String src) {
        if (src == null) {
            return null;
        } else {
            boolean title = true;
            char[] cs = src.toCharArray();
            int i = 0;

            for(int j = cs.length; i < j; ++i) {
                char c = cs[i];
                if (c == '_') {
                    c = ' ';
                }

                cs[i] = title ? Character.toTitleCase(c) : Character.toLowerCase(c);
                title = Character.isWhitespace(c);
            }

            return new String(cs);
        }
    }

    public static String toUpperCase(String src) {
        return src == null ? null : src.replace(' ', '_').toUpperCase();
    }

    private static boolean checkGuiScreen(ScreenBase gui) {
        return gui == null || gui instanceof GuiScreenInterface || gui instanceof Chat || gui instanceof Death;
    }

    private static Map<String, String> createObfuscatorFieldMap() {
        HashMap<String, String> map = new HashMap<>();
        if ("Beta 1.7.3".equals("Beta 1.7.3")) {
            map.put("chatMessageList", "e");
            map.put("worldInfo", "x");
            map.put("levelName", "j");
            map.put("sendQueue", "bN");
            map.put("netManager", "e");
            map.put("remoteSocketAddress", "i");
            map.put("dimension", "m");
        } else if ("Beta 1.7.3".equals("Beta 1.6.6")) {
            map.put("chatMessageList", "e");
            map.put("worldInfo", "x");
            map.put("levelName", "j");
            map.put("sendQueue", "bM");
            map.put("netManager", "e");
            map.put("remoteSocketAddress", "i");
            map.put("dimension", "m");
        } else if ("Beta 1.7.3".equals("Beta 1.5_01")) {
            map.put("chatMessageList", "e");
            map.put("worldInfo", "s");
            map.put("levelName", "j");
            map.put("sendQueue", "bJ");
            map.put("netManager", "d");
            map.put("remoteSocketAddress", "g");
            map.put("dimension", "p");
        }

        return Collections.unmodifiableMap(map);
    }

    private static Object getField(Object obj, String name) {
        String obfName = (String)obfascatorFieldMap.get(name);
        if (obj != null && name != null && obfName != null) {
            Class<?> clazz = obj instanceof Class ? (Class)obj : obj.getClass();
            Object result = getField(clazz, obj, obfName);
            return result != null ? result : getField(clazz, obj, name);
        } else {
            return null;
        }
    }

    private static Object getFields(Object obj, String... names) {
        String[] arr$ = names;
        int len$ = names.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String name = arr$[i$];
            obj = getField(obj, name);
        }

        return obj;
    }

    private static Object getField(Class<?> clazz, Object obj, String name) {
        while(clazz != null) {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception var4) {
                clazz = clazz.getSuperclass();
            }
        }

        return null;
    }

    private int getEntityColor(EntityBase entity) {
        if (entity == this.thePlayer) {
            return 0;
        } else if (entity instanceof PlayerBase) {
            return this.visibleEntityPlayer ? -16711681 : 0;
        } else if (entity instanceof Squid) {
            return this.visibleEntitySquid ? -16760704 : 0;
        } else if (entity instanceof AnimalBase) {
            return this.visibleEntityAnimal ? -1 : 0;
        } else if (entity instanceof Slime) {
            return this.visibleEntitySlime ? -10444704 : 0;
        } else if (entity instanceof MonsterBase) {
            return this.visibleEntityMob ? -65536 : 0;
        } else {
            return 0;
        }
    }

    static {
        directory = new File(Minecraft.getGameDirectory(), "mods" + File.separatorChar + "rei_minimap");
        updateFrequencys = new int[]{2, 5, 10, 20, 40};
        instance = new ReiMinimap();
        EMPTY_CHUNK_BYTES = new byte['\u8000'];
        EMPTY_CHUNK = new class_255((Level) null, EMPTY_CHUNK_BYTES, 0, 0);
        ZOOM_LIST = new double[]{0.5D, 1.0D, 1.5D, 2.0D, 4.0D, 8.0D};
        temp = new float[10];
        float f = 0.0F;

        int i;
        for(i = 0; i < temp.length; ++i) {
            temp[i] = (float)(1.0D / Math.sqrt((double)(i + 1)));
            f += temp[i];
        }

        f = 0.3F / f;

        for(i = 0; i < temp.length; ++i) {
            float[] var10000 = temp;
            var10000[i] *= f;
        }

        f = 0.0F;

        for(i = 0; i < 10; ++i) {
            f += temp[i];
        }

        obfascatorFieldMap = createObfuscatorFieldMap();
    }
}
