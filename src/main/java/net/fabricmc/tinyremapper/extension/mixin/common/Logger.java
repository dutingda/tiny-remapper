package net.fabricmc.tinyremapper.extension.mixin.common;

import java.util.List;

public final class Logger {
	public enum Level {
		WARN, ERROR
	}

	public static Level level = Level.WARN;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static void error(String message) {
		if (level.equals(Level.WARN) || level.equals(Level.ERROR)) {
			System.out.println(ANSI_RED + "[ERROR]" + ANSI_RESET + " [Mixin] " + message);
		}
	}

	public static void warn(String message) {
		if (level.equals(Level.WARN)) {
			System.out.println(ANSI_YELLOW + "[WARN]" + ANSI_RESET + " [Mixin] " + message);
		}
	}

	public static void warn(String annotation, String target, String className) {
		warn(ANSI_RED + annotation + ANSI_RESET
				+ " remap fail for target " + target + " on mixin class " + className);
	}

	public static void warn(String annotation, List<String> targets, String className, String memberName) {
		warn(ANSI_RED + annotation + ANSI_RESET
				+ " remap fail for one of targets " + targets.toString()
				+ " on member " + memberName + " inside mixin class " + className);
	}
}
