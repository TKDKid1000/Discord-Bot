package net.tkdkid1000.discordbot;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.tkdkid1000.discordbot.utils.StringUtil;

public class TagCommand extends Command {

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public TagCommand(JDA jda, String name, long delay, String delaymsg, Collection<Permission> perms,
			String description) {
		super(jda, name, delay, delaymsg, perms, description);
	}

	@Override
	public void execute(Member member, String[] args, Message msg) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> tags = gson.fromJson(Files.newBufferedReader(Paths.get("./tags.json")), Map.class);
			if (args.length == 0) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("Tag Custom Commands!");
				embed.setColor(Color.BLUE);
				embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
				embed.setDescription("This is the tag custom command system. It allows creation of mini custom commands using text.\n**Usage**\nRun these commands with `" + Config.config.get("tagprefix") + "<command>`.\n**Commands**\n ./tag <command> <args>");
				embed.addField("add <title> <description>", "Creates a tag command with the specified description.", true);
				embed.addField("remove <title>", "Removes a tag command.", true);
				embed.addField("list", "Lists all current commands.", true);
				msg.reply(embed.build()).queue();
			} else {
				if (args[0].equalsIgnoreCase("add")) {
					if (args.length >= 3) {
						String title = args[1];
						String description = StringUtil.join(Arrays.asList(args).subList(2, args.length), " ");
						EmbedBuilder embed = new EmbedBuilder();
						embed.setTitle("Tag Custom Commands!");
						embed.setColor(Color.BLUE);
						embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
						embed.setDescription("Added tag " + title + " with content " + description + ".");
						msg.reply(embed.build()).queue();
						tags.put(title, description);
						Writer writer = new FileWriter("./tags.json");
						gson.toJson(tags, writer);
						writer.flush();
						writer.close();
					} else {
						msg.reply("Please provide a tag title and description!").queue();
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (args.length >= 2) {
						String title = args[1];
						EmbedBuilder embed = new EmbedBuilder();
						embed.setTitle("Tag Custom Commands!");
						embed.setColor(Color.BLUE);
						embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
						if (tags.containsKey(title)) {
							embed.setDescription("Removed tag "+ title + ".");
							tags.remove(title);
						} else {
							embed.setDescription("That tag does not exist!");
						}
						msg.reply(embed.build()).queue();
						Writer writer = new FileWriter("./tags.json");
						gson.toJson(tags, writer);
						writer.flush();
						writer.close();
					} else {
						msg.reply("Please provide a tag title!").queue();
					}
				} else if (args[0].equalsIgnoreCase("list")) {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle("Tag Custom Commands!");
					embed.setColor(Color.BLUE);
					embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
					embed.setDescription("Here is a list of the current tags, the are run with `" + Config.config.get("tagprefix") + "<command>.`");
					tags.forEach((command, value) -> {
						embed.addField("**"+command+"**", value, true);
					});
					msg.reply(embed.build()).queue();
				} else {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle("Tag Custom Commands!");
					embed.setColor(Color.BLUE);
					embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
					embed.setDescription("This is the tag custom command system. It allows creation of mini custom commands using text.\n**Commands**\n ./tag <command> <args>");
					embed.addBlankField(true);
					embed.addField("add <title> <description>", "Creates a tag command with the specified description.", true);
					embed.addField("remove <title>", "Removes a tag command.", true);
					msg.reply(new MessageBuilder().append("Unknown subcommand!").setEmbed(embed.build()).build()).queue();
				}
			}
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("A Fatal Error Occurred!");
			embed.setColor(Color.RED);
			embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
			embed.setDescription("An error occurred when loading the tags!\nPlease report this error to https://github.com/TKDKid1000/Discord-Bot/issues with the content of\n```"+e.getMessage()+"```");
			msg.reply(embed.build()).queue();
		}
	}
}
