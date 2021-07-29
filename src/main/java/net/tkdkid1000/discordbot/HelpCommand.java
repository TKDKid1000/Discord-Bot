package net.tkdkid1000.discordbot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class HelpCommand extends Command {

	public HelpCommand(JDA jda, String name, String description) {
		super(jda, name, 0, "", null, description);
	}

	@Override
	public void execute(Member member, String[] args, Message msg) {
		EmbedBuilder embed = new EmbedBuilder();
		if (args.length == 0) {
			embed.setTitle("Commands");
			embed.setColor(Color.BLUE);
			embed.setDescription("Here are all the commands that can be used by me!\n"
					+ "My prefix is **"+Command.getPrefix()+"**");
			Command.commands.forEach(cmd -> {
				embed.addField(cmd.getName(), cmd.getDescription(), true);
			});
		} else {
			List<String> commandNames = new ArrayList<String>();
			Command.commands.forEach(cmd -> {
				commandNames.add(cmd.getName());
			});
			if (commandNames.contains(args[0])) {
				Command cmd = Command.getCommand(args[0]);
				embed.setTitle("Commands - " + cmd.getName());
				embed.setColor(Color.BLUE);
				embed.setDescription(cmd.getDescription());
				embed.addField("Delay", (cmd.getDelay()/1000)+"s", true);
				String permString = "";
				if (cmd.getPermissions() != null) {
					for (Permission perm : cmd.getPermissions()) {
						permString += perm.toString().toLowerCase().replace("_", " ") + ", ";
					}
					permString = permString.substring(0, permString.length()-2);
				} else {
					permString = "none";
				}
				embed.addField("Permissions", permString, true);
			} else {
				embed.setTitle("Command Not Found");
				embed.setColor(Color.RED);
				embed.setDescription("The specified command does not exist, so I can't show help on it.");
			}
		}
		embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
		msg.getChannel().sendMessage(embed.build()).queue();
	}
}
