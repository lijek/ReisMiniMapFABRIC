package reifnsk.minimap;

import java.util.Arrays;

import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;

public class ChunkCache {
    private static final int threshold = 128;
    private final int shift;
    private final int size;
    private final int mask;
    private Chunk[] cache;
    private int[] count;
    private boolean[] slime;

    public ChunkCache(int scale) {
        this.shift = scale;
        this.size = 1 << this.shift;
        this.mask = this.size - 1;
        this.cache = new Chunk[this.size * this.size];
        this.count = new int[this.size * this.size];
        this.slime = new boolean[this.size * this.size];
    }

    public Chunk get(Level world, int x, int z) {
        int ptr = x & this.mask | (z & this.mask) << this.shift;
        Chunk chunk = this.cache[ptr];
        if (chunk == null || chunk.level != world || !chunk.method_858(x, z) || ++this.count[ptr] > 128) {
            this.cache[ptr] = chunk = world.getChunkFromCache(x, z);
            this.count[ptr] = 0;
            this.slime[ptr] = chunk.method_863(987234911L).nextInt(10) == 0;
        }

        return chunk;
    }

    public boolean isSlimeSpawn(int x, int z) {
        return this.slime[x & this.mask | (z & this.mask) << this.shift];
    }

    public void clear() {
        Arrays.fill(this.cache, (Object)null);
        Arrays.fill(this.count, 0);
    }
}
