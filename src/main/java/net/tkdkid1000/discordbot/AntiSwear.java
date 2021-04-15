package net.tkdkid1000.discordbot;

import java.util.Arrays;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AntiSwear extends ListenerAdapter {

	private String[] blockedwords;

	public AntiSwear(JDA jda, String[] blockedwords) {
		jda.addEventListener(this);
		this.blockedwords = blockedwords;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String[] msg = event.getMessage().getContentRaw().split(" ");
		Arrays.asList(msg).forEach(word -> {
			Arrays.asList(blockedwords).forEach(blocked -> {
				if (word.equalsIgnoreCase(blocked)) {
					event.getMessage().delete().queue();
				}
			});
		});
	}
}
