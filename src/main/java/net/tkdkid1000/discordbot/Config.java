package net.tkdkid1000.discordbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Config {
	static Map<String, String> config;
	static {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> config = new Gson().fromJson(Files.newBufferedReader(Paths.get("./config.json")), Map.class);
			Config.config = config;
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
}
