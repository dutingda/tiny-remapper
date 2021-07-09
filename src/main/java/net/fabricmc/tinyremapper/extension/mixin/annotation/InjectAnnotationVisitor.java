package net.fabricmc.tinyremapper.extension.mixin.annotation;

import java.util.List;

import org.objectweb.asm.AnnotationVisitor;

import net.fabricmc.tinyremapper.extension.mixin.Constant;
import net.fabricmc.tinyremapper.extension.mixin.annotation.common.BoolRemapAnnotationVisitor;
import net.fabricmc.tinyremapper.extension.mixin.annotation.factory.DataHolder;

public class InjectAnnotationVisitor extends BoolRemapAnnotationVisitor {
	public InjectAnnotationVisitor(String annotationDesc, DataHolder data, boolean remap, List<String> targets) {
		super(annotationDesc, remap, data.delegate);
	}

	@Override
	public AnnotationVisitor next(AnnotationVisitor delegate, boolean remap) {
		return new AnnotationVisitor(Constant.ASM_VERSION, delegate) {
			@Override
			public AnnotationVisitor visitArray(String name) {
				System.out.println("name = " + name);
				return new AnnotationVisitor(Constant.ASM_VERSION, super.visitArray(name)) {
					@Override
					public void visit(String name, Object value) {
						System.out.println("value = " + value);
						super.visit(name, value);
					}
				};
			}
		};
	}
}
