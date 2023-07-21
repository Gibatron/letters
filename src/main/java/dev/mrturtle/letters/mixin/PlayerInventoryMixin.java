package dev.mrturtle.letters.mixin;

import dev.mrturtle.letters.gui.DeconstructGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
	@Shadow @Final public PlayerEntity player;

	@Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setCount(I)V"))
	public void insertStack(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		displayNewLetters(stack);
	}

	@Inject(method = "setStack", at = @At(value = "RETURN"))
	public void setStack(int slot, ItemStack stack, CallbackInfo ci) {
		displayNewLetters(stack);
	}

	public void displayNewLetters(ItemStack stack) {
		// TODO PLAY SOUND ON DISPLAY
		if (stack.getItem() == Items.AIR)
			return;
		MutableText gain = DeconstructGui.calculateGain(stack.getItem(), (ServerPlayerEntity) player, true);
		if (gain.getString().length() > 0) {
			MutableText bolded = Text.literal("(!) ").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));
			MutableText message = Text.literal("New letter(s) available: ").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false));
			gain = Text.literal(gain.getString().toUpperCase());
			gain.setStyle(gain.getStyle().withColor(Formatting.GREEN).withBold(true));
			player.sendMessage(bolded.append(message.append(gain)), true);
		}
	}
}
