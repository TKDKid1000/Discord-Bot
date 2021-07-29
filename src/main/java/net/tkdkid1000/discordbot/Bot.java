package net.tkdkid1000.discordbot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.tkdkid1000.discordbot.utils.PunishmentUtil;
import net.tkdkid1000.discordbot.utils.StringUtil;
import net.tkdkid1000.discordbot.utils.UserUtil;

public class Bot {

	JDA jda;
	
	public static void main(String[] args) {
		new Bot().run(args);
	}
	
	public void run(String[] args) {
		String token = "";
		try {
			Scanner s = new Scanner(new File("token.txt"));
			while (s.hasNextLine()) {
				token += s.nextLine() + "\n";
			}
			s.close();
		} catch (FileNotFoundException e) {
			Logger log = LoggerFactory.getLogger("main");
			log.error("Provide a token.txt file!");
			log.error("echo token.txt > tokenhere");
			System.exit(1);
		}
		token = token.strip();
		JDABuilder builder = JDABuilder.createDefault(token);
		builder.setActivity(Activity.watching("https://github.com/TKDKid1000"));
		try {
			this.jda = builder.build();
		} catch (LoginException e) {
			Logger log = LoggerFactory.getLogger("main");
			log.error("That token is invalid! Please provide a valid one.");
			System.exit(1);
		}
		Command.setPrefix(Config.config.get("prefix"));
		new Command(jda, "hello", 1000, "That command is on delay for {delay} seconds!", null, "Says hello to you!") {

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
		new Command(jda, "kick", 0, "", Arrays.asList(new Permission[] {Permission.KICK_MEMBERS}), "Kicks a user from the server.") {

			@Override
			public void execute(Member member, String[] args, Message msg) {
				if (args.length == 0) {
					msg.reply("Please specify a user!").queue();
				} else if (args.length > 0) {
					if (UserUtil.getUserFromMention(args[0]) != null) {
						User user = UserUtil.getUserFromMention(args[0]);
						Member target = member.getGuild().getMember(user);
						if (target == null) {
							msg.reply("The specified user is a bot!").queue();
						}
						if (args.length > 1) {
							String reason = StringUtil.join(Arrays.asList(args).subList(1, args.length), " ");
							target.kick(reason).queue();
							msg.reply("Kicking " + user.getAsMention() + " from the server for " + reason + ".").queue();
						} else {
							target.kick().queue();
							msg.reply("Kicking " + user.getAsMention() + " from the server.").queue();
						}
					} else {
						msg.reply("That is not a valid user!").queue();
					}
				}
			}
			
		};
		new Command(jda, "mute", 0, "", Arrays.asList(new Permission[] {Permission.KICK_MEMBERS}), "Kicks a user from the server.") {

			@Override
			public void execute(Member member, String[] args, Message msg) {
				if (args.length == 0 || args.length == 1) {
					msg.reply("Please specify a user an a time!").queue();
				} else if (args.length > 1) {
					if (UserUtil.getUserFromMention(args[0]) != null) {
						User user = UserUtil.getUserFromMention(args[0]);
						msg.reply("Kicking " + user.getAsMention() + " from the server.").queue();
						Member target = member.getGuild().getMember(user);
						if (target == null) {
							msg.reply("The specified user is a bot!").queue();
						}
						try {
							String reason = StringUtil.join(Arrays.asList(args).subList(2, args.length), " ");
							PunishmentUtil.punish(target, member, args[1], "mute");
							if (member.getGuild().getRolesByName("muted", true).size() == 0) {
								member.getGuild().createRole()
								.setColor(Color.gray)
								.setName("Muted")
								.setPermissions(Permission.EMPTY_PERMISSIONS)
								.queue();
							}
							target.getRoles().add(member.getGuild().getRolesByName("muted", true).get(0));
							if (args.length == 2) {
								msg.reply("Muting " + target.getAsMention() + ".");
							} else {
								msg.reply("Muting " + target.getAsMention() + " for " + reason + ".");
							}
						} catch (IllegalArgumentException e) {
							msg.reply("That date is invalid!").queue();
						}
					} else {
						msg.reply("That is not a valid user!").queue();
					}
				}
			}
			
		};
		new HelpCommand(jda, "help", "Shows this message");
		new TagCommand(jda, "tag", 1000, "That command is on delay for {delay} seconds!", Arrays.asList(new Permission[] {Permission.MESSAGE_MANAGE}), "Tag custom command system!");
		new AntiSwear(jda, new String[]{});
		new TagListener(jda, Config.config.get("tagprefix"));
		new Timer().scheduleAtFixedRate(new PunishmentTimer(jda), 1000, 1000);
		new ChatLogger(jda, false, Config.config.get("prefix"), Config.config.get("tagprefix"));
	}
}
