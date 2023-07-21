package dev.mrturtle.letters.gui;

import dev.mrturtle.letters.Letters;
import dev.mrturtle.letters.LettersData;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;

public class SelectGui extends SimpleGui {

	static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	public SelectGui(ServerPlayerEntity player) {
		super(ScreenHandlerType.GENERIC_3X3, player, false);
		setTitle(Text.literal("Letters"));
		for (int x = 0; x < getSize(); x++) {
			setSlot(x, new GuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE).setName(Text.empty()).build());
		}
		setSlot(3, new GuiElementBuilder(Items.DAMAGED_ANVIL).setName(Text.literal("Deconstruct Items")).setCallback((index, clickType, actionType) -> {
			close();
			DeconstructGui deconstructGui = new DeconstructGui(player);
			deconstructGui.open();
		}));
		setSlot(5, new GuiElementBuilder(Items.CRAFTING_TABLE).setName(Text.literal("Construct Items")).setCallback((index, clickType, actionType) -> {
			close();
			ConstructGui constructGui = new ConstructGui(player);
			constructGui.open();
		}));

		if (PlayerDataApi.getCustomDataFor(player, Letters.DATA_STORAGE) == null) {
			PlayerDataApi.setCustomDataFor(player, Letters.DATA_STORAGE, new LettersData());
		}
		String letters = PlayerDataApi.getCustomDataFor(player, Letters.DATA_STORAGE).letters;
		HashMap<Character, Integer> letterMap = new HashMap<>();
		MutableText text = Text.empty();
		for (char c : alphabet) {
			letterMap.put(c, 0);
		}
		for (char c : letters.toCharArray()) {
			if (letterMap.containsKey(c)) {
				letterMap.put(c, letterMap.get(c) + 1);
			} else {
				letterMap.put(c, 1);
			}
		}
		for (Character c : letterMap.keySet()) {
			MutableText segment = Text.literal(String.valueOf(c).toUpperCase()).setStyle(Style.EMPTY.withBold(true));
			int count = letterMap.get(c);
			String countString = convertToSubscript(String.valueOf(count).toCharArray());
			MutableText countSegment = Text.literal(countString).setStyle(styleLetter(count).withBold(false));
			text.append(segment);
			text.append(countSegment);
		}
		setSlot(1, new GuiElementBuilder(Items.END_CRYSTAL).setName(text));
	}

	public String convertToSubscript(char[] characters) {
		StringBuilder subscript = new StringBuilder();
		for (Character c : characters) {
			int integer = Integer.parseInt(Character.toString(c));
			char character = (char) (integer + 8320);
			subscript.append(character);
		}
		return subscript.toString();
	}

	public Style styleLetter(int count) {
		if (count >= 15) {
			return Style.EMPTY.withColor(Formatting.GREEN);
		} else if (count >= 5) {
			return Style.EMPTY.withColor(Formatting.YELLOW);
		} else if (count > 0) {
			return Style.EMPTY.withColor(Formatting.RED);
		} else {
			return Style.EMPTY.withColor(Formatting.GRAY);
		}
	}
}
