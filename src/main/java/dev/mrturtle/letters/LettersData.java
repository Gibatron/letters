package dev.mrturtle.letters;

import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;

public class LettersData {
	public HashMap<Character, Integer> letters = new HashMap<>();

	public LettersData() {
		char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		for (char c : alphabet) {
			letters.put(c, 0);
		}
	}

	public static void createIfNull(ServerPlayerEntity player) {
		if (PlayerDataApi.getCustomDataFor(player, Letters.DATA_STORAGE) == null) {
			PlayerDataApi.setCustomDataFor(player, Letters.DATA_STORAGE, new LettersData());
		}
	}
}
