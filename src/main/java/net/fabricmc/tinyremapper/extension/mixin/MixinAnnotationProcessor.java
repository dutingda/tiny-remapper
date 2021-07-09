package net.fabricmc.tinyremapper.extension.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.extension.mixin.data.Constant;
import net.fabricmc.tinyremapper.extension.mixin.data.IMappingHolder;
import net.fabricmc.tinyremapper.extension.mixin.data.SimpleMappingHolder;
import net.fabricmc.tinyremapper.extension.mixin.data.Annotation;
import net.fabricmc.tinyremapper.extension.mixin.annotation.ImplementsAnnotationVisitor;
import net.fabricmc.tinyremapper.extension.mixin.annotation.ImplementsAnnotationVisitor.Interface;
import net.fabricmc.tinyremapper.extension.mixin.factory.ClassAnnotationVisitorFactory;
import net.fabricmc.tinyremapper.extension.mixin.factory.FieldAnnotationVisitorFactory;
import net.fabricmc.tinyremapper.extension.mixin.factory.MethodAnnotationVisitorFactory;

public class MixinAnnotationProcessor {
	private final IMappingHolder mapping = new SimpleMappingHolder();

	public ClassVisitor getClassVisitor(ClassVisitor cv, Remapper remapper, String className) {
		return new ClassMixinAnnotationProcessor(cv, remapper, mapping, className);
	}

	public IMappingProvider getMapping() {
		return mapping;
	}
}

class ClassMixinAnnotationProcessor extends ClassVisitor {
	private final Remapper remapper;
	private final IMappingHolder mapping;
	private final String className;

	// @Mixin
	private final AtomicBoolean remap = new AtomicBoolean();
	private final List<String> targets = new ArrayList<>();

	// @Implements
	private final List<Interface> interfaces = new ArrayList<>();

	ClassMixinAnnotationProcessor(ClassVisitor classVisitor, Remapper remapper,
								IMappingHolder mapping, String className) {
		super(Constant.ASM_VERSION, classVisitor);
		this.remapper = remapper;
		this.mapping = mapping;

		this.className = className;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		AnnotationVisitor visitor = super.visitAnnotation(descriptor, visible);
		ClassAnnotationVisitorFactory factory = new ClassAnnotationVisitorFactory(
				remapper, visitor, mapping, className);

		if (Annotation.MIXIN.get().equals(descriptor)) {
			visitor = factory.mixin(remap, targets);
		} else if (Annotation.IMPLEMENTS.get().equals(descriptor)) {
			visitor = factory._implements(interfaces);
		}

		return visitor;
	}

	@Override
	public FieldVisitor visitField(int access, String fieldName, String fieldDescriptor, String signature, Object value) {
		FieldVisitor fv = super.visitField(access, fieldName, fieldDescriptor, signature, value);

		if (targets.isEmpty()) {
			return fv;
		} else {
			return new FieldVisitor(Constant.ASM_VERSION, fv) {
				@Override
				public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
					AnnotationVisitor visitor = super.visitAnnotation(descriptor, visible);
					FieldAnnotationVisitorFactory factory = new FieldAnnotationVisitorFactory(
							remapper, visitor, mapping, className, fieldName, fieldDescriptor);

					if (Annotation.SHADOW.get().equals(descriptor)) {
						visitor = factory.shadow(remap.get(), targets);
					}

					return visitor;
				}
			};
		}
	}

	@Override
	public MethodVisitor visitMethod(int access, String methodName, String methodDescriptor, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, methodName, methodDescriptor, signature, exceptions);

		ImplementsAnnotationVisitor.MethodVisitorUtil(
				remapper, interfaces, mapping, className, methodName, methodDescriptor);

		if (targets.isEmpty()) {
			return mv;
		} else {
			return new MethodVisitor(Constant.ASM_VERSION, mv) {
				@Override
				public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
					AnnotationVisitor visitor = super.visitAnnotation(descriptor, visible);
					MethodAnnotationVisitorFactory factory = new MethodAnnotationVisitorFactory(
							remapper, visitor, mapping, className, methodName, methodDescriptor);

					if (Annotation.SHADOW.get().equals(descriptor)) {
						visitor = factory.shadow(remap.get(), targets);
					} else if (Annotation.OVERWRITE.get().equals(descriptor)) {
						visitor = factory.overwrite(remap.get(), targets);
					} else if (Annotation.ACCESSOR.get().equals(descriptor)) {
						visitor = factory.accessor(remap.get(), targets);
					} else if (Annotation.INVOKER.get().equals(descriptor)) {
						visitor = factory.invoker(remap.get(), targets);
					} else if (Annotation.INJECT.get().equals(descriptor)) {
						visitor = factory.inject(remap.get(), targets);
					}

					return visitor;
				}
			};
		}
	}
}
