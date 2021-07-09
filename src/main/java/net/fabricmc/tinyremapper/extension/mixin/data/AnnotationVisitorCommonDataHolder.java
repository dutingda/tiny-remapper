package net.fabricmc.tinyremapper.extension.mixin.data;

import java.util.Objects;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.api.Classpath;

public class AnnotationVisitorCommonDataHolder {
	public final Remapper remapper;
	public final Classpath classpath;
	public final AnnotationVisitor delegate;
	public final IMappingHolder mapping;

	public final AnnotationType type;
	public final String className;
	public final String memberName;
	public final String memberDescriptor;

	public AnnotationVisitorCommonDataHolder(Remapper remapper, Classpath classpath,
			AnnotationVisitor delegate, IMappingHolder mapping, AnnotationType type,
			String className, String memberName, String memberDescriptor) {
		this.remapper = Objects.requireNonNull(remapper);
		this.classpath = classpath;
		this.delegate = Objects.requireNonNull(delegate);
		this.mapping = Objects.requireNonNull(mapping);

		this.type = Objects.requireNonNull(type);
		this.className = Objects.requireNonNull(className);
		this.memberName = memberName;
		this.memberDescriptor = memberDescriptor;
	}
}
