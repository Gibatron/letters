package dev.mrturtle.letters.gui;

import dev.mrturtle.letters.Letters;
import dev.mrturtle.letters.LettersData;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.HashMap;

public class DeconstructGui extends SimpleGui {
	public DeconstructGui(ServerPlayerEntity player) {
		super(ScreenHandlerType.GENERIC_3X3, player, false);
		setTitle(Text.literal("Deconstruct Items"));
		for (int x = 0; x < getSize(); x++) {
			setSlot(x, new GuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE).setName(Text.empty()).build());
		}
		clearSlot(4);
		SimpleInventory inventory = new SimpleInventory(1);
		setSlotRedirect(4, new Slot(inventory, 0, 0, 0));
	}

	@Override
	public void onTick() {
		Item inputItem = getSlotRedirect(4).getStack().getItem();
		if (inputItem != Items.AIR) {
			setSlot(7, new GuiElementBuilder()
					.setItem(Items.DAMAGED_ANVIL)
					.setName(Text.literal("Deconstruct")
							.setStyle(Style.EMPTY.withItalic(false)))
					.addLoreLine(calculateGain(inputItem, player))
					.setCallback((i, clickType, actionType) -> {
						ItemStack inputStack = getSlotRedirect(4).getStack();
						if (clickType.shift) {
							while (!inputStack.isEmpty()) {
								addGain();
								inputStack.decrement(1);
							}
						} else {
							addGain();
							inputStack.decrement(1);
						}
					})
			);
		} else {
			setSlot(7, new GuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE).setName(Text.empty()).build());
		}
	}

	@Override
	public void onClose() {
		player.getInventory().offerOrDrop(getSlotRedirect(4).getStack());
		super.onClose();
		SelectGui selectGui = new SelectGui(player);
		selectGui.open();
	}

	public void addGain() {
		LettersData data = PlayerDataApi.getCustomDataFor(player, Letters.DATA_STORAGE);
		String gain = calculateGain(getSlotRedirect(4).getStack().getItem(), player).getString();
		for (char c : gain.toCharArray()) {
			data.letters.put(c, data.letters.get(c) + 1);
		}
		PlayerDataApi.setCustomDataFor(player, Letters.DATA_STORAGE, data);
	}

	public static MutableText calculateGain(Item item, ServerPlayerEntity serverPlayer, boolean onlyNew) {
		LettersData.createIfNull(serverPlayer);
		HashMap<Character, Integer> letters = PlayerDataApi.getCustomDataFor(serverPlayer, Letters.DATA_STORAGE).letters;
		char[] name = item.getName().getString().toLowerCase().replace(" ", "").toCharArray();
		Arrays.sort(name);
		MutableText text = Text.empty();
		for (char c : name) {
			MutableText segment = Text.literal(String.valueOf(c));
			if (letters.get(c) > 0) {
				segment.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
				if (!onlyNew)
					text.append(segment);
			} else {
				segment.setStyle(Style.EMPTY.withColor(Formatting.GREEN));
				text.append(segment);
			}
		}
		return text;
	}

	public static Text calculateGain(Item item, ServerPlayerEntity serverPlayer) {
		return calculateGain(item, serverPlayer, false);
	}
}
