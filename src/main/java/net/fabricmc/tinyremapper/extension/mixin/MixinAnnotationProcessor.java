package net.fabricmc.tinyremapper.extension.mixin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.api.Classpath;
import net.fabricmc.tinyremapper.extension.mixin.common.MixinExtensionClassVisitor;
import net.fabricmc.tinyremapper.extension.mixin.data.IMappingHolder;
import net.fabricmc.tinyremapper.extension.mixin.data.SimpleMappingHolder;

public class MixinAnnotationProcessor {
	private final IMappingHolder mapping = new SimpleMappingHolder();

	public ClassVisitor getPreVisitor(ClassVisitor cv, Remapper remapper, Classpath classpath) {
		return new MixinExtensionClassVisitor(cv, remapper, mapping, classpath);
	}

	public IMappingProvider getMapping() {
		return mapping;
	}
}

