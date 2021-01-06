package reifnsk.minimap.mixin;

import net.minecraft.network.ClientPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerMixin {
    @Accessor("netHandler")
    ServerPlayNetworkHandler getNetHandler();
}
