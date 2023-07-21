package dev.mrturtle.letters.mixin;

import dev.mrturtle.letters.gui.SelectGui;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow public ServerPlayerEntity player;

	@Inject(method = "onAdvancementTab", at = @At("TAIL"))
	public void onAdvancementTab(AdvancementTabC2SPacket packet, CallbackInfo ci) {
		if (packet.getAction() == AdvancementTabC2SPacket.Action.OPENED_TAB) {
			SelectGui gui = new SelectGui(player);
			gui.open();
		}
	}
}
