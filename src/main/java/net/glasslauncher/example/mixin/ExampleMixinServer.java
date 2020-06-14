package net.glasslauncher.example.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class ExampleMixinServer {
	@Inject(at = @At("HEAD"), method = "start")
	private void init(CallbackInfoReturnable<Boolean> cir) {
		System.out.println("This line is printed by an example mod mixin!");
	}
}
