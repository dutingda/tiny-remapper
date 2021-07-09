package net.fabricmc.tinyremapper.extension.mixin.factory;

import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.api.Classpath;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationType;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationVisitorCommonDataHolder;
import net.fabricmc.tinyremapper.extension.mixin.data.IMappingHolder;
import net.fabricmc.tinyremapper.extension.mixin.annotation.ShadowAnnotationVisitor;

public class FieldAnnotationVisitorFactory {
	private final AnnotationVisitorCommonDataHolder data;

	public FieldAnnotationVisitorFactory(Remapper remapper, Classpath classpath,
			AnnotationVisitor delegate, IMappingHolder mapping,
			String className, String memberName, String memberDescriptor) {
		this.data = new AnnotationVisitorCommonDataHolder(
				remapper, classpath, delegate, mapping, AnnotationType.FIELD,
				className, memberName, memberDescriptor);
	}

	public AnnotationVisitor shadow(boolean remapIn, List<String> targetsIn) {
		return new ShadowAnnotationVisitor(data, remapIn, targetsIn);
	}
}
