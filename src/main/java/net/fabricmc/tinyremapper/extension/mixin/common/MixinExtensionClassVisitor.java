package net.fabricmc.tinyremapper.extension.mixin.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.api.Classpath;
import net.fabricmc.tinyremapper.extension.mixin.annotation.ImplementsAnnotationVisitor;
import net.fabricmc.tinyremapper.extension.mixin.annotation.ImplementsAnnotationVisitor.Interface;
import net.fabricmc.tinyremapper.extension.mixin.data.Annotation;
import net.fabricmc.tinyremapper.extension.mixin.data.Constant;
import net.fabricmc.tinyremapper.extension.mixin.data.IMappingHolder;
import net.fabricmc.tinyremapper.extension.mixin.factory.ClassAnnotationVisitorFactory;
import net.fabricmc.tinyremapper.extension.mixin.factory.FieldAnnotationVisitorFactory;
import net.fabricmc.tinyremapper.extension.mixin.factory.MethodAnnotationVisitorFactory;

public class MixinExtensionClassVisitor extends ClassVisitor {
	private final Remapper remapper;
	private final Classpath classpath;
	private final IMappingHolder mapping;

	private String className;

	// @Mixin
	private final AtomicBoolean remap = new AtomicBoolean();
	private final List<String> targets = new ArrayList<>();

	// @Implements
	private final List<Interface> interfaces = new ArrayList<>();

	public MixinExtensionClassVisitor(ClassVisitor delegate, Remapper remapper,
									IMappingHolder mapping, Classpath classpath) {
		super(Constant.ASM_VERSION, delegate);
		this.remapper = Objects.requireNonNull(remapper);
		this.classpath = Objects.requireNonNull(classpath);
		this.mapping = Objects.requireNonNull(mapping);
	}

	/**
	 * This is called before visitAnnotation.
	 */
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.className = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

	/**
	 * This is called before visitMethod & visitField.
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible);
		ClassAnnotationVisitorFactory factory = new ClassAnnotationVisitorFactory(
				remapper, classpath, annotationVisitor, mapping, className);

		if (Annotation.MIXIN.get().equals(descriptor)) {
			annotationVisitor = factory.mixin(remap, targets);
		} else if (Annotation.IMPLEMENTS.get().equals(descriptor)) {
			annotationVisitor = factory._implements(interfaces);
		}

		return annotationVisitor;
	}

	@Override
	public FieldVisitor visitField(int access, String fieldName, String fieldDescriptor, String signature, Object value) {
		FieldVisitor fieldVisitor = super.visitField(access, fieldName, fieldDescriptor, signature, value);

		if (targets.isEmpty()) {
			return fieldVisitor;
		} else {
			return new FieldVisitor(Constant.ASM_VERSION, fieldVisitor) {
				@Override
				public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
					AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible);
					FieldAnnotationVisitorFactory factory = new FieldAnnotationVisitorFactory(
							remapper, classpath, annotationVisitor, mapping, className, fieldName, fieldDescriptor);

					if (Annotation.SHADOW.get().equals(descriptor)) {
						annotationVisitor = factory.shadow(remap.get(), targets);
					}

					return annotationVisitor;
				}
			};
		}
	}

	@Override
	public MethodVisitor visitMethod(int access, String methodName, String methodDescriptor, String signature, String[] exceptions) {
		MethodVisitor methodVisitor = super.visitMethod(access, methodName, methodDescriptor, signature, exceptions);

		ImplementsAnnotationVisitor.MethodVisitorUtil(
				remapper, interfaces, mapping, className, methodName, methodDescriptor);

		if (targets.isEmpty()) {
			return methodVisitor;
		} else {
			return new MethodVisitor(Constant.ASM_VERSION, methodVisitor) {
				@Override
				public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
					AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible);
					MethodAnnotationVisitorFactory factory = new MethodAnnotationVisitorFactory(
							remapper, classpath, annotationVisitor, mapping, className, methodName, methodDescriptor);

					if (Annotation.SHADOW.get().equals(descriptor)) {
						annotationVisitor = factory.shadow(remap.get(), targets);
					} else if (Annotation.OVERWRITE.get().equals(descriptor)) {
						annotationVisitor = factory.overwrite(remap.get(), targets);
					} else if (Annotation.ACCESSOR.get().equals(descriptor)) {
						annotationVisitor = factory.accessor(remap.get(), targets);
					} else if (Annotation.INVOKER.get().equals(descriptor)) {
						annotationVisitor = factory.invoker(remap.get(), targets);
					} else if (Annotation.INJECT.get().equals(descriptor)) {
						annotationVisitor = factory.inject(remap.get(), targets);
					}

					return annotationVisitor;
				}
			};
		}
	}
}
