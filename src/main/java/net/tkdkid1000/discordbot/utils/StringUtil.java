package net.tkdkid1000.discordbot.utils;

import java.util.List;

public class StringUtil {

	public static String join(List<String> list, String seperator) {
		String result = "";
		for (String word : list) {
			result += word + seperator;
		}
		return result.substring(0, result.length() - 1);
	}
}
