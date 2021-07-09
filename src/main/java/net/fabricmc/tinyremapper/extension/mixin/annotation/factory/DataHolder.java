package net.fabricmc.tinyremapper.extension.mixin.annotation.factory;

import java.util.Objects;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.extension.mixin.Constant.AnnotationType;
import net.fabricmc.tinyremapper.extension.mixin.util.IMappingHolder;

public class DataHolder {
	public final Remapper remapper;
	public final AnnotationVisitor delegate;
	public final IMappingHolder mapping;

	public final AnnotationType type;
	public final String className;
	public final String memberName;
	public final String memberDescriptor;

	DataHolder(Remapper remapper, AnnotationVisitor delegate, IMappingHolder mapping,
			AnnotationType type, String className, String memberName, String memberDescriptor) {
		this.remapper = Objects.requireNonNull(remapper);
		this.delegate = Objects.requireNonNull(delegate);
		this.mapping = Objects.requireNonNull(mapping);

		this.type = Objects.requireNonNull(type);
		this.className = Objects.requireNonNull(className);
		this.memberName = memberName;
		this.memberDescriptor = memberDescriptor;
	}
}
