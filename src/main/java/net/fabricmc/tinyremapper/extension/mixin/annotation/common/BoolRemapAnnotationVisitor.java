package net.fabricmc.tinyremapper.extension.mixin.annotation.common;

import java.util.Objects;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.tree.AnnotationNode;

import net.fabricmc.tinyremapper.extension.mixin.data.Constant;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationElement;

/**
 * Common super class for any annotation require to read {@code remap} in
 * the first pass, and process at second pass.
 */
public abstract class BoolRemapAnnotationVisitor extends AnnotationNode {
	private boolean remap;
	private final AnnotationVisitor delegate;

	public BoolRemapAnnotationVisitor(String annotationDesc, boolean defaultRemap,
									AnnotationVisitor delegate) {
		super(Constant.ASM_VERSION, annotationDesc);
		this.remap = defaultRemap;
		this.delegate = delegate;
	}

	/**
	 * Construct a {@code AnnotationVisitor} used in second pass.
	 * @param delegate the {@code AnnotationVisitor} that new one must delegate to.
	 * @param remap the {@code remap} value read in the first pass.
	 * @return the new {@code AnnotationVisitor}.
	 */
	public abstract AnnotationVisitor next(AnnotationVisitor delegate, boolean remap);

	@Override
	public void visit(String name, Object value) {
		if (name.equals(AnnotationElement.REMAP.get())) {
			remap = Objects.requireNonNull((Boolean) value);
		}

		super.visit(name, value);
	}

	@Override
	public void visitEnd() {
		this.accept(Objects.requireNonNull(next(this.delegate, remap)));

		super.visitEnd();
	}
}
