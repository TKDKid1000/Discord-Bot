package net.tkdkid1000.discordbot;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TagListener extends ListenerAdapter {

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private String prefix;

	public TagListener(JDA jda, String prefix) {
		this.prefix = prefix;
		jda.addEventListener(this);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot() || event.isWebhookMessage()) return;
		if (event.getMessage().getContentRaw().strip().startsWith(prefix)) {
			Logger logger = LoggerFactory.getLogger(TagListener.class);
			logger.info(event.getMember().getEffectiveName() + " executed tag " + event.getMessage().getContentRaw());
			try {
				@SuppressWarnings("unchecked")
				Map<String, String> tags = gson.fromJson(Files.newBufferedReader(Paths.get("./tags.json")), Map.class);
				String cmd = event.getMessage().getContentRaw().substring(prefix.length(), event.getMessage().getContentRaw().length());
				EmbedBuilder embed = new EmbedBuilder();
				if (cmd.strip().equals("")) {
					embed.setTitle("Tag Custom Commands!");
					embed.setColor(Color.BLUE);
					embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
					embed.setDescription("This is the tag custom command system. It allows creation of mini custom commands using text.\n**Commands**\n ./tag <command> <args>");
					embed.addBlankField(true);
					embed.addField("add <title> <description>", "Creates a tag command with the specified description.", true);
					embed.addField("remove <title>", "Removes a tag command.", true);
					event.getMessage().reply(embed.build()).queue();
					return;
				}
				embed.setColor(Color.BLUE);
				embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
				if (tags.containsKey(cmd)) {
					embed.setDescription(tags.get(cmd));
				} else {
					embed.setDescription("That tag does not exist!");
				}
				event.getMessage().reply(embed.build()).queue();
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("A Fatal Error Occurred!");
				embed.setColor(Color.RED);
				embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
				embed.setDescription("An error occurred when loading the tags!\nPlease report this error to https://github.com/TKDKid1000/Discord-Bot/issues with the content of\n```"+e.getMessage()+"```");
				event.getMessage().reply(embed.build()).queue();
			}
		}
	}
}
