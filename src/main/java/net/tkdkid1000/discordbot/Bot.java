package net.tkdkid1000.discordbot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

public class Bot {

	JDA jda;
	
	public static void main(String[] args) {
		try {
			new Bot().run(args);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	public void run(String[] args) throws LoginException {
		JDABuilder builder = JDABuilder.createDefault("ODIwNzQwODk5NDY1OTg2MTM4.YE5khw.jzaxn1JWSCLjprjJ9p3wRDT6yac");
		builder.setActivity(Activity.watching("https://twitch.tv/armi_s4"));
		this.jda = builder.build();
		Command.setPrefix("./");
		new Command(jda, "hello", 1000, "That command is on delay for {delay} seconds!", Arrays.asList(new Permission[]{Permission.MESSAGE_MANAGE}), "Says hello to you!") {

			@Override
			public void execute(Member member, String[] args, Message msg) {
				msg.getChannel().sendMessage("Hello, " + member.getAsMention() + "!").queue();
			}
			
		};
		new Command(jda, "about", 0, "", null, "Shows some info about me.") {

			@Override
			public void execute(Member member, String[] args, Message msg) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("About");
				embed.setColor(Color.BLUE);
				try {
					URL url = new URL("https://raw.githubusercontent.com/TKDKid1000/TKDKid1000/main/README.md");
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					BufferedReader in = new BufferedReader(
							  new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						content.append(inputLine);
					}
					in.close();
					embed.setDescription(content.toString().replace("â€", "\n ").replace("#", "").replace("™", "").replace("I\n", "I"));
				} catch (IOException e1) {
					embed.setDescription("An error occured loading the http request!");
				}
				embed.setAuthor("TKDKid1000", "https://github.com/TKDKid1000", "https://avatars.githubusercontent.com/u/69736165?v=4");
				msg.getChannel().sendMessage(embed.build()).queue();
			}
			
		};
		new Command(jda, "delete", 0, "", Arrays.asList(new Permission[] {Permission.MESSAGE_MANAGE}), "Deletes a certain amount of messages from a channel.") {

			@Override
			public void execute(Member member, String[] args, Message msg) {
				TextChannel channel = msg.getTextChannel();
				if (args.length == 0) {
					msg.reply("Please specify a message count!").queue();
				} else {
					try {
						if (Integer.parseInt(args[0])+1>100) {
							msg.reply("You can't delete more than 99 messages in 1 queue!").queue();
						} else {
							new MessageHistory(msg.getChannel()).retrievePast(Integer.parseInt(args[0])+1).queue(msgs -> {
								channel.purgeMessages(msgs);
							});
						}
					} catch (NumberFormatException e) {
						msg.reply("That isn't a number!").queue();
					}
				}
			}
			
		};
		new HelpCommand(jda, "help", "Shows this message");
		new AntiSwear(jda, new String[]{"bad", "word", "here"});
	}
}
