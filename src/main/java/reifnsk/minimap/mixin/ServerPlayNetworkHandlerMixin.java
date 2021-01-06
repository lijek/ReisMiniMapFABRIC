package reifnsk.minimap.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;

@Mixin(ServerPlayNetworkHandler.class)
public interface ServerPlayNetworkHandlerMixin {
    @Accessor("field_1282")
    SocketAddress getSocketAddress();
}
