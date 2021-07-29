package net.tkdkid1000.discordbot;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class PunishmentTimer extends TimerTask {

	private Gson gson;
	private JDA jda;
	
	public PunishmentTimer(JDA jda) {
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.jda = jda;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			Map<String, Object> punishments = gson.fromJson(new FileReader("punishments.json"), Map.class);
			Map<String, Map<String, Object>> mutes = (Map<String, Map<String, Object>>) punishments.get("mute");
			jda.getGuilds().forEach(guild -> {
				if (!mutes.containsKey(guild.getId())) {
					mutes.put(guild.getId(), new HashMap<String, Object>());
				}
			});
			mutes.forEach((guildid, guilddata) -> {
				Guild guild = jda.getGuildById(guildid);
				guilddata.forEach((memberid, userdata) -> {
					Member member = guild.getMemberById(memberid);
					if (Duration.ofMillis(System.currentTimeMillis()).compareTo(Duration.ofSeconds((long) ((Map<String, Object>) userdata).get("date"))) > 0) {
						if (guild.getRolesByName("muted", true).size() == 0) {
							guild.createRole()
							.setColor(Color.gray)
							.setName("Muted")
							.setPermissions(Permission.EMPTY_PERMISSIONS)
							.queue();
						}
						member.getRoles().remove(guild.getRolesByName("muted", true).get(0));
						member.getUser().openPrivateChannel()
						.flatMap(channel -> channel.sendMessage("Your mute on "+guild.getName()+" has expired!"))
						.queue();
						Map<String, Map<String, Object>> mutesedit = new HashMap<String, Map<String, Object>>();
						mutesedit.putAll(mutes);
						Map<String, Object> members = mutesedit.get(guildid);
						members.remove(memberid);
						punishments.replace("mutes", mutesedit);
					}
				});
			});
			Writer writer = new FileWriter("./punishments.json");
			gson.toJson(punishments, writer);
			writer.flush();
			writer.close();
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
