package net.fabricmc.tinyremapper.extension.mixin.annotation.factory;

import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.extension.mixin.data.Constant.Annotation;
import net.fabricmc.tinyremapper.extension.mixin.data.Constant.AnnotationType;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationVisitorCommonDataHolder;
import net.fabricmc.tinyremapper.extension.mixin.data.IMappingHolder;
import net.fabricmc.tinyremapper.extension.mixin.annotation.AccessorAnnotationVisitor;
import net.fabricmc.tinyremapper.extension.mixin.annotation.InjectAnnotationVisitor;
import net.fabricmc.tinyremapper.extension.mixin.annotation.InvokerAnnotationVisitor;
import net.fabricmc.tinyremapper.extension.mixin.annotation.OverwriteAnnotationVisitor;
import net.fabricmc.tinyremapper.extension.mixin.annotation.ShadowAnnotationVisitor;

public class MethodAnnotationVisitorFactory {
	private final AnnotationVisitorCommonDataHolder data;

	public MethodAnnotationVisitorFactory(Remapper remapper, AnnotationVisitor av, IMappingHolder mapping,
										String className, String memberName, String memberDescriptor) {
		this.data = new AnnotationVisitorCommonDataHolder(remapper, av, mapping,
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
