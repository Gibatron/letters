package dev.mrturtle.letters;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class Letters implements ModInitializer {
	public static final PlayerDataStorage<LettersData> DATA_STORAGE = new JsonDataStorage<>("letters", LettersData.class);

	public static HashMap<String, Item> searchItems = new HashMap<>();

	@Override
	public void onInitialize() {
		PlayerDataApi.register(DATA_STORAGE);
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			for (Item item : Registry.ITEM) {
				searchItems.put(item.getName().getString().toLowerCase(), item);
			}
		});
	}
}
