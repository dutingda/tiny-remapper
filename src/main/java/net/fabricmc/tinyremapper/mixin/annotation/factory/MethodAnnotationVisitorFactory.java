package net.fabricmc.tinyremapper.mixin.annotation.factory;

import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.mixin.Constant.Annotation;
import net.fabricmc.tinyremapper.mixin.Constant.AnnotationType;
import net.fabricmc.tinyremapper.mixin.annotation.AccessorAnnotationVisitor;
import net.fabricmc.tinyremapper.mixin.annotation.InjectAnnotationVisitor;
import net.fabricmc.tinyremapper.mixin.annotation.InvokerAnnotationVisitor;
import net.fabricmc.tinyremapper.mixin.annotation.OverwriteAnnotationVisitor;
import net.fabricmc.tinyremapper.mixin.annotation.ShadowAnnotationVisitor;
import net.fabricmc.tinyremapper.mixin.util.IMappingHolder;

public class MethodAnnotationVisitorFactory {
	private final DataHolder data;

	public MethodAnnotationVisitorFactory(Remapper remapper, AnnotationVisitor av, IMappingHolder mapping,
										String className, String memberName, String memberDescriptor) {
		this.data = new DataHolder(remapper, av, mapping,
				AnnotationType.METHOD, className, memberName, memberDescriptor);
	}

	public AnnotationVisitor shadow(boolean remapIn, List<String> targetsIn) {
		return new ShadowAnnotationVisitor(data, remapIn, targetsIn);
	}

	public AnnotationVisitor overwrite(boolean remapIn, List<String> targetsIn) {
		return new OverwriteAnnotationVisitor(data, remapIn, targetsIn);
	}

	public AnnotationVisitor accessor(boolean remapIn, List<String> targetsIn) {
		return new AccessorAnnotationVisitor(data, remapIn, targetsIn);
	}

	public AnnotationVisitor invoker(boolean remapIn, List<String> targetsIn) {
		return new InvokerAnnotationVisitor(data, remapIn, targetsIn);
	}

	public AnnotationVisitor inject(boolean remapIn, List<String> targetsIn) {
		return new InjectAnnotationVisitor(Annotation.INJECT.get(), data, remapIn, targetsIn);
	}
}
