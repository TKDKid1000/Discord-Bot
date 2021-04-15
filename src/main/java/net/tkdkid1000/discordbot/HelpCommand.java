package net.tkdkid1000.discordbot;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class HelpCommand extends Command {

	public HelpCommand(JDA jda, String name, String description) {
		super(jda, name, 0, "", null, description);
	}

	@Override
	public void execute(Member member, String[] args, Message msg) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Commands");
		embed.setColor(Color.BLUE);
		embed.setDescription("Here are all the commands that can be used by me!\n"
				+ "My prefix is **"+Command.getPrefix()+"**");
		Command.commands.forEach(cmd -> {
			embed.addField(cmd.getName(), cmd.getDescription(), true);
		});
		embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
		msg.getChannel().sendMessage(embed.build()).queue();
	}
}
