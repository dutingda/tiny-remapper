package net.fabricmc.tinyremapper.mixin.annotation.factory;

import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.mixin.Constant.AnnotationType;
import net.fabricmc.tinyremapper.mixin.annotation.ShadowAnnotationVisitor;
import net.fabricmc.tinyremapper.mixin.util.IMappingHolder;

public class FieldAnnotationVisitorFactory {
	private final DataHolder data;

	public FieldAnnotationVisitorFactory(Remapper remapper, AnnotationVisitor av, IMappingHolder mapping,
										String className, String memberName, String memberDescriptor) {
		this.data = new DataHolder(remapper, av, mapping,
				AnnotationType.FIELD, className, memberName, memberDescriptor);
	}

	public AnnotationVisitor shadow(boolean remapIn, List<String> targetsIn) {
		return new ShadowAnnotationVisitor(data, remapIn, targetsIn);
	}
}
