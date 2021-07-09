package net.fabricmc.tinyremapper.extension.mixin.annotation.common;

import java.util.List;
import java.util.Locale;

import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationType;
import net.fabricmc.tinyremapper.extension.mixin.data.IMappingHolder;

public class AnnotationVisitorCommonUtil {
	public static String removeCamelPrefix(String prefix, String str) {
		if (str.startsWith(prefix)) {
			str = str.substring(prefix.length());

			return str.isEmpty() ? str
					: str.substring(0, 1).toLowerCase(Locale.ROOT) + str.substring(1);
		}

		throw new RuntimeException(prefix + " is not the prefix of " + str);
	}

	public static String addCamelPrefix(String prefix, String str) {
		return str.isEmpty() ? prefix
				: prefix + str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1);
	}

	public static void emitMapping(Remapper remapper, AnnotationType type,
								IMappingHolder mapping, String owner,
								String srcName, String srcDesc, String dstName) {
		if (srcName.equals(dstName)) {
			throw new RuntimeException("srcName and dstName are the same, " + srcName);
		} else {
			// srcDesc need to be remapped, at it may be remapped by tiny-remapper in
			// this pass.

			if (type.equals(AnnotationType.METHOD)) {
				String desc = remapper.mapMethodDesc(srcDesc);
				mapping.putMethod(owner, srcName, desc, dstName);
			} else if (type.equals(AnnotationType.FIELD)) {
				String desc = remapper.mapDesc(srcDesc);
				mapping.putField(owner, srcName, desc, dstName);
			} else {
				throw new RuntimeException("Encounter non-member type " + type.name());
			}
		}
	}

	public static String remapMember(Remapper remapper, AnnotationType type,
									List<String> targets, String srcName, String srcDesc) {
		String dstName = srcName;

		for (String target : targets) {
			if (type.equals(AnnotationType.METHOD)) {
				dstName = remapper.mapMethodName(target, srcName, srcDesc);
			} else if (type.equals(AnnotationType.FIELD)) {
				dstName = remapper.mapFieldName(target, srcName, srcDesc);
			} else {
				throw new RuntimeException("Encounter non-member type " + type.name());
			}

			if (!srcName.equals(dstName)) {
				return dstName;
			}
		}

		return dstName;
	}
}
