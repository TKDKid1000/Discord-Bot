package net.tkdkid1000.discordbot;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatLogger extends ListenerAdapter {

	private JDA jda;
	private boolean logBots;
	private String[] deniedPrefixes;

	public ChatLogger(JDA jda, boolean logBots, String... deniedPrefixes) {
		this.setJda(jda);
		this.setLogBots(logBots);
		this.setDeniedPrefixes(deniedPrefixes);
		jda.addEventListener(this);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if ((event.getAuthor().isBot() || event.isWebhookMessage()) && !logBots) return;
		if (Arrays.stream(deniedPrefixes).anyMatch(event.getMessage().getContentRaw()::contains)) return;
		Logger logger = LoggerFactory.getLogger(ChatLogger.class);
		logger.info(event.getMember().getEffectiveName() + " said \"" + event.getMessage().getContentRaw() + "\" " + " in " + event.getChannel().getName());
	}

	/**
	 * @return the jda
	 */
	public JDA getJda() {
		return jda;
	}

	/**
	 * @param jda the jda to set
	 */
	public void setJda(JDA jda) {
		this.jda = jda;
	}

	/**
	 * @return the logBots
	 */
	public boolean isLogBots() {
		return logBots;
	}

	/**
	 * @param logBots the logBots to set
	 */
	public void setLogBots(boolean logBots) {
		this.logBots = logBots;
	}

	/**
	 * @return the deniedPrefixes
	 */
	public String[] getDeniedPrefixes() {
		return deniedPrefixes;
	}

	/**
	 * @param deniedPrefixes the deniedPrefixes to set
	 */
	public void setDeniedPrefixes(String[] deniedPrefixes) {
		this.deniedPrefixes = deniedPrefixes;
	}
}
