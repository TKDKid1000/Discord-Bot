package net.tkdkid1000.discordbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter {

	private static String prefix = "";
	public static List<Command> commands = new ArrayList<Command>();
	private String name;
	private long delay;
	private Map<Member, Long> delaymap;
	private String delaymsg;
	private Collection<Permission> perms;
	private String description;

	public Command(JDA jda, String name, long delay, String delaymsg, Collection<Permission> perms, String description) {
		this.name = name;
		this.delay = delay;
		this.delaymap = new HashMap<Member, Long>();
		this.delaymsg = delaymsg;
		this.perms = perms;
		this.description = description;
		commands.add(this);
		jda.addEventListener(this);
		new Timer().scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				Map<Member, Long> delayiter = new HashMap<Member, Long>();
				delayiter.putAll(delaymap);
				delayiter.forEach((user, delay) -> {
					delaymap.replace(user, delaymap.get(user)-1);
					if (delay <= 0) {
						delaymap.remove(user);
					}
				});
			}
			
		}, 1, 1);
	}
	
	public static Command getCommand(String name) {
		Command command = null;
		for (Command cmd : commands) {
			if (cmd.getName().equals(name)) {
				command = cmd;
			}
		}
		return command;
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	public static void setPrefix(String pref) {
		prefix = pref;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public Collection<Permission> getPermissions() {
		return perms;
	}
	
	public abstract void execute(Member member, String[] args, Message msg);
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot() || event.isWebhookMessage()) return;
		if (!delaymap.containsKey(event.getMember())) {
			String msg = event.getMessage().getContentRaw();
			Member member = event.getMember();
			delaymap.put(member, delay);
			if (msg.strip().startsWith(Command.prefix+name)) {
				if (perms == null || member.getPermissions().containsAll(perms)) {
					Logger logger = LoggerFactory.getLogger(Command.class);
					logger.info(member.getEffectiveName() + " executed command " + msg);
					String[] args = Arrays.copyOfRange(msg.split(" "), 1, msg.split(" ").length);
					execute(member, args, event.getMessage());
				}
			}
		} else {
			event.getMessage().reply(delaymsg.replace("{delay}", ""+(delaymap.get(event.getMember())/1000))).queue();
		}
	}
}
