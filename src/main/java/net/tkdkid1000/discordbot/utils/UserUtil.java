package net.tkdkid1000.discordbot.utils;

import net.dv8tion.jda.api.entities.User;

public class UserUtil {

	public static User getUserFromMention(String mention) {
		if (mention.startsWith("<@!") && mention.endsWith(">")) {
			mention = mention.substring(0, mention.length() - 1);
			mention = mention.substring(3);
			User user = User.fromId(mention);
			if (user != null) {
				return user;
			}
		}
		return null;
	}
}
