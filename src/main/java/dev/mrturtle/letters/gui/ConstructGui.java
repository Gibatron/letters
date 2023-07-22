package dev.mrturtle.letters.gui;

import dev.mrturtle.letters.Letters;
import dev.mrturtle.letters.LettersData;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;

public class ConstructGui extends AnvilInputGui {
	public ConstructGui(ServerPlayerEntity player) {
		super(player, false);
	}

	@Override
	public void onInput(String input) {
		CostResult result = calculateCost(input);
		Item item = getResult(input);
		if (item != Items.AIR) {
			if (result.canMake) {
				setSlot(2, new GuiElementBuilder(item).addLoreLine(Text.literal("Click to construct").setStyle(Style.EMPTY.withColor(Formatting.GREEN))).setCallback((index, clickType, actionType) -> {
					if (calculateCost(input).canMake) {
						player.getInventory().offerOrDrop(item.getDefaultStack());
						removeCost(result.text.getString());
						setDefaultInputValue(input);
					} else {
						setSlot(2, new GuiElementBuilder(Items.BARRIER).setName(result.text).addLoreLine(Text.literal("Missing letters").setStyle(Style.EMPTY.withColor(Formatting.RED))));
					}
				}));
			} else {
				setSlot(2, new GuiElementBuilder(Items.BARRIER).setName(result.text).addLoreLine(Text.literal("Missing letters").setStyle(Style.EMPTY.withColor(Formatting.RED))));
			}
		} else {
			setSlot(2, new GuiElementBuilder(Items.BARRIER).setName(result.text).addLoreLine(Text.literal("Invalid item").setStyle(Style.EMPTY.withColor(Formatting.RED))).glow());
		}
	}

	@Override
	public void onClose() {
		super.onClose();
		SelectGui selectGui = new SelectGui(player);
		selectGui.open();
	}

	public void removeCost(String input) {
		LettersData data = PlayerDataApi.getCustomDataFor(player, Letters.DATA_STORAGE);
		for (char c : input.toCharArray()) {
			data.letters.put(c, data.letters.get(c) - 1);
		}
		PlayerDataApi.setCustomDataFor(player, Letters.DATA_STORAGE, data);
	}

	public Item getResult(String input) {
		for (String name : Letters.searchItems.keySet()) {
			if (name.equalsIgnoreCase(input)) {
				return Letters.searchItems.get(name);
			}
		}
		return Items.AIR;
	}

	public CostResult calculateCost(String input) {
		HashMap<Character, Integer> letters = (HashMap<Character, Integer>) PlayerDataApi.getCustomDataFor(player, Letters.DATA_STORAGE).letters.clone();
		char[] name = input.toLowerCase().replace(" ", "").toCharArray();
		MutableText text = Text.empty();
		CostResult result = new CostResult();
		for (char c : name) {
			MutableText segment = Text.literal(String.valueOf(c));
			if (letters.get(c) > 0) {
				letters.put(c, letters.get(c) - 1);
				segment.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
			} else {
				segment.setStyle(Style.EMPTY.withColor(Formatting.RED));
				result.canMake = false;
			}
			text.append(segment);
		}
		result.text = text;
		return result;
	}

	private static class CostResult {
		public Text text;
		public boolean canMake = true;
	}
}
