package net.fabricmc.tinyremapper.mixin.util;

import java.util.List;

public class Logger {
	public static void warn(String message) {
		System.out.println("[WARN] [MixinAnnotationRemapper] " + message);
	}

	public static void warn(String annotation, String target, String className) {
		warn(annotation + " remap fail for target " + target + " on mixin class " + className);
	}

	public static void warn(String annotation, List<String> targets, String className, String memberName) {
		warn(annotation + " remap fail for one of targets " + targets.toString()
				+ " on member " + memberName + " of class " + className);
	}
}
