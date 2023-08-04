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

		LettersData.createIfNull(player);
		HashMap<Character, Integer> letters = PlayerDataApi.getCustomDataFor(player, Letters.DATA_STORAGE).letters;
		MutableText text = Text.empty();
		for (Character c : letters.keySet()) {
			MutableText segment = Text.literal(String.valueOf(c).toUpperCase()).setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(true));
			int count = letters.get(c);
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
