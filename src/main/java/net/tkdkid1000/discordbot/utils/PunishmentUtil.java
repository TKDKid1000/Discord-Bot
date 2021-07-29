package net.tkdkid1000.discordbot.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.dv8tion.jda.api.entities.Member;

public class PunishmentUtil {
	
	@SuppressWarnings("unchecked")
	public static void punish(Member member, Member issuer, String end, String type) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Duration dur = DurationParser.parseDuration(end);
		try {
			Map<String, Map<String, Map<String, Object>>> punishments = gson.fromJson(new FileReader("punishments.json"), Map.class);
			Map<String, Object> userdata = new HashMap<String, Object>();
			userdata.put("date", dur.toSeconds());
			userdata.put("issuer", issuer.getId());
			punishments.get(type).get(member.getGuild().getId()).put(member.getId(), userdata);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
