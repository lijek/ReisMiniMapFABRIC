package reifnsk.minimap.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.InGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reifnsk.minimap.ReiMinimap;

@Mixin(InGame.class)
public class InGameMixin {

    @Shadow private Minecraft minecraft;

    @Inject(at=@At("RETURN"), method="renderHud")
    public void renderHud(float f, boolean flag, int i, int j, CallbackInfo ci){
        ReiMinimap.instance.onTickInGame(this.minecraft);
    }
}
