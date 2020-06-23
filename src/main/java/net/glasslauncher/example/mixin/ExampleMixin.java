package net.glasslauncher.example.mixin;

import net.minecraft.item.ItemBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBase.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/item/ItemBase;getTranslatedName()Ljava/lang/String;")
	private void init(CallbackInfoReturnable<String> cir) {
		System.out.println("This line is printed by an example mod mixin!");
	}
}
